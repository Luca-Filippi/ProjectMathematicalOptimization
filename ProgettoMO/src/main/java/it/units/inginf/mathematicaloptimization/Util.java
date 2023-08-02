package it.units.inginf.mathematicaloptimization;

import it.units.inginf.mathematicaloptimization.problementities.Line;
import it.units.inginf.mathematicaloptimization.problementities.Path;
import it.units.inginf.mathematicaloptimization.problementities.Station;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Util {
    //Classe di utilità per la generazione degli insiemi utilizzati nei modelli
    private final static Random rand = new Random();

    public static List<Line> getL(int numberOfLines, int minT, int cycleT, int numberOfTrains, List<Station> S) {
        //metodo statico che ritorna l'insieme delle linee L
        List<Line> L = new ArrayList<>();
        for(int i = 1; i <= numberOfLines; i ++) {
            Line l = new Line(i, minT, cycleT,numberOfTrains,S);
            L.add(l);
        }
        return L;
    }

    public static List<Station> getS(String link) {
        //metodo statico che ritorna l'insieme delle stazioni S
        List<Station> S = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(link))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    // Salta la prima riga (intestazione)
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                Station s = new Station(values);
                S.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return S;
    }

    public static List<Station> getSl(int line, List<Station> S) {
        //Cre0 per una determinata linea la lista delle stazioni di quella linea
        List<Station> Sl = new ArrayList<>();
        for(Station i: S) {
            if(i.getLine() == line) {
                Sl.add(i);
            }
        }
        return Sl;
    }
    public static List<Path> getP(String link) {
        //metodo statico che restitusice l'insieme dei percorsi P
        List<Path> P = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(link))) {
            String line;
            int pathId = 1;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    // Salta la prima riga (intestazione)
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                String[] stringPath = values[1].split(";");
                boolean transfers = values[2].equalsIgnoreCase("yes");
                Path p = new Path(pathId,stringPath,transfers);
                P.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return P;
    }

    public static List<Path> getP0i(Station i, List<Path> P) {
        //metodo che restituisce l'insieme P_i^0 di una stazione specifica
        List<Path> P0 = new ArrayList<>();
        for(Path p : P) {
            int[] path = p.getPath();
            if(path[0] == i.getId()) {
                P0.add(p);
            }
        }
        return P0;
    }

    public static List<Path> getPri(Station i, List<Path> P) {
        //metodo che restituisce l'insime P_r^0 di una stazione specifica
        List<Path> Pr = new ArrayList<>();
        for(Path p : P) {
            for(int j = 1; j < p.getPath().length - 1; j++) {//itero sulle stazioni del percorso senza considerare l'origine e la destinazione
                if(p.getStationId(j) == i.getId()) {
                    Pr.add(p);
                    break; //esco dal ciclo interno
                }
            }
        }
        return Pr;
    }

    public static List<Path> getPi(List<Path> P0i, List<Path> Pri) {
        //metodo che restituisce l'insieme Pi dato dall'unione di Poi e Pri
        List<Path> Pi = new ArrayList<>();
        Pi.addAll(P0i);
        Pi.addAll(Pri);
        return Pi;
    }

    public static List<Integer> getTt(int t) {
        //metodo per la creazione dell'insieme Tt per un determinato t
        List<Integer> Tt = new ArrayList<>();
        for(int tau = 0; tau <= t; tau++) {
            Tt.add(tau);
        }
        return Tt;
    }

    public static int[][] generateMatrixtpi(List<Path> P, List<Station> S) {
        //matrice con tutte le tempistiche tra la stezione precedente a i e la stazione i
        int[][] t = new int[P.size()][S.size()];
        for(int p = 0; p < P.size(); p++) {
            for(int i = 0; i < S.size(); i++){
                t[p][i] = rand.nextInt(100) + 5;
            }
        }
        return t;
    }

    public static List<List<Integer>> getTpi(List<Station> S,int maxT, Path p, int[][] t) {
        //classe che genera l'insieme Tpi per un determinata stazione i su un determinato percorso p
        List<List<Integer>> Tpi = new ArrayList<>();
        for(int i = 0; i < S.size(); i++) {
            List<Integer> support = new ArrayList<>();
            for(int tau = 0; tau <= maxT; tau++) {
                if(tau >= t[p.getId() - 1][i]) {
                    support.add(tau);
                }
            }
            Tpi.add(support);
        }
        return Tpi;
    }

    public static List<List<List<Integer>>> getTpit(int maxT, List<Station> S, Path p, int[][] tMatrix) {
        //classe che genera l'insieme Tpit per un determinata stazione i su un determinato percorso p e fissato una certa unità di tempo t
        List<List<List<Integer>>> Tpit = new ArrayList<>();
        for(int i = 0; i < S.size(); i++) {
            List<List<Integer>> support = new ArrayList<>();
            if(p.stationIsInThisPath(S.get(i))) {
                for (int tau = 0; tau <= maxT; tau++) {
                    List<Integer> newSupport = new ArrayList<>();
                    for (int t = 0; t <= maxT; t++) {
                        if (tau + tMatrix[p.getId() - 1][i] <= t) {
                            newSupport.add(tau);
                        }
                    }
                    support.add(newSupport);
                }
            }
            Tpit.add(support);
        }
        return Tpit;
    }

    public static int[] getK() {
        //Vettore per i vincoli di materiale rotabile
        int[] k = new int[2];
        k[0] = 6;
        k[1] = 7;
        return k;
    }

    public static int[][] getD(List<Path> P, int maxT) {
        //Matric D, i cui elementi rappresentano il numero di passeggeri che entrano su un percorso p a un momento t
        //Questo è un esempio di generazione costante, si possono scegliere altre generazioni, andando ad indagare sul traffico
        int[][] D = new int[P.size()][maxT+1];
        for(int p = 0; p < P.size(); p++) {
            for(int t = 0; t <= maxT; t++) {
                D[p][t] = 10;
            }
        }
        return D;
    }

    public static int calculateSingledit(List<Path> P, List<Path> P0i, int t, int[][] D) {
        //Numero di passeggeri alla stazione i al momento t
        int d = 0;
        for(Path p : P0i) {
            d = d + D[P.indexOf(p)][t];
        }
        return d;
    }

    public static List<Station> getSr(Path p, List<Station> S) {
        //Lista delle stazioni di esclusa l'origine e la destinazione
        List<Station> Sr = new ArrayList<>();
        for(int i = 1; i < p.getPath().length - 1; i++) {
            for(Station s: S) {
                if(s.getId() == p.getStationId(i)) {
                    Sr.add(s);
                }
            }
        }
        return Sr;
    }

    public static List<List<List<Station>>> getSil(List<Line> L) {
        //Lista delle stazioni precedenti alla stazione i su una linea l, per tutte le linee e per tutte le stazioni
        List<List<List<Station>>> Sil = new ArrayList<>();
        for(Line l : L) {
            List<List<Station>> Sl = new ArrayList<>();
            for(Station i: l.getStationList()) {
                List<Station> support = new ArrayList<>();
                for(int j = 0; j < l.getStationList().indexOf(i); j++) {
                    support.add(l.getStationList().get(j));
                }
                Sl.add(support);
            }
            Sil.add(Sl);
        }
        return Sil;
    }
    
    public static List<Path> getPsi(List<Path> P, Station i, List<Path> Pri) {
        //Lista dei percorsi che terminano alla stazione i
        List<Path> Psi = new ArrayList<>();
        for(Path p : P) {//ciclo per trovare i percorsi che terminano alla stazione i
            int[] path = p.getPath();
            if(path[path.length-1] == i.getId()){
                Psi.add(p);
            }
        }
        Psi.addAll(Pri);
        return Psi;
    }
    public static List<Path> getPji(List<Path> P0j, List<Path> Prj, List<List<Path>> Ps, Station i, Line l) {
        //Lista degli insiemi dei percorsi che partano dal nodo j e terminano al nodo i
        List<Path> Pji = new ArrayList<>();
        List<Path> Pji1 = new ArrayList<>();
        List<Path> Pji2 = new ArrayList<>();
        Pji1.addAll(P0j);
        Pji1.addAll(Prj);
        for(int index = l.getStationList().indexOf(i); index < l.getStationList().size(); index++) {
            Pji2.addAll(Ps.get(index+1));
        }
        for(Path p : Pji1) {
            if(Pji2.contains(p)) {
                Pji.add(p);
            }
        }
        return Pji;
    }

}
