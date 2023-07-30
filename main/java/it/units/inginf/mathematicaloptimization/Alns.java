package it.units.inginf.mathematicaloptimization;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import it.units.inginf.mathematicaloptimization.problementities.Line;
import it.units.inginf.mathematicaloptimization.problementities.Path;
import it.units.inginf.mathematicaloptimization.problementities.Station;

import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Alns {
    protected final int maxT = 100; //valore massimo dell'insieme delle unità discrete dell'insieme T
    protected final int minT = 5;//Tempo minimo di processo per una linea, uguale per tutte le linee (mia scelta)
    protected final int cycleT = 44;//Tempo di ciclo per una linea, uguale per tutte le linee(mia scelta)
    protected final int numbersOfLines = 2;//Numero delle linee della rete
    protected final  int numbersOfTrains = 60; //Numero dei treni su ciascuna linea (mia scelta)
    protected List<Line> L;//Insieme delle linee
    protected List<Station> S;//Insieme delle stazioni
    protected List<Path> P;//Insieme dei percorsi
    protected final int limitN = 60000;//Numero iterazioni massime per il soddisfare i criteri di stop
    protected final long limitT = 6000;//Tempo massimo per soddisfare i criteri di stop
    protected final int snNum = 50000; //Numero iterazioni massime per il soddisfare i criteri alternativi
    protected final long maxTS = 5000; //Tempo massimo per soddisfare i criteri alternativi
    protected int[] k; //Materiale rotabilei su una certa linea
    protected int[][] D; //numero di passeggeri sul percorso p al momento t
    protected int[][] d; //domanda di passeggeri alla stazione i al momento t
    protected List<List<Path>> P0 = new ArrayList<>(); //Lista delle liste dei percorsi che iniziano a una certa stazione, per ogni stazione
    protected ThreadMXBean threadMXBean; //necessario per calcolare il tempo
    protected Random rand = new Random();
    protected long startCpuTime; //tempo cpu d'inizio di un'operazione
    protected DestroyOperator destroyOperator;
    protected RepairOperator repairOperator;
    protected Model model;
    protected int[][] xb; //Soluzione migliore
    protected double bestValue;//Valore di P(xb)

    public Alns() {
        //Step 1: Initialization -> Definisco tutti gli oggetti e i parametri che servono all'implementazione
        this.S = Util.getS(System.getProperty("user.home") + "/Desktop/ProgettoMO/input_node.csv");
        this.L = Util.getL(this.numbersOfLines, this.minT, this.cycleT, this.numbersOfTrains, this.S);
        this.P = Util.getP(System.getProperty("user.home")+"/Desktop/ProgettoMO/input_path.csv");
        this.k = Util.getK();
        for(Station i : this.S) {
            List<Path> p0i = Util.getP0i(i,this.P);
            this.P0.add(p0i);
        }
        this.D = Util.getD(this.P, this.maxT);
        this.d = new int[this.S.size()][this.maxT+1];
        for(int i = 0; i < this.S.size(); i++) {
            for(int tau = 0; tau <= this.maxT; tau++) {
                this.d[i][tau] = Util.calculateSingledit(this.P,P0.get(i),tau,D);
            }
        }
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        // Ci assicuriamo che la piattaforma supporti il tempo di CPU
        if (this.threadMXBean.isCurrentThreadCpuTimeSupported()) {
            // Tempo di CPU per il thread corrente
            this.startCpuTime = this.threadMXBean.getCurrentThreadCpuTime();
        } else {
            System.err.println("Errore");
            System.exit(1);
        }
    }

    public int[][] run() {
        //Step 2
        //Assegno sia a xc che a xb il valore della soluzione iniziale
        int[][] xc = generateInitialSolution(); //Soluzione corrente
        this.xb = Arrays.copyOf(xc, xc.length);
        for (int i = 0; i < xc.length; i++) {
            xb[i] = Arrays.copyOf(xc[i], xc[i].length);
        }

        //Step3 -> Parte Algoritmo Alns
        this.destroyOperator = new DestroyOperator();
        this.repairOperator = new RepairOperator();
        this.model = new Model(S,L,P,P0,maxT,D,d);
        double currentValue = this.model.resolve(xc).getValue(); //Valore della soluzione corrente
        this.bestValue = currentValue;
        xc = executeOuterLoop(xc,currentValue);

        //Ritorno la soluzione migliore
        System.out.println(this.bestValue);
        return this.xb;
    }

    protected int[][] executeOuterLoop(int[][] xc, double currentValue) {
        boolean stop = false;
        int numberOfIterations = 0;
        this.startCpuTime = this.threadMXBean.getCurrentThreadCpuTime();
        long cpuTime = this.threadMXBean.getCurrentThreadCpuTime();
        double[] destroyOperatorProbability;
        double[] repairOperatorProbability;
        while(!checkStoppingCriteria(numberOfIterations,cpuTime-startCpuTime,stop)) {
            destroyOperatorProbability = this.destroyOperator.getProbability();
            repairOperatorProbability = this.repairOperator.getProbability();
            int destroyIndex = -1;
            int repairIndex = -1;
            //Incomincio a generare una nuova soluzione
            if(destroyOperatorProbability[0] > destroyOperatorProbability[1]) {
                xc = DestroyOperator.destroyOperator1(xc,rand.nextInt(xc.length),rand.nextInt(maxT/minT));
                destroyIndex = 0;
            } else {
                xc = DestroyOperator.destroyOperator2(xc,rand.nextInt(maxT+1));
                destroyIndex = 1;
            }
            if(repairOperatorProbability[0] >= repairOperatorProbability[1] &&
                    repairOperatorProbability[0] >= repairOperatorProbability[2]) {
                xc = RepairOperator.repairOperator1(xc,rand.nextInt(maxT/minT),minT);
                repairIndex = 0;
            } else if(repairOperatorProbability[1] >= repairOperatorProbability[0] &&
                    repairOperatorProbability[1] >= repairOperatorProbability[2]) {
                xc = RepairOperator.repairOperator2(xc,d,rand.nextInt(maxT/minT));
                repairIndex = 1;
            } else {
                xc = RepairOperator.repairOperator3(xc,k,minT,cycleT);
                repairIndex = 2;
            }
            if(!RepairOperator.checkEquation12(xc,minT) && RepairOperator.checkEquation13(xc,k,cycleT)) {
                xc = RepairOperator.repairOperator3(xc, k, minT, cycleT);
                repairIndex = 2;
            }
            double newValue = model.resolve(xc).getValue();
            if(newValue < bestValue) {
                //In questo caso si sceglie il delta più alto, il valore scelto da me è 3
                destroyOperator.updateScore(destroyIndex,3);
                repairOperator.updateScore(repairIndex,3);
                this.xb = Arrays.copyOf(xc, xc.length);
                for (int i = 0; i < xc.length; i++) {
                    xb[i] = Arrays.copyOf(xc[i], xc[i].length);
                }
                this.bestValue = newValue;
            } else {
                if (newValue < currentValue) {
                    //In questo caso si sceglie un valore di delta medio, il valore scelto da me è 2
                    destroyOperator.updateScore(destroyIndex, 2);
                    repairOperator.updateScore(repairIndex, 2);
                } else {
                    //In questo caso si sceglie il delta più basso, il valore scelto da me è 1
                    destroyOperator.updateScore(destroyIndex, 1);
                    repairOperator.updateScore(repairIndex, 1);
                }
                numberOfIterations++;
                cpuTime = this.threadMXBean.getCurrentThreadCpuTime();
                stop = checkAlternativeCriteria(numberOfIterations,cpuTime-startCpuTime);
            }
            //Aggiornamento dei pesi
            destroyOperator.updateWeight(destroyIndex);
            repairOperator.updateWeight(repairIndex);
        }
        return xc;
    }

    protected boolean checkStoppingCriteria(int numberOfIterations, long cpuTime, boolean stop) {
        return (numberOfIterations >= this.limitN) || (cpuTime >= this.limitT) || stop;
    }

    protected boolean checkAlternativeCriteria(int numberOfIterations, long cpuTime) {
        return (numberOfIterations >= this.snNum) || (cpuTime >= this.maxTS);
    }

    protected int[][] generateInitialSolution() {
        double[] objective = new double[L.size() * (maxT+1)];
        int index = 0;
        for (int l = 0; l < L.size(); l++) {
            for (int t = 0; t <= maxT; t++) {
                objective[index++] = 1.0;
            }
        }

        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objective,0.0);

        List<LinearConstraint> constraints = new ArrayList<>();
        int size;
        // Vincoli -> Equazioni 12 e 13
        for(int l = 0; l < L.size(); l++) {
            for(int t = 0; t <= maxT; t++) {
                size = Math.min(maxT + 1, t + minT);
                double[] constraintValue = new double[L.size()*(maxT+1)];
                for (int tau = t; tau < size; tau++) {
                    index = l * (maxT+1) + tau;
                    constraintValue[index] = 1.0;
                }
                constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 1.0));
            }
        }

        for(int l = 0; l < L.size(); l++) {
            for(int t = 0; t <= maxT; t++) {
                size = Math.min(maxT + 1, t + cycleT);
                double[] constraintValue = new double[L.size()*(maxT+1)];
                for (int tau = t; tau < size; tau++) {
                    index = l * (maxT+1) + tau;
                    constraintValue[index] = 1.0;
                }
                constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, k[l]));
            }
        }
        //Vincolo binario
        for (int l = 0; l < this.L.size(); l++) {
            for (int t = 0; t <= this.maxT; t++) {
                double[] constraintValue = new double[this.L.size() * (this.maxT + 1)];
                index = l * (this.maxT + 1) + t;
                constraintValue[index] = 1.0;
                constraints.add(new LinearConstraint(constraintValue, Relationship.LEQ, 1.0));
                constraints.add(new LinearConstraint(constraintValue, Relationship.GEQ, 0.0));
            }
        }

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);
        OptimizationData[] optimizationData = {
                objectiveFunction,
                constraintSet,
                GoalType.MAXIMIZE
        };

        // Risoluzione del problema di ottimizzazione
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(optimizationData);
        index = 0;
        int[][] x0 = new int[this.L.size()][maxT+1];
        for (int l = 0; l < this.L.size(); l++) {
            for (int t = 0; t <= this.maxT; t++) {
                x0[l][t] = (int) solution.getPoint()[index++];
            }
        }
        return x0;
    }
}
