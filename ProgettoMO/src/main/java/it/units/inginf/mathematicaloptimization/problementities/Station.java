package it.units.inginf.mathematicaloptimization.problementities;

public class Station {
    //Classe che modella l'oggetto stazione/fermata

    private int id;
    private String name;
    private int line;

    public Station(int id, String name, int line) {
        this.id = id;
        this.name = name;
        this.line = line;
    }

    public Station(String[] values) {
        try{
            this.id = Integer.parseInt(values[1]);
            this.name = values[0];
            this.line = Integer.parseInt(values[2]);
        } catch(Exception e) {
            System.err.println("La creazione della stazione non è andata a buon fine: "+ e.getMessage());
        }
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getLine(){
        return this.line;
    }
}
