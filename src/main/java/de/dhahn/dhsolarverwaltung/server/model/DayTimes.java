package de.dhahn.dhsolarverwaltung.server.model;

import de.dhahn.dhsolarverwaltung.client.model.Direction;

/**
 * Diese Enum Klasse dient, um für die verschiedenen Tageszeiten und der Ausrichtung der Solaranlage einen Faktor zu
 * ermitteln.
 */

public enum DayTimes {
    //region Aufzählungen
    MORNING(new double[]{0.1, 0.4, 0.6, 0.3, 0, 0, 0, 0}),
    MIDDAY(new double[]{0, 0.1, 0.25, 0.6, 1, 0.8, 0, 0}),
    AFTERNOON(new double[]{0, 0, 0.1, 0.2, 1, 1, 0, 0}),
    EVENING(new double[]{0, 0, 0, 0, 0.1, 0.2, 0.45, 0.1}),
    NIGHT(new double[]{0, 0, 0, 0, 0, 0, 0, 0});

    //endregion

    //region Konstanten
    private static final int INDEX_FACTOR_N = 0;
    private static final int INDEX_FACTOR_NE = 1;
    private static final int INDEX_FACTOR_E = 2;
    private static final int INDEX_FACTOR_SE = 3;
    private static final int INDEX_FACTOR_S = 4;
    private static final int INDEX_FACTOR_SW = 5;
    private static final int INDEX_FACTOR_W = 6;
    private static final int INDEX_FACTOR_NW = 7;
    //endregion


    //region Attribute
    private final double factorNorth;
    private final double factorNorthEast;
    private final double factorEast;
    private final double factorSouthEast;
    private final double factorSouth;
    private final double factorSouthWest;
    private final double factorWest;
    private final double factorNorthWest;
    //endregion

    //region Konstruktoren
    DayTimes(double[] factors) {
        factorNorth = factors[INDEX_FACTOR_N];
        factorNorthEast = factors[INDEX_FACTOR_NE];
        factorEast = factors[INDEX_FACTOR_E];
        factorSouthEast = factors[INDEX_FACTOR_SE];
        factorSouth = factors[INDEX_FACTOR_S];
        factorSouthWest = factors[INDEX_FACTOR_SW];
        factorWest = factors[INDEX_FACTOR_W];
        factorNorthWest = factors[INDEX_FACTOR_NW];
    }
    //endregion

    //region Methoden
    public double getFactor(Direction direction) {
        return switch (direction) {
            case N -> factorNorth;
            case NE -> factorNorthEast;
            case E -> factorEast;
            case SE -> factorSouthEast;
            case S -> factorSouth;
            case SW -> factorSouthWest;
            case W -> factorWest;
            case NW -> factorNorthWest;
        };
    }
    //endregion
}
