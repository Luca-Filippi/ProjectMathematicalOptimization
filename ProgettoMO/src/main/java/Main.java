import it.units.inginf.mathematicaloptimization.Alns;
import it.units.inginf.mathematicaloptimization.Dalns;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Main {
    public static void main(String[] args) {
        /*
        Definiamo alcuni esperimenti:
        sia in alns che in dalns si materrà il valore di Tmax = 10 per tutti gli esperimenti che presenterò;
        verranno presentati due insiemi di stazioni e percorsi distinti che verranno combinati in 4 esperimenti
         */
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean(); //Serve per calcolare il tempo di esecuzione
        String[] stationSet1 = {",1,1" ,
                ",2,1" ,
                ",3,1" ,
                ",4,1," ,
                ",5,1" ,
                ",6,1" ,
                ",7,1" ,
                ",8,1" ,
                ",1,2" ,
                ",2,2" ,
                ",3,2" ,
                ",4,2" ,
                ",5,2" ,
                ",6,2"};
        String[] stationSet2 = {",1,1" ,
                ",2,1" ,
                ",3,1" ,
                ",4,1," ,
                ",5,1" ,
                ",6,1" ,
                ",1,2" ,
                ",2,2" ,
                ",3,2" ,
                ",4,2"};
        String[] pathSet1 = {"1,1;1;2;1,no" ,
                "2,1;1;3;1,no" ,
                "3,1;1;4;1,no" ,
                "4,2;1;3;1,no" ,
                "5,2;1;4;1,no" ,
                "6,3;1;4;1,no" ,
                "7,5;1;6;1,no" ,
                "8,5;1;7;1,no" ,
                "9,5;1;8;1,no" ,
                "10,6;1;7;1,no"};
        String[] pathSet2 = {"1,1;1;2;1,no" ,
                "2,1;1;3;1,no" ,
                "3,1;1;4;1,no" ,
                "4,2;1;3;1,no" ,
                "5,2;1;4;1,no" ,
                "6,3;1;4;1,no" ,
                "7,5;1;6;1,no" ,
                "8,5;1;7;1,no" ,
                "9,5;1;8;1,no" ,
                "10,6;1;7;1,no" ,
                "11,6;1;8;1,no" ,
                "12,7;1;8;1,no" ,
                "13,5;1;6;1,no" ,
                "14,5;1;7;1,no" ,
                "15,5;1;8;1,no" ,
                "16,1;2;2;2,no" ,
                "17,1;2;3;2,no" ,
                "18,1;2;4;2,no" ,
                "19,2;2;3;2,no" ,
                "20,2;2;4;2,no"};
        long startCpuTime;
        long finishCpuTime;
        long cpuTime;
        Alns alns1 = new Alns(stationSet1,pathSet1);
        Dalns dalns1 = new Dalns(stationSet1,pathSet1);
        Alns alns2 = new Alns(stationSet1,pathSet2);
        Dalns dalns2 = new Dalns(stationSet1,pathSet2);
        Alns alns3 = new Alns(stationSet2,pathSet1);
        Dalns dalns3 = new Dalns(stationSet2,pathSet1);
        Alns alns4 = new Alns(stationSet2,pathSet2);
        Dalns dalns4 = new Dalns(stationSet2,pathSet2);
        int x[][];

        //Iniziamo gli esperimenti

        //Esperimento 1, uso stationSet1 e pathSet1
        System.out.println("Esperimento 1.a ALNS con stationSet1 e pathSet1");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = alns1.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms" + "\n");


        System.out.println("Esperimento 1.b DALNS con stationSet1 e pathSet1");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = dalns1.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms");

        System.out.println("---------------------------------------------------------------------");

        //Esperimento 2, uso stationSet1 e pathSet2
        System.out.println("Esperimento 2.a ALNS con stationSet1 e pathSet2");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = alns2.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms" + "\n");

        System.out.println("Esperimento 2.b DALNS con stationSet1 e pathSet2");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = dalns2.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms");

        System.out.println("---------------------------------------------------------------------");

        //Esperimento 3, uso stationSet1 e pathSet1
        System.out.println("Esperimento 3.a ALNS con stationSet2 e pathSet1");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = alns3.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime  + "ms" + "\n");


        System.out.println("Esperimento 3.b DALNS con stationSet2 e pathSet1");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = dalns3.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms");

        System.out.println("---------------------------------------------------------------------");

        //Esperimento 4, uso stationSet2 e pathSet2
        System.out.println("Esperimento 4.a ALNS con stationSet2 e pathSet2");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = alns4.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms" + "\n");

        System.out.println("Esperimento 4.b DALNS con stationSet2 e pathSet2");
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        System.out.print("Valore funzione obiettivo: ");
        x = dalns4.run();
        finishCpuTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = (long) (finishCpuTime-startCpuTime)/1000000; //Lo esprimo in millisecondi
        System.out.println("il tempo impiegato da questo esperimento è : " + cpuTime + "ms");

        System.out.println("---------------------------------------------------------------------");
    }
}
