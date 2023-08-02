package it.units.inginf.mathematicaloptimization.problementities;

import java.util.ArrayList;
import java.util.List;

public class Line {
    //Classe che modella l'oggetto linea
    private int lineNumber;
    private int Tmin;
    private int Tcycle;
    private List<Station> stationList;
    private int numberOfTrains;

    public Line(int lineNumber, int Tmin, int Tcycle, int numberOfTrains, List<Station> S) {
        this.lineNumber = lineNumber;
        this.Tmin = Tmin;
        this.Tcycle = Tcycle;
        this.numberOfTrains = numberOfTrains;
        this.stationList = new ArrayList<>();
        for(Station s : S) {
            if(s.getLine() == this.lineNumber) {
                this.stationList.add(s);
            }
        }
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getTmin() {
        return this.Tmin;
    }

    public int getTcycle() {
        return this.Tcycle;
    }

    public int getNumberOfTrains(){
        return this.numberOfTrains;
    }

    public List<Station> getStationList() {
        return this.stationList;
    }
}
