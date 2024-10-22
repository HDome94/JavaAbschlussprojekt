package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse stellt die CRUD Methoden für die Solar-Panel-Tabelle der Datenbank zur verfügung
 */
public class DaoSolarPanel implements Dao<SolarPanel> {

    //region Konstanten
    private static final String SQL_INSERT =
            "INSERT INTO solarpanels (Brand, Model, MaximumWatt, SystemID) VALUES (?,?,?,?);";
    private static final String SQL_READ_ALL =
            "SELECT * FROM solarpanels;";
    private static final String SQL_READ_BY_ID =
            "SELECT * FROM solarpanels WHERE Id=?;";
    private static final String SQL_READALL_BY_SYSTEM_ID =
            "SELECT * FROM solarpanels WHERE SystemID=?;";
    private static final String SQL_UPDATE =
            "UPDATE solarpanels SET Brand=?, Model=?, MaximumWatt=? WHERE id=?;";
    private static final String SQL_DELETE =
            "DELETE FROM solarpanels WHERE Id=?";


    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_BRAND = "Brand";
    private static final String COLUMN_MODEL = "Model";
    private static final String COLUMN_MAXIMUM_WATT = "MaximumWatt";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    /**
     * Legt das übergebene Solarpanel in der Datenbank an
     *
     * @param connection Verbindung zur Datenbank
     * @param panel      neues Objekt zum Anlegen
     * @param systemId   Id der zugeordneten Solaranlage
     */
    @Override
    public void create(Connection connection, SolarPanel panel, long systemId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, panel.getBrand());
            statement.setString(2, panel.getModel());
            statement.setInt(3, panel.getMaximumWatt());
            statement.setLong(4, systemId);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                panel.setId(generatedKeys.getInt(COLUMN_GENERATED_PRIMARY));

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER,
                            AppText.SOLAR_PANEL, systemId), e.getMessage()));
        }
    }

    /**
     * Lädt das Solarpanel mit übergebener Id aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des gesuchten Objektes
     * @return Solarpanel
     */
    @Override
    public SolarPanel read(Connection connection, long id) {
        SolarPanel panel = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                panel = new SolarPanel(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getInt(COLUMN_MAXIMUM_WATT));
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_HEADER,
                            AppText.SOLAR_PANEL, id), e.getMessage()));
        }
        return panel;
    }

    /**
     * Lädt alle Solarpanels aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste aller Solarpanel
     */
    @Override
    public List<SolarPanel> readAll(Connection connection) {
        List<SolarPanel> panels = new ArrayList<>();
        SolarPanel panel;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                panel = new SolarPanel(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getInt(COLUMN_MAXIMUM_WATT));
                panels.add(panel);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER,
                            AppText.SOLAR_PANELS), e.getMessage()));
        }
        return panels;
    }

    /**
     * Lädt alle Solarpanels, die dem System zugeordnet sind aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         der Solaranlage
     * @return Liste der Solarpanels
     */
    public List<SolarPanel> readBySystemId(Connection connection, long id) {
        List<SolarPanel> panels = new ArrayList<>();
        SolarPanel panel;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READALL_BY_SYSTEM_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                panel = new SolarPanel(resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_BRAND),
                        resultSet.getString(COLUMN_MODEL),
                        resultSet.getInt(COLUMN_MAXIMUM_WATT));
                panels.add(panel);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_FOR_SYSTEM_HEADER,
                            AppText.SOLAR_PANELS, id), e.getMessage()));
        }
        return panels;
    }

    /**
     * Aktualisiert das übergebene Solarpanel in der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param panel      Objekt zum Aktualisieren
     */
    @Override
    public void update(Connection connection, SolarPanel panel) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setString(1, panel.getBrand());
            statement.setString(2, panel.getModel());
            statement.setInt(3, panel.getMaximumWatt());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_UPDATE_HEADER,
                            AppText.SOLAR_PANEL, panel.getId()), e.getMessage()));
        }
    }

    /**
     * Löscht das übergebene Solarpanel aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param panel      Objekt zum Löschen
     */
    @Override
    public void delete(Connection connection, SolarPanel panel) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, panel.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_DELETE_HEADER,
                            AppText.SOLAR_PANEL, panel.getId()), e.getMessage()));
        }
    }

    //endregion
}
