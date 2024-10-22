package de.dhahn.dhsolarverwaltung.client.settings;

/**
 * Diese Klasse stellt die Texte der App zur Verfügung
 */
public class AppText {

    //region Konstanten
    public static final String APP_TITLE = "DH - Solarsystem Verwaltung";
    public static final String PRODUCTION = "Produktion";
    public static final String CONSUMPTION = "Verbrauch";
    public static final String SALE = "Verkauf";
    public static final String PURCHASE = "Einkauf";
    public static final String NEW_SYSTEM = "Neues System";
    public static final String SOLAR_SYSTEM = "Solar Anlage";
    public static final String SOLAR_SYSTEMS = "Solar Anlagen";
    public static final String SOLAR_PANEL = "Solar Panel";
    public static final String SOLAR_PANELS = "Solar Panels";
    public static final String BATTERY = "Batterie";
    public static final String BATTERIES = "Batterien";
    public static final String MAX_PERFORMANCE = "Maximale Leistung";
    public static final String BATTERY_COUNT = "Anzahl Batterien";
    public static final String OVERALL_BATTERY_CAPACITY = "Gesamte Speicher Kapazität:";
    public static final String ORIENTATION = "Ausrichtung";
    public static final String ADDRESS = "Adresse";
    public static final String CITY = "Stadt";
    public static final String STREET = "Straße";
    public static final String HOUSE_NR = "HausNr";
    public static final String POSTAL_CODE = "PLZ";
    public static final String BRAND = "Marke";
    public static final String MODEL = "Modell";
    public static final String CAPACITY = "Kapazität";
    public static final String CHARGE = "Ladung";
    public static final String ELECTRICITY_MEASUREMENT = "Strom Ein- Verkauf Messwert";
    public static final String ELECTRICITY_MEASUREMENTS = "Strom Ein- Verkauf Messwerte";
    public static final String PRODUCTION_CONSUMPTION_MEASUREMENT = "Produktions/Verbrauchs Messwert";
    public static final String PRODUCTION_CONSUMPTION_MEASUREMENTS = "Produktions/Verbrauchs Messwerte";
    public static final String MEASUREMENTS = "Messwerte";
    public static final String EVALUATIONS = "Auswertungen";
    public static final String DATE_AXIS_LABEL = "Zeit (Datum und Uhrzeit)";



    //region Errors
    public static final String ERROR_API_TITLE = "Fehler bei API zugriff";
    public static final String ERROR_DATABASE_TITLE = "Fehler bei Datenbank zugriff";
    public static final String ERROR_ESTABLISH_DB_CONNECTION =
            "Es konnte keine Verbindung zur Datenbank aufgebaut werden.";
    public static final String TEMPLATE_ERROR_DATABASE_CREATE_SYSTEM_HEADER = "%s konnte nicht angelegt werden";
    public static final String TEMPLATE_ERROR_DATABASE_CREATE_HEADER = "%s (SystemID: %s) konnte nicht angelegt werden";
    public static final String TEMPLATE_ERROR_DATABASE_READ_HEADER = "%s (ID: %s) konnte nicht abgerufen werden";
    public static final String TEMPLATE_ERROR_DATABASE_READ_FOR_SYSTEM_HEADER
            = "%s (SystemID: %s) konnte nicht abgerufen werden";
    public static final String TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER = "%s konnten nicht abgerufen werden";
    public static final String TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER =
            "%s (SystemID: %s) konnten nicht abgerufen werden";
    public static final String TEMPLATE_ERROR_DATABASE_UPDATE_HEADER = "%s (ID: %s) konnte nicht aktualisiert werden";
    public static final String TEMPLATE_ERROR_DATABASE_DELETE_HEADER = "%s (ID: %s) konnte nicht entfernt werden";


    public static final String ERROR = "Fehler";
    public static final String ERROR_ALL_MASKED_FIELDS_MUST_FILLED = "Alle maskierten Felder müssen ausgefüllt werden.";
    public static final String ERROR_EMPTY_FIELDS_HEADER = "Nicht alle Felder ausgefüllt";
    public static final String ERROR_EMPTY_TEXTBOX = "Das Feld darf nicht leer sein.";
    public static final String ERROR_NUMERIC_TEXTBOX = "Nur ganze Zahlen erlaubt.";
    public static final String ERROR_OPEN_DETAILS = "Fehler beim Öffnen";
    public static final String ERROR_DELETE_TITLE = "Fehler beim löschen";
    public static final String ERROR_SELECT_SYSTEM_FIRST = "Wählen Sie zunächst ein System aus der Liste aus";
    public static final String ERROR_SELECT_PANEL_FIRST = "Wählen Sie zunächst ein Panel aus der Liste aus";
    public static final String ERROR_SELECT_BATTERY_FIRST = "Wählen Sie zunächst eine Batterie aus der Liste aus";
    public static final String ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST =
            "Fehler beim Öffnen der Detail Ansicht. Zuerst System aus der oberen Liste auswählen";
    public static final String ERROR_OPEN_DETAILS_SELECT_BATTERY_FIRST =
            "Fehler beim Öffnen der Batterie Detail Ansicht. Zuerst Batterie aus Liste auswählen";
    public static final String ERROR_OPEN_DETAILS_SELECT_PANEL_FIRST =
            "Fehler beim Öffnen der Panel Detail Ansicht. Zuerst Solar Panel aus Liste auswählen";
    public static final String ERROR_DELETE_SELECT_SYSTEM_FIRST =
            "Fehler beim Löschen. Zuerst System aus der oberen Liste auswählen";
    public static final String ERROR_DELETE_SELECTED_SYSTEM_NOT_IN_DB =
            "Fehler beim Löschen. Ausgewähltes System noch nicht in Datenbank vorhanden";
    public static final String ERROR_QUEUE_INTERRUPTED = "Queue Interrupted";
    public static final String ERROR_DATE_AXIS = "Es gab einen Fehler bei der Datumsachse";
    public static final String ERROR_LOAD_SCENE = "Fehler beim Laden des Fensters";


    //endregion Error

    //region Confirmations
    public static final String CONFIRM_DELETE_TITLE = "Wirklich löschen?";
    public static final String CONFIRM_DELETE_BATTERY_HEADER = "Ausgewählte Batterie wirklich löschen?";
    public static final String CONFIRM_DELETE_SYSTEM_HEADER = "Ausgewähltes System wirklich löschen?";
    public static final String CONFIRM_DELETE_PANEL_HEADER = "Ausgewähltes Solarpanel wirklich löschen?";
    //endregion Confirmations

    //region Information
    public static final String NEW_SYSTEM_SELECTED_NOW_EDITABLE =
            "Neues System ausgewählt, System kann im Detail bereich editiert werden.";
    public static final String UNSAVED_CHANGES = "Ungespeicherte Änderungen";
    public static final String SAVE_SUCCESSFUL = "Speichern erfolgreich";
    public static final String UNSAVED_CHANGES_HEADER = "Sollen die ungespeicherten Änderungen übernommen werden?";
    public static final String SAVE_SUCCESSFUL_SYSTEM_HEADER = "System wurde erfolgreich gespeichert";
    //endregion Information


    //region Templates
    public static final String TEMPLATE_CASH = "%.2f €";
    public static final String TEMPLATE_WATT = "%.2f W";
    public static final String TEMPLATE_DETAIL_SYSTEM = "System Nr: %s - Adresse: %s %s, %s %s";
    public static final String TEMPLATE_DETAIL_PANEL = "Panel Nr: %s - Marke: %s - Modell: %S - Leistung: %s";
    public static final String TEMPLATE_DETAIL_BATTERY = "Batterie Nr: %s - Marke: %s - Modell: %S";
    public static final String PANEL_COUNT = "Anzahl Panels";
    public static final String NR = "Nr";

    //endregion Templates

    //endregion

    //region Konstruktoren
    private AppText() {
    }
    //endregion


    //region Methoden

    //endregion
}
