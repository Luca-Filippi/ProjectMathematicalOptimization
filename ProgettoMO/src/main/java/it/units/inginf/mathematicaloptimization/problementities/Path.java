package it.units.inginf.mathematicaloptimization.problementities;

import java.util.List;

public class Path {
    //Classe che modella l'oggetto percorso
    private int id;
    private int[] path;
    private boolean transfers;

    public Path(int id, int[] path, boolean transfers) {
        this.id = id;
        this.path = path;
        this.transfers = transfers;
    }

    public Path(int id, String[] stringPath, boolean transfers) {
        this.id = id;
        int intPath[] = new int[stringPath.length];
        for(int i = 0; i < intPath.length; i++) {
            intPath[i] = Integer.parseInt(stringPath[i]);
        }
        this.path = intPath;
        this.transfers = transfers;

    }

    public int getId() {
        return this.id;
    }

    public int[] getPath() {
        return this.path;
    }

    public int getStationId(int i) {
        return this.path[i];
    }

    public boolean stationIsInThisPath(Station s) {
        for(int i = 0; i < this.path.length; i++) {
            if(this.path[i] == s.getId()) {
                return true;
            }
        }
        return false;
    }

    public int getMaxPathLength(List<Path> P) {
        int max = 0;
        for(Path p: P) {
            if(p.getPath().length > 0) {
                max = p.getPath().length;
            }
        }
        return max;
    }
}
