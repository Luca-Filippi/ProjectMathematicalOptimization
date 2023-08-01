package it.units.inginf.mathematicaloptimization;

import it.units.inginf.mathematicaloptimization.problementities.Line;
import it.units.inginf.mathematicaloptimization.problementities.Path;
import it.units.inginf.mathematicaloptimization.problementities.Station;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Model {
    //Classe che rappresenta il modello L-CTT
    private final int maxT; //valore massimo dell'insieme delle unità discrete dell'insieme T
    private  List<Line> L;//Insieme delle linee
    private List<Station> S;//Insieme delle stazioni
    private List<List<Station>> Sl = new ArrayList<>(); //Insieme delle stazioni sulla linea l
    private List<List<Station>> Srp = new ArrayList<>(); //Insieme delle stazioni intermedie sul percorso p
    private List<Path> P;//Insieme dei percorsi
    private int[] SC; //Capacità di carico di tutte le stazioni i
    private int[][] D; //numero di passeggeri sul percorso p al momento t
    private int[][] d; //domanda di passeggeri alla stazione i al momento t
    private int[][] t; //tempo del percorso che collega la stazione i alla precedente sul percorso p
    private final int c = 500; //Capacità di carico di ogni treno (uguale per tutti i treni)
    private List<List<Integer>> Tt = new ArrayList<>(); //Lista di tutti gli insiemi di unità discrete superiori al valore t
    private List<List<List<Integer>>> Tpi = new ArrayList<>();
    private List<List<List<List<Integer>>>> Tpit = new ArrayList<>();
    private List<List<Path>> P0; //Lista delle liste dei percorsi che iniziano a una certa stazione, per ogni stazione
    private List<List<Path>> Pr = new ArrayList<>(); //Lista delle liste dei percorsi che attraversano a una certa stazione, per ogni stazione
    private List<List<Path>> Pi = new ArrayList<>(); //Unione delle precedenti
    private  List<List<List<Station>>> Sil;
    private  List<List<Path>> Ps = new ArrayList<>();
    private  List<List<List<Path>>> Pji = new ArrayList<>();

    public Model(List<Station> S, List<Line> L, List<Path> P, List<List<Path>> P0, int maxT, int[][] D, int[][] d) {
        //Costruttore che imposta tutti i parametri necessari per il modello
        this.maxT = maxT;
        this.S = S;
        this.L = L;
        for(Line l : L) {
            Sl.add(Util.getSl(l.getLineNumber(),S));
        }
        this.P = P;
        this.SC = new int[S.size()];
        for(int i = 0; i <S.size(); i++){
            this.SC[i] = 50;
        }
        this.t = Util.generateMatrixtpi(this.P,this.S);
        for(int tau = 0; tau <= this.maxT; tau++) {
            this.Tt.add(Util.getTt(tau));
        }
        for(Path p : this.P) {
            this.Tpi.add(Util.getTpi(this.S,this.maxT,p,this.t));
            this.Tpit.add(Util.getTpit(this.maxT,this.S,p,this.t));
            this.Srp.add(Util.getSr(p,this.S));
        }
        this.P0 = P0;
        for(Station i : this.S) {
            List<Path> p0i = Util.getP0i(i,this.P);
            List<Path> pri = Util.getPri(i,this.P);
            List<Path> pii = Util.getPi(p0i,pri);
            this.Pr.add(pri);
            this.Pi.add(pii);
        }
        this.D = D;
        this.d = d;
        this.Sil = Util.getSil(this.L);
        for(Station i : S) {
            Ps.add(Util.getPsi(this.P,i,Pr.get(i.getId()-1)));
        }
        for(Station i : S) {
            List<List<Path>> support = new ArrayList<>();
            for (Station j : Sil.get(i.getLine() - 1).get(i.getId() - 1)) {
                support.add(Util.getPji(this.P0.get(j.getId() - 1), this.Pr.get(j.getId() - 1), this.Ps, i,
                        this.L.get(i.getLine() - 1)));
            }
            this.Pji.add(support);
        }
    }

    public PointValuePair resolve(int[][] x) {
        //risoluzione di P(x) descritto dall'equazione 22
        int[][][] b = ModelUtil.generateB(this.L,this.Srp,this.P,this.Pi,this.S,this.P0,this.Pr,this.Tt,this.Tpi,
                this.Tpit, this.maxT, this.D, x, this.c);
        double[] objective = new double[1]; //Sarebbe epsilon
        objective[0] = 1.0;

        //Definizione funzione obiettivo
        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objective,0.0);

        //Definizione dei vincoli
        List<LinearConstraint> constraints = new ArrayList<>();

        //Vincoli rappresentati dall'equazione 20
        for(int i = 0; i < this.S.size(); i++){
            double value = 0;
            double[] constraintValue = new double[1];
            for(int tau = 0; tau <= this.maxT; tau++) {
                if((i < Pi.size()) && (i < Pr.size())) {
                    int dElementSum = 0;
                    int bElementSum1 = 0;
                    int bElementSum2 = 0;
                    for (Integer t1 : Tt.get(tau)) {
                        dElementSum = dElementSum + d[i][t1];
                    }
                    for (Path p : Pi.get(i)) {
                        if (S.get(i).getId() - 1 < Tpit.get(P.indexOf(p)).size()) {
                            for (Integer t1 : Tpit.get(P.indexOf(p)).get(S.get(i).getId() - 1).get(tau)) {
                                bElementSum2 = bElementSum2 + b[p.getId() - 1][i][t1];
                            }
                        }
                    }
                    for (Path p : Pr.get(i)) {
                        if (S.get(i).getId() - 1 < Tpit.get(P.indexOf(p)).size() + 1) {
                            for (Integer t1 : Tpit.get(P.indexOf(p)).get(S.get(i).getId() - 1).get(tau)) {
                                int save = -1;
                                for (int index = 1; index < p.getPath().length; index++) {
                                    if ((p.getPath()[index] == S.get(i).getId())) {
                                        save = index - 1;
                                        break;
                                    }
                                }
                                if (save > -1) {
                                    bElementSum1 = bElementSum1 + b[p.getId() - 1][p.getStationId(save)][t1];
                                }
                            }
                        }
                    }
                    value = (1 / (double) SC[i]) * (dElementSum + bElementSum1 - bElementSum2);
                    if(value < 0) {
                        value = 0;
                    }
                    constraintValue[0] = 1.0;
                }
                constraints.add(new LinearConstraint(constraintValue, Relationship.GEQ, value));
            }
        }
        /*
        Il vincolo epsilon >= 0 è implicito nella definizione del vincolo precedente.
        Alla riga 128 controllo che value sia > 0, in questo modo unisco i 2 vincoli insieme
         */


        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

        OptimizationData[] optimizationData = {
                objectiveFunction,
                constraintSet,
                GoalType.MINIMIZE
        };
        // Risoluzione del problema di ottimizzazione
        SimplexSolver solver = new SimplexSolver();
        return solver.optimize(optimizationData);
    }

    public PointValuePair resolve(int[][] x1, Line lOline, List<Line> newL) {
        //Risoluzione di P(x,l) descritto dall'equazione 30
        int[][] x = new int[newL.size()-1][x1[0].length];
        int indexLine = 0;
        for(int l = 0; l < x1.length; l++) {
            if(!L.get(l).equals(lOline)) {
                for(int t = 0; t < x1[0].length; t++) {
                    x[indexLine][t] = x1[l][t];
                }
                indexLine++;
            }
        }
        List<Line> supportL = new ArrayList<>(newL);
        supportL.remove(lOline);
        //b non viene usato, ma viene generato per vedere se esiste un valore fattibile
        int[][][] b = ModelUtil.generateB(supportL,this.Srp,this.P,this.Pi,this.S,this.P0,this.Pr,this.Tt,this.Tpi,
                this.Tpit, this.maxT, this.D, x, this.c);
        int[][][] n = ModelUtil.generateN(this.Srp,this.P,this.Pi,this.S,this.P0,this.Pr,this.Tt,this.Tpi,this.Tpit,
                this.maxT, this.D);
        double[] objective = new double[1];
        objective[0] = 1.0;

        //Definizione funzione obiettivo
        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objective,0.0);

        //Definizione dei vincoli
        List<LinearConstraint> constraints = new ArrayList<>();

        //Vincoli rappresentati dall'equazione 20
        for(int i = 0; i < this.S.size(); i++){
            double value = 0;
            double[] constraintValue = new double[1];
            for(int tau = 0; tau <= this.maxT; tau++) {
                if((i < Pi.size()) && (i < Pr.size())) {
                    int dElementSum = 0;
                    int bElementSum1 = 0;
                    int bElementSum2 = 0;
                    for (Integer t1 : Tt.get(tau)) {
                        dElementSum = dElementSum + d[i][t1];
                    }
                    for (Path p : Pi.get(i)) {
                        if (S.get(i).getId() - 1 < Tpit.get(P.indexOf(p)).size()) {
                            for (Integer t1 : Tpit.get(P.indexOf(p)).get(S.get(i).getId() - 1).get(tau)) {
                                bElementSum2 = bElementSum2 + n[p.getId() - 1][i][t1];
                            }
                        }
                    }
                    for (Path p : Pr.get(i)) {
                        if (S.get(i).getId() - 1 < Tpit.get(P.indexOf(p)).size() + 1) {
                            for (Integer t1 : Tpit.get(P.indexOf(p)).get(S.get(i).getId() - 1).get(tau)) {
                                int save = -1;
                                for (int index = 1; index < p.getPath().length; index++) {
                                    if ((p.getPath()[index] == S.get(i).getId())) {
                                        save = index - 1;
                                        break;
                                    }
                                }
                                if (save > -1) {
                                    bElementSum1 = bElementSum1 + n[p.getId() - 1][p.getStationId(save)][t1];
                                }
                            }
                        }
                    }
                    value = (1 / (double) SC[i]) * (dElementSum + bElementSum1 - bElementSum2);
                    if(value < 0) {
                        value = 0;
                    }
                    constraintValue[0] = 1.0;
                }
                constraints.add(new LinearConstraint(constraintValue, Relationship.GEQ, value));
            }
        }
        //Come per il metodo precedente, il vincolo epsilon >= 0 è implicito


        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

        OptimizationData[] optimizationData = {
                objectiveFunction,
                constraintSet,
                GoalType.MINIMIZE
        };
        // Risoluzione del problema di ottimizzazione
        SimplexSolver solver = new SimplexSolver();
        return solver.optimize(optimizationData);
    }

}
