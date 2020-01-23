package com.example.ame_simonsays;

import java.io.Serializable;

public class Guanyador implements Comparable<Guanyador>, Serializable {
    private String nomJugador;
    private int puntuacioObtinguda;

    public Guanyador(String nomJugador, int puntuacioObtinguda) {
        this.nomJugador = nomJugador;
        this.puntuacioObtinguda = puntuacioObtinguda;
    }

    public String getNomJugador() {
        return nomJugador;
    }

    public void setNomJugador(String nomJugador) {
        this.nomJugador = nomJugador;
    }

    public int getPuntuacioObtinguda() {
        return puntuacioObtinguda;
    }

    public void setPuntuacioObtinguda(int puntuacioObtinguda) {
        this.puntuacioObtinguda = puntuacioObtinguda;
    }

    @Override
    public String toString() {
        return nomJugador+","+puntuacioObtinguda;
    }

    @Override
    public int compareTo(Guanyador o) {
        if(this.getPuntuacioObtinguda() < o.getPuntuacioObtinguda())
            return -1;
        else if(this.getPuntuacioObtinguda() > o.getPuntuacioObtinguda())
            return 1;
        else return 0;
    }
}
