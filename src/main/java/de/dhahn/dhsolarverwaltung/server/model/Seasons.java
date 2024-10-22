package de.dhahn.dhsolarverwaltung.server.model;

/**
 * Diese Enum Klasse dient, um für die verschiedenen Jahreszeiten einen Faktor zu ermitteln
 */
public enum Seasons {

    //region Aufzählungen
    SPRING(0.5),
    SUMMER(1),
    FALL(0.6),
    WINTER(0.3);

    //endregion

    //region Konstanten
    //endregion


    //region Attribute
    double factor;
    //endregion

    //region Konstruktoren

    Seasons(double factor) {
        this.factor = factor;
    }

    //endregion

    //region Methoden

    public double getFactor() {
        return factor;
    }

    //endregion

}
