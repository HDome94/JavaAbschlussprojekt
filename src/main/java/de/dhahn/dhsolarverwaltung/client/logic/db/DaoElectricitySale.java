package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.ElectricitySale;
import de.dhahn.dhsolarverwaltung.client.model.ElectricitySaleEvaluation;
import de.dhahn.dhsolarverwaltung.client.model.MeasurementEvaluation;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse stellt die CRUD Methoden für die Strom Ein- /Verkauf Tabelle der Datenbank zur verfügung
 */
public class DaoElectricitySale implements Dao<ElectricitySale> {


    //region Konstanten
    private static final String SQL_INSERT =
            "INSERT INTO  electricitysales (DateTime, Watt, IsPurchased, PricePerKW, SystemID) VALUES (?,?,?,?,?);";
    private static final String SQL_READ_ALL =
            "SELECT * FROM electricitysales;";
    private static final String SQL_READ_ALL_IN_DATE_SPAN =
            "SELECT * FROM electricitysales WHERE IsPurchased=? AND DateTime >= ? AND DateTime <= ?;";
    private static final String SQL_READ_ALL_BY_SYSTEM_IN_DATE_SPAN =
            "SELECT * FROM electricitysales WHERE SystemID=? AND IsPurchased=? AND DateTime >= ? AND DateTime <= ?;";
    private static final String SQL_READ_BY_ID =
            "SELECT * FROM electricitysales WHERE Id=?;";
    private static final String SQL_READALL_BY_SYSTEM_ID =
            "SELECT * FROM electricitysales WHERE SystemID=? AND IsPurchased=?;";
    private static final String SQL_UPDATE =
            "UPDATE electricitysales SET DateTime=?, Watt=?, PricePerKW=? IsPurchased=? WHERE id=?;";
    private static final String SQL_DELETE =
            "DELETE FROM electricitysales WHERE Id=?";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN =
            "SELECT SystemID, IsPurchased, " +
                    "DATE_FORMAT(DATE_SUB(DateTime, INTERVAL MINUTE(DateTime) % 10 MINUTE), '%Y-%m-%d %H:%i:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt, " +
                    "AVG(PricePerKW) as avg_price, " +
                    "MAX(PricePerKW) as max_price, MIN(PricePerKW) as min_price " +
                    "FROM electricitysales WHERE IsPurchased = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";
    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_HOUR =
            "SELECT SystemID, IsPurchased, DATE_FORMAT(DateTime, '%Y-%m-%d %H:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt, " +
                    "AVG(PricePerKW) as avg_price, " +
                    "MAX(PricePerKW) as max_price, MIN(PricePerKW) as min_price " +
                    "FROM electricitysales WHERE IsPurchased = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_DAY =
            "SELECT SystemID, IsPurchased, DATE_FORMAT(DateTime, '%Y-%m-%d 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt, " +
                    "AVG(PricePerKW) as avg_price, " +
                    "MAX(PricePerKW) as max_price, MIN(PricePerKW) as min_price " +
                    "FROM electricitysales WHERE IsPurchased = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_MONTH =
            "SELECT SystemID, IsPurchased, DATE_FORMAT(DateTime, '%Y-%m-01 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt, " +
                    "AVG(PricePerKW) as avg_price, " +
                    "MAX(PricePerKW) as max_price, MIN(PricePerKW) as min_price " +
                    "FROM electricitysales WHERE IsPurchased = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_YEAR =
            "SELECT SystemID, IsPurchased, DATE_FORMAT(DateTime, '%Y-01-01 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt, " +
                    "AVG(PricePerKW) as avg_price, " +
                    "MAX(PricePerKW) as max_price, MIN(PricePerKW) as min_price " +
                    "FROM electricitysales WHERE IsPurchased = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";


    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_SYSTEM_ID = "SystemID";
    private static final String COLUMN_DATETIME = "DateTime";
    private static final String COLUMN_WATT = "Watt";
    private static final String COLUMN_PRICE_PER_KW = "PricePerKW";
    private static final String COLUMN_IS_PURCHASED = "IsPurchased";

    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_TOTAL_WATT = "total_watt";
    private static final String COLUMN_AVG_WATT = "avg_watt";
    private static final String COLUMN_MAX_WATT = "max_watt";
    private static final String COLUMN_MIN_WATT = "min_watt";
    private static final String COLUMN_AVG_PRICE = "avg_price";
    private static final String COLUMN_MAX_PRICE = "max_price";
    private static final String COLUMN_MIN_PRICE = "min_price";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    /**
     * Legt den übergebenen Messwert in der Datenbank an
     *
     * @param connection      Verbindung zur Datenbank
     * @param electricitySale neues Objekt zum Anlegen
     * @param systemId        Id der zugeordneten Solaranlage
     */
    @Override
    public void create(Connection connection, ElectricitySale electricitySale, long systemId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, electricitySale.getDateTime());
            statement.setDouble(2, electricitySale.getWatt());
            statement.setBoolean(3, electricitySale.isIsPurchased());
            statement.setDouble(4, electricitySale.getPricePerKW());
            statement.setLong(5, systemId);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                electricitySale.setId(generatedKeys.getInt(COLUMN_GENERATED_PRIMARY));

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER,
                            AppText.ELECTRICITY_MEASUREMENT, systemId), e.getMessage()));
        }
    }

    /**
     * Lädt den Messwert mit übergebener Id aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des gesuchten Objektes
     * @return Messwert Strom Produktion / Verbrauch
     */
    @Override
    public ElectricitySale read(Connection connection, long id) {
        ElectricitySale electricitySale = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                electricitySale = new ElectricitySale(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getDouble(COLUMN_PRICE_PER_KW));
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_HEADER,
                            AppText.ELECTRICITY_MEASUREMENT, id), e.getMessage()));
        }
        return electricitySale;
    }

    /**
     * Lädt alle Messwerte, die dem System zugeordnet sind aus der Datenbank.
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des Systems
     * @param purchased  verkauft oder eingekauft
     * @return Liste der Messwerte
     */
    public List<ElectricitySale> readBySystemId(Connection connection, long id, boolean purchased) {
        List<ElectricitySale> electricitySales = new ArrayList<>();
        ElectricitySale electricitySale;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READALL_BY_SYSTEM_ID)) {

            statement.setLong(1, id);
            statement.setBoolean(2, purchased);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                electricitySale = new ElectricitySale(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getDouble(COLUMN_PRICE_PER_KW));
                electricitySales.add(electricitySale);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER,
                            AppText.ELECTRICITY_MEASUREMENTS, id), e.getMessage()));
        }
        return electricitySales;
    }

    /**
     * Lädt alle Messwerte zu dem System im übergebenen Zeitraum aus.
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des Systems
     * @param purchased  verkauft oder eingekauft
     * @param startDate  Startdatum
     * @param endDate    Enddatum
     * @return Liste der Messwerte
     */
    public List<ElectricitySale> readBySystemId(Connection connection, long id, boolean purchased, LocalDateTime startDate, LocalDateTime endDate) {
        List<ElectricitySale> electricitySales = new ArrayList<>();
        ElectricitySale electricitySale;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL_BY_SYSTEM_IN_DATE_SPAN)) {

            statement.setLong(1, id);
            statement.setBoolean(2, purchased);
            statement.setObject(3, startDate);
            statement.setObject(4, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                electricitySale = new ElectricitySale(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getDouble(COLUMN_PRICE_PER_KW));
                electricitySales.add(electricitySale);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER,
                            AppText.ELECTRICITY_MEASUREMENTS, id), e.getMessage()));
        }
        return electricitySales;
    }

    /**
     * Liest alle Messwerte aus der Datenbank aus
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste aller Messwerte
     */
    @Override
    public List<ElectricitySale> readAll(Connection connection) {
        List<ElectricitySale> electricitySales = new ArrayList<>();
        ElectricitySale electricitySale;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                electricitySale = new ElectricitySale(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getDouble(COLUMN_PRICE_PER_KW));
                electricitySales.add(electricitySale);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER, AppText.ELECTRICITY_MEASUREMENTS),
                    e.getMessage()));
        }
        return electricitySales;
    }

    /**
     * Liest alle Messwerte im übergebenen Zeitraum aus der Datenbank aus
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste aller Messwerte im Zeitraum
     */
    public List<ElectricitySale> readAll(Connection connection, boolean purchased, LocalDateTime startDate, LocalDateTime endDate) {
        List<ElectricitySale> electricitySales = new ArrayList<>();
        ElectricitySale electricitySale = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL_IN_DATE_SPAN)) {

            statement.setBoolean(1, purchased);
            statement.setObject(2, startDate);
            statement.setObject(2, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                electricitySale = new ElectricitySale(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getDouble(COLUMN_PRICE_PER_KW));
                electricitySales.add(electricitySale);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER, AppText.ELECTRICITY_MEASUREMENTS),
                    e.getMessage()));
        }
        return electricitySales;
    }

    /**
     * Liefert Auswertungen der Messwerte im gegeben Zeitraum. Hier kann nach Tage / Stunden / Minuten gruppiert werden.
     *
     * @param connection Datenbank Verbindung
     * @param purchased Verkauft oder Gekauft
     * @param startDate Startdatum der Auswertung
     * @param endDate Enddatum der Auswertung
     * @param groupBy Gruppiert nach -> AppCommand Minute / Hour / Day
     * @return Liste der MeasurementEvaluations
     */
    public List<ElectricitySaleEvaluation> readEvaluations(Connection connection, boolean purchased, LocalDateTime startDate, LocalDateTime endDate, int groupBy) {
        List<ElectricitySaleEvaluation> evaluations = new ArrayList<>();
        ElectricitySaleEvaluation evaluation;
        String sqlState = switch (groupBy) {
            case AppCommand.GROUP_BY_MIN -> SQL_READ_EVALUATION_IN_DATE_SPAN;
            case AppCommand.GROUP_BY_HOUR -> SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_HOUR;
            case AppCommand.GROUP_BY_DAY -> SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_DAY;
            case AppCommand.GROUP_BY_MONTH -> SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_MONTH;
            case AppCommand.GROUP_BY_YEAR -> SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_YEAR;
            default -> "";
        };

        if (sqlState.isBlank()) return evaluations;


        try (PreparedStatement statement = connection.prepareStatement(sqlState)) {
            statement.setBoolean(1, purchased);
            statement.setObject(2, startDate);
            statement.setObject(3, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                evaluation = new ElectricitySaleEvaluation(resultSet.getInt(COLUMN_SYSTEM_ID),
                        resultSet.getBoolean(COLUMN_IS_PURCHASED),
                        resultSet.getObject(COLUMN_TIMESTAMP, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_TOTAL_WATT),
                        resultSet.getDouble(COLUMN_AVG_WATT),
                        resultSet.getDouble(COLUMN_MAX_WATT),
                        resultSet.getDouble(COLUMN_MIN_WATT),
                        resultSet.getDouble(COLUMN_AVG_PRICE),
                        resultSet.getDouble(COLUMN_MAX_PRICE),
                        resultSet.getDouble(COLUMN_MIN_PRICE));
                evaluations.add(evaluation);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER, AppText.EVALUATIONS),
                    e.getMessage()));
        }
        return evaluations;
    }

    /**
     * Aktualisiert den übergebenen Messwert in der Datenbank
     *
     * @param connection      Verbindung zur Datenbank
     * @param electricitySale Objekt zum Aktualisieren
     */
    @Override
    public void update(Connection connection, ElectricitySale electricitySale) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setObject(1, electricitySale.getDateTime());
            statement.setDouble(2, electricitySale.getWatt());
            statement.setDouble(3, electricitySale.getPricePerKW());
            statement.setBoolean(4, electricitySale.isIsPurchased());
            statement.setLong(5, electricitySale.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_UPDATE_HEADER, AppText.ELECTRICITY_MEASUREMENT,
                            electricitySale.getId()), e.getMessage()));
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param connection      Verbindung zur Datenbank
     * @param electricitySale Objekt zum Löschen
     */
    @Override
    public void delete(Connection connection, ElectricitySale electricitySale) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, electricitySale.getId());
            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_DELETE_HEADER, AppText.ELECTRICITY_MEASUREMENT,
                            electricitySale.getId()), e.getMessage()));
        }
    }
    //endregion
}
