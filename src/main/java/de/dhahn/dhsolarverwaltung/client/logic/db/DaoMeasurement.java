package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.Measurement;
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
 * Diese Klasse stellt die CRUD Methoden für die Messwerte Tabelle der Datenbank zur verfügung
 */
public class DaoMeasurement implements Dao<Measurement> {

    //region Konstanten
    private static final String SQL_INSERT =
            "INSERT INTO  measurements (DateTime, Watt, isProduced, SystemID) VALUES (?,?,?,?);";
    private static final String SQL_READ_ALL =
            "SELECT * FROM measurements;";
    private static final String SQL_READ_ALL_IN_DATE_SPAN =
            "SELECT * FROM measurements WHERE isProduced=? AND DateTime >= ? AND DateTime <= ?;";
    private static final String SQL_READ_ALL_BY_SYSTEM_IN_DATE_SPAN =
            "SELECT * FROM measurements WHERE SystemID=? AND isProduced=? AND DateTime >= ? AND DateTime <= ?;";
    private static final String SQL_READ_BY_ID =
            "SELECT * FROM measurements WHERE Id=?;";
    private static final String SQL_READALL_BY_SYSTEM_ID =
            "SELECT * FROM measurements WHERE SystemID=? AND isProduced=?;";
    private static final String SQL_UPDATE =
            "UPDATE measurements SET DateTime=?, Watt=?, isProduced=? WHERE id=?;";
    private static final String SQL_DELETE =
            "DELETE FROM measurements WHERE Id=?";


    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN =
            "SELECT SystemID, isProduced, " +
                    "DATE_FORMAT(DATE_SUB(DateTime, INTERVAL MINUTE(DateTime) % 10 MINUTE), '%Y-%m-%d %H:%i:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt " +
                    "FROM measurements WHERE isProduced = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";
    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_HOUR =
            "SELECT SystemID, isProduced, DATE_FORMAT(DateTime, '%Y-%m-%d %H:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt " +
                    "FROM measurements WHERE isProduced = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_DAY =
            "SELECT SystemID, isProduced, DATE_FORMAT(DateTime, '%Y-%m-%d 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt " +
                    "FROM measurements WHERE isProduced = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_MONTH =
            "SELECT SystemID, isProduced, DATE_FORMAT(DateTime, '%Y-%m-01 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt " +
                    "FROM measurements WHERE isProduced = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String SQL_READ_EVALUATION_IN_DATE_SPAN_GROUPED_BY_YEAR =
            "SELECT SystemID, isProduced, DATE_FORMAT(DateTime, '%Y-01-01 00:00:00') as timestamp, "+
                    "SUM(Watt) as total_watt, AVG(Watt) as avg_watt, " +
                    "MAX(Watt) as max_watt, MIN(Watt) as min_watt " +
                    "FROM measurements WHERE isProduced = ? AND DateTime >= ? AND DateTime <= ? " +
                    "GROUP BY SystemID, timestamp ORDER BY timestamp ASC";

    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_SYSTEM_ID = "SystemID";
    private static final String COLUMN_DATETIME = "DateTime";
    private static final String COLUMN_WATT = "Watt";
    private static final String COLUMN_IS_PRODUCED = "isProduced";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_TOTAL_WATT = "total_watt";
    private static final String COLUMN_AVG_WATT = "avg_watt";
    private static final String COLUMN_MAX_WATT = "max_watt";
    private static final String COLUMN_MIN_WATT = "min_watt";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    /**
     * Legt den übergebenen Messwert in der Datenbank an
     *
     * @param connection  Verbindung zur Datenbank
     * @param measurement neues Objekt zum Anlegen
     * @param systemId    Id der zugeordneten Solaranlage
     */
    @Override
    public void create(Connection connection, Measurement measurement, long systemId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setObject(1, measurement.getDatetime());
            statement.setDouble(2, measurement.getWatt());
            statement.setBoolean(3, measurement.isProduced());
            statement.setLong(4, systemId);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                measurement.setId(generatedKeys.getInt(COLUMN_GENERATED_PRIMARY));

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENT, systemId), e.getMessage()));
        }
    }

    /**
     * Lädt den Messwert mit der übergebenen Id aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des gesuchten Objektes
     * @return Messwert
     */
    @Override
    public Measurement read(Connection connection, long id) {
        Measurement measurement = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                measurement = new Measurement(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED));
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENT, id), e.getMessage()));
        }
        return measurement;
    }

    /**
     * Lädt alle Messwerte aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste aller Messwerte
     */
    @Override
    public List<Measurement> readAll(Connection connection) {
        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                measurement = new Measurement(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED));
                measurements.add(measurement);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENTS), e.getMessage()));
        }
        return measurements;
    }

    /**
     * Lädt alle Messwerte im übergebenen Zeitraum aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param produced   produziert / verbraucht
     * @param startDate  Startdatum
     * @param endDate    Enddatum
     * @return Liste aller Messwerte für den Zeitraum
     */
    public List<Measurement> readAllInDateSpan(Connection connection, boolean produced, LocalDateTime startDate, LocalDateTime endDate) {
        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement;


        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL_IN_DATE_SPAN)) {
            statement.setBoolean(1, produced);
            statement.setObject(2, startDate);
            statement.setObject(3, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                measurement = new Measurement(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED));
                measurements.add(measurement);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENTS), e.getMessage()));
        }
        return measurements;
    }


    /**
     * Lädt alle Messwerte, der zugeordneten Solar Anlage im übergebenen Zeitraum aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         der Solaranlage
     * @param produced   produziert / verbraucht
     * @param startDate  Startdatum
     * @param endDate    Enddatum
     * @return Liste der Messwerte
     */
    public List<Measurement> readBySystemId(Connection connection, long id, boolean produced, LocalDateTime startDate, LocalDateTime endDate) {
        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement;

        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL_BY_SYSTEM_IN_DATE_SPAN)) {
            statement.setLong(1, id);
            statement.setBoolean(2, produced);
            statement.setObject(3, startDate);
            statement.setObject(4, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                measurement = new Measurement(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED));
                measurements.add(measurement);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENTS, id), e.getMessage()));
        }
        return measurements;
    }

    /**
     * Liefert Auswertungen der Messwerte im gegeben Zeitraum. Hier kann nach Tage / Stunden / Minuten gruppiert werden.
     *
     * @param connection Datenbank Verbindung
     * @param produced Produziert oder Verbraucht
     * @param startDate Startdatum der Auswertung
     * @param endDate Enddatum der Auswertung
     * @param groupBy Gruppiert nach -> AppCommand Minute / Hour / Day
     * @return Liste der MeasurementEvaluations
     */
    public List<MeasurementEvaluation> readEvaluations(Connection connection, boolean produced, LocalDateTime startDate, LocalDateTime endDate, int groupBy) {
        List<MeasurementEvaluation> evaluations = new ArrayList<>();
        MeasurementEvaluation evaluation;
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
            statement.setBoolean(1, produced);
            statement.setObject(2, startDate);
            statement.setObject(3, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                evaluation = new MeasurementEvaluation(resultSet.getInt(COLUMN_SYSTEM_ID),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED),
                        resultSet.getObject(COLUMN_TIMESTAMP, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_TOTAL_WATT),
                        resultSet.getDouble(COLUMN_AVG_WATT),
                        resultSet.getDouble(COLUMN_MAX_WATT),
                        resultSet.getDouble(COLUMN_MIN_WATT));
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
     * Lädt alle Messwerte, die der Solaranlage zugeordnet sind aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         der Solaranlage
     * @param produced   produziert / verbraucht
     * @return Liste der Messwerte
     */
    public List<Measurement> readBySystemId(Connection connection, long id, boolean produced) {
        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READALL_BY_SYSTEM_ID)) {

            statement.setLong(1, id);
            statement.setBoolean(2, produced);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                measurement = new Measurement(resultSet.getInt(COLUMN_ID),
                        resultSet.getObject(COLUMN_DATETIME, LocalDateTime.class),
                        resultSet.getDouble(COLUMN_WATT),
                        resultSet.getBoolean(COLUMN_IS_PRODUCED));
                measurements.add(measurement);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENTS, id), e.getMessage()));
        }
        return measurements;
    }

    /**
     * Aktualisiert den übergebenen Messwert in der Datenbank
     *
     * @param connection  Verbindung zur Datenbank
     * @param measurement Objekt zum Aktualisieren
     */
    @Override
    public void update(Connection connection, Measurement measurement) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setObject(1, measurement.getDatetime());
            statement.setDouble(2, measurement.getWatt());
            statement.setBoolean(3, measurement.isProduced());
            statement.setLong(4, measurement.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_UPDATE_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENT, measurement.getId()), e.getMessage()));
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param connection  Verbindung zur Datenbank
     * @param measurement Objekt zum Löschen
     */
    @Override
    public void delete(Connection connection, Measurement measurement) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, measurement.getId());
            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_DELETE_HEADER,
                            AppText.PRODUCTION_CONSUMPTION_MEASUREMENT, measurement.getId()), e.getMessage()));
        }
    }

    //endregion
}
