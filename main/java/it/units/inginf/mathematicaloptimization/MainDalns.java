package it.units.inginf.mathematicaloptimization;

public class MainDalns {
    public static void main(String[] args) {
        System.out.println("Comincio a calcolare la soluzione di DALNS");
        Dalns dalns = new Dalns();
        int[][] x = dalns.run();
    }
}