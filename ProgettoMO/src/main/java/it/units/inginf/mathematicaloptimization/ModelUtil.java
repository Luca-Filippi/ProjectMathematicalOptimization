package it.units.inginf.mathematicaloptimization;

import it.units.inginf.mathematicaloptimization.problementities.Line;
import it.units.inginf.mathematicaloptimization.problementities.Path;
import it.units.inginf.mathematicaloptimization.problementities.Station;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModelUtil {
    //Classe che contiene dei metodi statici per la generazione di elementi utili nella risoluzione di un modello
    private static Random rand = new Random();
    public static int[][][] generateB(List<Line> L, List<List<Station>> Srp, List<Path> P, List<List<Path>> Pi,
                                      List<Station> S, List<List<Path>> P0, List<List<Path>> Pr,
                                      List<List<Integer>> Tt, List<List<List<Integer>>> Tpi,
                                      List<List<List<List<Integer>>>> Tpit, int maxT, int[][] D, int[][] x, int c) {
        int[][][] b = new int[P.size()][S.size()][maxT+1];
        int numElements = P.size() * S.size() * (maxT+1);
        double[] objective = new double[numElements];
        for (int i = 0; i < numElements; i++) {
            objective[i] = 1.0;
        }
        // Crea la funzione obiettivo vuota (non influisce sulla soluzione)
        LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0.0);

        // Creazione del solver Simplex
        SimplexSolver solver = new SimplexSolver();

        // Vincoli per b >= 0
        NonNegativeConstraint nonNegativeConstraint = new NonNegativeConstraint(true);

        // Creazione dei vincoli per le somme
        List<LinearConstraint> constraints = new ArrayList<>();

        //Inserisci i vincoli
        //Vincolo che rappresenta g(x,b) ovvero l'equazione 9
        for(int l = 0; l < L.size(); l++) {
            int[] ti = new int[S.size()];
            for(int i = 0; i < S.size(); i++) {
                ti[i] = rand.nextInt(maxT+1);
                for(int t = ti[i]; t <= maxT; t++) {
                    double[] constraintValue = new double[numElements];
                    for(Path p : Pi.get(i)) {
                        int index = (p.getId()-1)*P.size()*(maxT+1) + i*(maxT+1) + t;
                        constraintValue[index] = 1.0;
                    }
                    constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ,
                            c*x[l][t-ti[i]]));
                }
            }
        }

        //Vincolo equazione 10
        for(int i = 0; i < S.size(); i++) {
            if(P0.get(i).isEmpty()) {
                for (Path p : P0.get(i)) {
                    for (Integer tau : Tpi.get(p.getId() - 1).get(S.get(i).getId()-1)) {
                        double[] constraintValue = new double[numElements];
                        int sum = 0;
                        for (Integer tTau : Tt.get(tau)) {
                            int index = (p.getId()-1)*P.size()*(maxT+1) + i*(maxT+1) + tTau;
                            constraintValue[index] = 1.0;
                            sum = sum + D[p.getId() - 1][tTau];
                        }
                        constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 0.0));
                    }
                }
            }
        }

        //Vincolo equazione 11
        for(Path p : P) {
            for(Station i : Srp.get(p.getId()-1)) {
                if(Pr.get(i.getId()-1).contains(p)) {
                    if(i.getId()-2 >= 0) {
                        double[] constraintValue = new double[numElements];
                        for(Integer tau : Tpi.get(p.getId()-1).get(i.getId()-1)) {
                            for(Integer tTau: Tt.get(tau)) {
                                int index = (p.getId()-1)*P.size()*(maxT+1) + (i.getId()-1)*(maxT+1) + tTau;
                                constraintValue[index] = 1.0;
                            }
                            for(Integer tTau: Tpit.get(p.getId()-1).get(i.getId()-1).get(tau)) {
                                int index = (p.getId()-1)*P.size()*(maxT+1) + (i.getId()-1)*(maxT+1) + tTau;
                                constraintValue[index] = -1.0;
                            }
                        }
                        constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 0.0));
                    }
                }
            }
        }

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

        try {
            PointValuePair solution = solver.optimize(f, constraintSet, nonNegativeConstraint);
            int index = 0;
            for(int p = 0; p < P.size(); p++) {
                for(int i = 0; i < S.size(); i++) {
                    for(int t = 0; t <= maxT; t++) {
                        //Forzo b ad assumere valori interi, dato che valori double non avrebbero senso
                        b[p][i][t] = (int) Math.round(solution.getPoint()[index]);
                        index++;
                    }
                }
            }
            return b;
        } catch (UnboundedSolutionException e) {
            System.err.println("Impossibile generare un vettore b: " + e.getMessage());
            return new int[][][]{{{-1}}};
        }
    }

    public static int[][][] generateN(List<List<Station>> Srp, List<Path> P, List<List<Path>> Pi,
                                      List<Station> S, List<List<Path>> P0, List<List<Path>> Pr,
                                      List<List<Integer>> Tt, List<List<List<Integer>>> Tpi,
                                      List<List<List<List<Integer>>>> Tpit, int maxT, int[][] D) {
        int[][][] n = new int[P.size()][S.size()][maxT+1];
        int numElements = P.size() * S.size() * (maxT+1);
        double[] objective = new double[numElements];
        for (int i = 0; i < numElements; i++) {
            objective[i] = 1.0;
        }

        // Crea la funzione obiettivo vuota (non influisce sulla soluzione)
        LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0.0);

        // Creazione del solver Simplex
        SimplexSolver solver = new SimplexSolver();

        // Vincoli per n >= 0
        NonNegativeConstraint nonNegativeConstraint = new NonNegativeConstraint(true);

        // Creazione dei vincoli per le somme
        List<LinearConstraint> constraints = new ArrayList<>();

        //Vincolo equazione 10
        for(int i = 0; i < S.size(); i++) {
            if(P0.get(i).isEmpty()) {
                for (Path p : P0.get(i)) {
                    for (Integer tau : Tpi.get(p.getId() - 1).get(S.get(i).getId()-1)) {
                        double[] constraintValue = new double[numElements];
                        int sum = 0;
                        for (Integer tTau : Tt.get(tau)) {
                            int index = (p.getId()-1)*P.size()*(maxT+1) + i*(maxT+1) + tTau;
                            constraintValue[index] = 1.0;
                            sum = sum + D[p.getId() - 1][tTau];
                        }
                        constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 0.0));
                    }
                }
            }
        }

        //Vincolo equazione 11
        for(Path p : P) {
            for(Station i : Srp.get(p.getId()-1)) {
                if(Pr.get(i.getId()-1).contains(p)) {
                    if(i.getId()-2 >= 0) {
                        double[] constraintValue = new double[numElements];
                        for(Integer tau : Tpi.get(p.getId()-1).get(i.getId()-1)) {
                            for(Integer tTau: Tt.get(tau)) {
                                int index = (p.getId()-1)*P.size()*(maxT+1) + (i.getId()-1)*(maxT+1) + tTau;
                                constraintValue[index] = 1.0;
                            }
                            for(Integer tTau: Tpit.get(p.getId()-1).get(i.getId()-1).get(tau)) {
                                int index = (p.getId()-1)*P.size()*(maxT+1) + (i.getId()-1)*(maxT+1) + tTau;
                                constraintValue[index] = -1.0;
                            }
                        }
                        constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 0.0));
                    }
                }
            }
        }

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

        try {
            PointValuePair solution = solver.optimize(f, constraintSet, nonNegativeConstraint);
            int index = 0;
            for(int p = 0; p < P.size(); p++) {
                for(int i = 0; i < S.size(); i++) {
                    for(int t = 0; t <= maxT; t++) {
                        //Forzo b ad assumere valori interi, dato che valori double non avrebbero senso
                        n[p][i][t] = (int) Math.round(solution.getPoint()[index]);
                        index++;
                    }
                }
            }
            return n;
        } catch (UnboundedSolutionException e) {
            System.err.println("Impossibile generare un vettore b: " + e.getMessage());
            return new int[][][]{{{-1}}};
        }
    }

}
