package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse stellt die CRUD Methoden für die Batterie Tabelle der Datenbank zur verfügung
 */
public class DaoBattery implements Dao<Battery> {

    //region Konstanten
    private static final String SQL_INSERT =
            "INSERT INTO batteries (Brand, Model, Capacity, Charge, SystemID) VALUES (?,?,?,?,?);";
    private static final String SQL_READ_ALL =
            "SELECT * FROM batteries;";
    private static final String SQL_READ_BY_ID =
            "SELECT * FROM batteries WHERE Id=?;";
    private static final String SQL_READALL_BY_SYSTEM_ID =
            "SELECT * FROM batteries WHERE SystemID=?;";
    private static final String SQL_UPDATE =
            "UPDATE batteries SET Brand=?, Model=?, Capacity=?, Charge=? WHERE id=?;";
    private static final String SQL_DELETE =
            "DELETE FROM batteries WHERE Id=?";


    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_BRAND = "Brand";
    private static final String COLUMN_MODEL = "Model";
    private static final String COLUMN_CAPACITY = "Capacity";
    private static final String COLUMN_CHARGE = "Charge";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    /**
     * Legt die übergebene Batterie in der Datenbank an
     *
     * @param connection Verbindung zur Datenbank
     * @param battery    neues Objekt zum Anlegen
     * @param systemId   Id der zugeordneten Solaranlage
     */
    @Override
    public void create(Connection connection, Battery battery, long systemId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, battery.getBrand());
            statement.setString(2, battery.getModel());
            statement.setDouble(3, battery.getCapacity());
            statement.setDouble(4, battery.getCharge());
            statement.setLong(5, systemId);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                battery.setId(generatedKeys.getInt(COLUMN_GENERATED_PRIMARY));

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER, AppText.BATTERY, systemId),
                    e.getMessage()));
        }

    }

    /**
     * Lädt die Batterie mit übergebener Id aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des gesuchten Objektes
     * @return Batterie
     */
    @Override
    public Battery read(Connection connection, long id) {
        Battery battery = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                battery = new Battery(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getDouble(COLUMN_CAPACITY),
                        resultSet.getInt(COLUMN_CHARGE));
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_HEADER, AppText.BATTERY, id), e.getMessage()));

        }
        return battery;
    }

    /**
     * Lädt alle Batterien aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste aller Batterien
     */
    @Override
    public List<Battery> readAll(Connection connection) {
        List<Battery> batteries = new ArrayList<>();
        Battery battery;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                battery = new Battery(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getDouble(COLUMN_CAPACITY),
                        resultSet.getInt(COLUMN_CHARGE));

                batteries.add(battery);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER, AppText.BATTERIES), e.getMessage()));

        }
        return batteries;
    }

    /**
     * Lädt alle Batterien für die Solaranlage aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         der Solaranlage
     * @return Liste aller Batterien der Anlage
     */
    public List<Battery> readBySystemId(Connection connection, long id) {
        List<Battery> batteries = new ArrayList<>();
        Battery battery;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READALL_BY_SYSTEM_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                battery = new Battery(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getDouble(COLUMN_CAPACITY),
                        resultSet.getInt(COLUMN_CHARGE));

                batteries.add(battery);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER, AppText.BATTERIES, id),
                    e.getMessage()));
        }
        return batteries;
    }

    /**
     * Aktualisiert die übergebene Batterie in der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param battery    Objekt zum Aktualisieren
     */
    @Override
    public void update(Connection connection, Battery battery) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setString(1, battery.getBrand());
            statement.setString(2, battery.getModel());
            statement.setDouble(3, battery.getCapacity());
            statement.setDouble(4, battery.getCharge());
            statement.setDouble(5, battery.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_UPDATE_HEADER, AppText.BATTERY, battery.getId()),
                    e.getMessage()));
        }
    }

    /**
     * Löscht die übergebene Batterie aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param battery    Objekt zum Löschen
     */
    @Override
    public void delete(Connection connection, Battery battery) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, battery.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_DELETE_HEADER, AppText.BATTERY, battery.getId()),
                    e.getMessage()));
        }
    }

    //endregion
}
