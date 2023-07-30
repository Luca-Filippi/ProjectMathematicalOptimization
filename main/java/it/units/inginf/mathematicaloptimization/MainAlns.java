package it.units.inginf.mathematicaloptimization;

public class MainAlns {
    public static void main(String[] args) {
        System.out.println("Comincio a calcolare la soluzione di ALNS");
        Alns alns = new Alns();
        int[][] x = alns.run();
    }
}
