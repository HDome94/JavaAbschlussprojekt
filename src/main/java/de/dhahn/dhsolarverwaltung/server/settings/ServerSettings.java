package de.dhahn.dhsolarverwaltung.server.settings;

/**
 * Diese Klasse stellt die Einstellungen für den Server bereit
 */
public class ServerSettings {

    //region Konstanten
    public static final int PORT = 5090;
    public static final String ROUTE_SOLAR_SERVICE = "solarservice";
    public static int DAY_TIME_DURATION = 180_000; // 3min
    public static int SEASON_DURATION = 600_000; // 10min
    public static int MIN_CONSUMPTION = 300; // min Verbrauchswert
    public static int MAX_CONSUMPTION = 5_000; // max Verbrauchswert

    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    private ServerSettings() {
    }

    //endregion


    //region Methoden

    //endregion
}
