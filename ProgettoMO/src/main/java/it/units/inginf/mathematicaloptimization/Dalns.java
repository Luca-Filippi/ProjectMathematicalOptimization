package it.units.inginf.mathematicaloptimization;

import it.units.inginf.mathematicaloptimization.problementities.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dalns extends Alns {

    public Dalns(String[] stationSet, String[] pathSet) {
        //Step 1: Initialization
        super(stationSet,pathSet);
    }

    @Override
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

        //Step4 -> Parte Algoritmo Dalns
        List<Line> newL = new ArrayList<>(this.L);
        executeInnerLoop(xc,newL);

        //Step5 ritornare la soluzione migliore
        System.out.println(this.bestValue);
        return this.xb;
    }
    private void executeInnerLoop(int[][] xc, List<Line> newL) {
        while(!newL.isEmpty()) {
            int lOline = -1;
            boolean lOlineIsRight = false;
            //Step 4.1
            while(!lOlineIsRight) {
                lOline = rand.nextInt(this.L.size());
                for(Line l : newL) {
                    if(l.getLineNumber()-1 == lOline) {
                        lOlineIsRight = true;
                        break;
                    }
                }
            }
            double newValue = Double.MAX_VALUE;
            if(newL.size() - 1 > 0) {
                newValue = this.model.resolve(xc, this.L.get(lOline), newL).getValue();
            }
            if (newValue < this.bestValue) {
                xc = executeOuterLoop(xc, newValue);
            } else {
                //Step 4.2
                newL.remove(this.L.get(lOline));
            }
        }
    }

}
