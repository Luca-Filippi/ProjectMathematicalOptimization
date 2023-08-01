package it.units.inginf.mathematicaloptimization;

import java.util.Arrays;
import java.util.Random;

public class DestroyOperator {
    /*
    Classe che rappresenta gli operatori di distruzione, sono presenti anche due metodi statici che rappresentano
    effettivamente i due metodi di distruzione proposti
     */
    private static Random rand = new Random();

    private final double lambda = 0.5;
    private final int[] scores = new int[2];
    private final double[] weights = new double[2];

    public DestroyOperator() { //Gli scores sono inizializzati a 0 di default
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

    public static int[][] destroyOperator1(int[][] x, int line, int s1) {
        //Operatore di distruzione 1
        int[][] xNew = Arrays.copyOf(x, x.length);
        for (int i = 0; i < x.length; i++) {
            xNew[i] = Arrays.copyOf(x[i], x[i].length);
        }
        int iterator = 0;
        while(iterator < s1) {
            int t = rand.nextInt(x[0].length+1);
            if(xNew[line][t] == 1) {
                xNew[line][t] = 0;
                iterator++;
            }
        }
        return xNew;
    }

    public static int[][] destroyOperator2(int[][] x, int tl) {
        //Operatore di distruzione 2
        int[][] xNew = new int[x.length][x[0].length];
        int ts = 1;
        for(int l = 0; l < x.length; l++) {
            for(int t = 0; t < x[0].length - ts; t++) {//Si itera da |T|-ts con ts solitamente = 1 -> maxT+1-1=maxT
                if(x[l][t] == 1 && t > tl) {
                    xNew[l][t+ts] = 1;
                } else {
                    xNew[l][t] = x[l][t];
                }
            }
        }
        return xNew;
    }

}
