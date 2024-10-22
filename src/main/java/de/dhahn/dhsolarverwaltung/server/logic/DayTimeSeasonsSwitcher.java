package de.dhahn.dhsolarverwaltung.server.logic;

import de.dhahn.dhsolarverwaltung.server.model.DayTimes;
import de.dhahn.dhsolarverwaltung.server.model.Seasons;

/**
 * Diese Klasse dient zur Simulation der Tages- und Jahreszeiten.
 * Wird von den Threads gesteuert und für die Berechnung der Produktion benötigt.
 */
public class DayTimeSeasonsSwitcher {

    //region Konstanten
    private static DayTimeSeasonsSwitcher instance;

    //endregion


    //region Attribute
    private DayTimes currentDayTime;
    private Seasons currentSeason;
    //endregion


    //region Konstruktoren

    private DayTimeSeasonsSwitcher() {
        currentDayTime = DayTimes.MORNING;
        currentSeason = Seasons.SPRING;
    }

    //endregion


    //region Methoden
    public static synchronized DayTimeSeasonsSwitcher getInstance() {
        if (instance == null) instance = new DayTimeSeasonsSwitcher();
        return instance;
    }

    /**
     * Wechselt zur nächsten Tageszeit
     */
    public void switchDayTime() {
        int newOrdinal;
        if (currentDayTime.ordinal() + 1 == DayTimes.values().length)
            newOrdinal = 0;
        else
            newOrdinal = currentDayTime.ordinal() + 1;

        currentDayTime = DayTimes.values()[newOrdinal];
    }

    /**
     * Wechselt zur nächsten Jahreszeit
     */
    public void switchSeason() {
        int newOrdinal;
        if (currentSeason.ordinal() + 1 == Seasons.values().length)
            newOrdinal = 0;
        else
            newOrdinal = currentSeason.ordinal() + 1;

        currentSeason = Seasons.values()[newOrdinal];
    }

    public DayTimes getCurrentDayTime() {
        return currentDayTime;
    }

    public Seasons getCurrentSeason() {
        return currentSeason;
    }

    //endregion
}
