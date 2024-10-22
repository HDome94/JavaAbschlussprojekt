package de.dhahn.dhsolarverwaltung.client.settings;


/**
 * Diese Klasse stellt die Einstellungen der Client Applikation bereit
 */
public class AppSettings {

    //region Konstanten
    public static final String DB_PROTOCOL = "jdbc:mariadb://";
    public static final String DB_DOMAIN = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "solarVerwaltung";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";
    public static final int DB_RETRY_INTERVALL = 60_000; // 1min

    public static final int MEASUREMENT_INTERVALL = 60_000; // 1min
    public static final int SCENE_SWITCH_CLICK_COUNT = 2;
    public static final int TABLE_COLUMN_PADDING = 30;
    public static final String API_URL = "rmi://localhost:5090/solarservice";
    public static final String CURRENCY = "€";

    public static double PRICE_PER_KW_PURCHASED = 0.4;
    public static double PRICE_PER_KW_SALED = 0.15;


    public static double DEFAULT_WIDTH = 1600;
    public static double DEFAULT_HEIGTH = 1080;

    public static final String TABLE_COLUMN_FONT = "-fx-font-size: 14px;";

    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    private AppSettings() {
    }

    //endregion


    //region Methoden

    //endregion
}
