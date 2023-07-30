package it.units.inginf.mathematicaloptimization;

import java.util.Arrays;
import java.util.Random;
public class RepairOperator {
    /*
    Classe che rappresenta gli operatori di riparazione, sono presenti anche 3 metodi statici che rappresentano
    effettivamente i 3 metodi di riparazione proposti
     */
    private static Random rand = new Random();
    private final double lambda = 0.5;
    private final int[] scores = new int[3];
    private final double[] weights = new double[3];

    public RepairOperator() {//Gli scores sono inizializzati a 0 di default
        weights[0] = 1.0;
        weights[1] = 1.0;
    }

    public int[] getScores() {
        return this.scores;
    }

    public double[] getWeights() {
        return this.weights;
    }

    public void updateScore(int i, int delta) {
        try{
            scores[i]+= delta;
        } catch (Exception e) {
            System.err.println("lo score desiderato non esiste: " + e.getMessage());
        }
    }

    public void updateWeight(int i) {
        try{
            int sumScore = 0;
            for(int j = 0; j < scores.length; j++) {
                sumScore+= scores[i];
            }
            weights[i] = (1-lambda) * weights[i] + lambda*scores[i]/sumScore;
        } catch (Exception e) {
            System.err.println("lo score desiderato non esiste: " + e.getMessage());
        }
    }

    public double[] getProbability() {
        double[] probability = new double[2];
        double sumWeights = 0;
        for(int i = 0; i < 2; i++) {
            sumWeights+= weights[i];
        }
        for(int i = 0; i < 2; i++) {
            probability[i] = weights[i]/sumWeights;
        }
        return probability;
    }

    public static int[][] repairOperator1(int[][] x, int s2, int minT) {//genero s2 esattamente come s1
        //Operatore di riparazione 1
        int[][] xNew = Arrays.copyOf(x, x.length);
        for (int i = 0; i < x.length; i++) {
            xNew[i] = Arrays.copyOf(x[i], x[i].length);
        }
        int iterator = 0;
        while(iterator < s2) {
            int[] parametersMax = getMaxInterval(x,minT);
            int t = parametersMax[2] - parametersMax[1];
            int line = parametersMax[0];
            if(t < (2 * minT) || line == -1 || t < 0) {
                break;
            }
            if(xNew[line][t] == 0) {
                xNew[line][t] = 1;
                iterator++;
            }
        }

        return xNew;
    }

    public static int[][] repairOperator2(int[][] x, int[][] d, int s3) { //d è la domanda di passeggeri
        //Operatore di riparazione 2
        /*
        Attenzione questo operatore può violare i vincoli rappresentati dalle equazioni 12 e 13, in Alns è
        stato messo questo controllo
         */
        int[][] xNew = Arrays.copyOf(x, x.length);
        for (int i = 0; i < x.length; i++) {
            xNew[i] = Arrays.copyOf(x[i], x[i].length);
        }
        int[] passengerDemand = new int[d[0].length];
        int[] maxValuesIndexT = new int[s3];
        for(int t = 0; t < d[0].length; t++) {
            for(int i = 0; i < d.length; i++) {
                passengerDemand[t] = passengerDemand[t] + d[i][t];
            }
        }
        for(int i = 0; i < s3; i++) {
            int max = 0;
            int indexMax = -1;
            for(int j = 0; j < d[0].length; j++) {
                if(passengerDemand[j] > max) {
                    indexMax = j;
                    max = passengerDemand[j];
                }
            }
            passengerDemand[indexMax] = -1;
            maxValuesIndexT[i] = indexMax;
        }
        int iterator = 0;
        while(iterator < s3) {
            int t = maxValuesIndexT[iterator];
            int line = rand.nextInt(x.length);
            xNew[line][t] = 1;
            iterator++;
        }
        return xNew;
    }

    public static int[][] repairOperator3(int[][] x, int[] k, int minT, int cycleT) {
        //Operatore di riparazione 3 -> implementazione dell'algoritmo 1
        int[][] xNew = Arrays.copyOf(x, x.length);
        for (int i = 0; i < x.length; i++) {
            xNew[i] = Arrays.copyOf(x[i], x[i].length);
        }
        for(int t = 0; t < xNew[0].length; t++) {
            if(!checkEquation12(xNew,minT) || !checkEquation13(xNew,k,cycleT)) {
                DestroyOperator.destroyOperator2(xNew,t);
            }
        }
        return xNew;
    }

    public static boolean checkEquation12(int[][] x, int minT) {
        //Metodo che verifica che l'equazione 12 sia soddisfatta
        for(int l = 0; l < x.length; l++) {
            for(int t = 0; t < x[0].length; t++) {
                int size = Math.min(x[0].length + 1, t + minT);
                int sum = 0;
                for (int tau = t; tau < size; tau++) {
                    sum = sum + x[l][tau];
                }
                if(sum > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkEquation13(int[][] x, int[] k, int cycleT) {
        //Metodo che verifica che l'equazione 12 sia soddisfatta
        for(int l = 0; l < x.length; l++) {
            for(int t = 0; t < x[0].length; t++) {
                int size = Math.min(x[0].length + 1, t + cycleT);
                int sum = 0;
                for (int tau = t; tau < size; tau++) {
                    sum = sum + x[l][tau];
                }
                if(sum > k[l]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] getMaxInterval(int[][] x, int minT) {
        //Metodo per la ricerca del massimo intervallo di tempo che serve nell'operatore di riparazione 1
        int startTime = -1;
        int stopTime = -1;
        int line = -1;
        int[] parametersMax = new int[3];
        parametersMax[0] = line;
        //line, startTime, endTime sono gli elementi che determinato il massimo intervallo
        for(int l = 0; l < x.length; l++) {
            line = l;
            for(int t = 0; t < x[l].length; t++) {
                if (x[l][t] == 0) {
                    if (startTime == -1) {
                        startTime = t;
                    } else {
                        stopTime = t;
                    }
                } else {
                    startTime = -1;
                    stopTime = -1;
                }
            }
            if(parametersMax[2] - parametersMax[1] < stopTime - startTime) {
                parametersMax[0] = l;
                parametersMax[1] = startTime;
                parametersMax[2] = stopTime;
            }
        }
        return parametersMax;
    }
}
