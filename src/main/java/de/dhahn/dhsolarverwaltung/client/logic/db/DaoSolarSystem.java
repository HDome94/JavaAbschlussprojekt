package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.Direction;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse stellt die CRUD Methoden für die Solar System Tabelle der Datenbank zur verfügung
 */
public class DaoSolarSystem implements Dao<SolarSystem> {

    //region Konstanten
    private static final String SQL_INSERT =
            "INSERT INTO solarsystems (Direction, AddressID) VALUES (?, ?);";
    private static final String SQL_READ_ALL =
            "SELECT s.Id, Direction, Street, HouseNr, PostalCode, City " +
                    "FROM solarsystems AS s INNER JOIN addresses AS a ON s.AddressID = a.Id " +
                    "INNER JOIN cities as c ON a.CityID = c.Id;";

    private static final String SQL_READ_BY_ID =
            "SELECT s.Id, Direction, Street, HouseNr, PostalCode, City " +
                    "FROM solarsystems AS s INNER JOIN addresses AS a ON s.AddressID = a.Id " +
                    "INNER JOIN cities as c ON a.CityID = c.Id WHERE s.Id=?;";
    private static final String SQL_UPDATE =
            "UPDATE solarsystems SET Direction=?, AddressID=? WHERE id=?;";
    private static final String SQL_DELETE =
            "DELETE FROM solarsystems WHERE Id=?";


    private static final String SQL_INSERT_CITY =
            "INSERT INTO cities (PostalCode, City) VALUES (?, ?);";
    private static final String SQL_INSERT_ADDRESS =
            "INSERT INTO addresses (Street, HouseNr, CityID) VALUES (?, ?, ?);";
    private static final String SQL_READ_ADDRESS =
            "SELECT * FROM addresses INNER JOIN cities ON addresses.CityID = cities.Id WHERE addresses.Id=?;";
    private static final String SQL_READ_ID_CITY_BY_POSTAL_CODE_AND_CITY =
            "SELECT Id FROM cities WHERE PostalCode=? AND City=?;";
    private static final String SQL_READ_ID_ADDRESS_BY_STREET_AND_HOUSE_NR_AND_CITY =
            "SELECT Id FROM addresses WHERE Street=? AND HouseNr=? AND CityID=?;";


    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_DIRECTION = "Direction";
    private static final String COLUMN_ADDRESSID = "AddressID";
    private static final String COLUMN_ADDRESS_STREET = "Street";
    private static final String COLUMN_ADDRESS_HOUSENR = "HouseNr";
    private static final String COLUMN_CITY_POSTAL_CODE = "PostalCode";
    private static final String COLUMN_CITY_CITY = "City";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    /**
     * Legt das übergebene System in der Datenbank an. SystemId wid ignoriert -> wird wegen override benötigt
     *
     * @param connection Verbindung zur Datenbank
     * @param system     neues Objekt zum Anlegen
     * @param systemId   Id der zugeordneten Solaranlage
     */
    @Override
    public void create(Connection connection, SolarSystem system, long systemId) {
        create(connection, system);
    }

    /**
     * Legt das übergebene System in der Datenbank an
     *
     * @param connection Verbindung zur Datenbank
     * @param system     neues Objekt zum Anlegen
     */
    public void create(Connection connection, SolarSystem system) {
        long adressId = createAddressIfNotExists(connection, system);

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, system.getDirection().toString());
            statement.setLong(2, adressId);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                system.setId(generatedKeys.getInt(COLUMN_GENERATED_PRIMARY));

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_SYSTEM_HEADER,
                            AppText.SOLAR_SYSTEM), e.getMessage()));

        }
    }

    /**
     * Sucht eine Adresse aus der Datenbank und legt diese an, falls sie noch nicht in der Datenbank existiert
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Solar Anlage
     * @return Id der Adresse
     */
    private long createAddressIfNotExists(Connection connection, SolarSystem system) {
        long cityId = getIdFromCity(connection, system);
        if (cityId == -1)
            cityId = createCity(connection, system);

        long addressId = getIdFromAddress(connection, system, cityId);
        if (addressId == -1)
            addressId = createAddress(connection, system, cityId);

        return addressId;
    }

    /**
     * Sucht die Stadt in der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Solaranlage
     * @return Id der Stadt | -1 wenn nicht vorhanden
     */
    private long getIdFromCity(Connection connection, SolarSystem system) {
        long id = -1;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ID_CITY_BY_POSTAL_CODE_AND_CITY)) {

            statement.setString(1, system.getPostalCode());
            statement.setString(2, system.getCity());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                id = resultSet.getInt(COLUMN_ID);

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_FOR_SYSTEM_HEADER,
                            AppText.CITY, system.getId()), e.getMessage()));
        }

        return id;
    }

    /**
     * Sucht die Adresse in der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Solaranlage
     * @return Id der Adresse | -1 wenn nicht vorhanden
     */
    private long getIdFromAddress(Connection connection, SolarSystem system, long cityId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ID_ADDRESS_BY_STREET_AND_HOUSE_NR_AND_CITY)) {

            statement.setString(1, system.getStreet());
            statement.setString(2, system.getHouseNr());
            statement.setLong(3, cityId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getInt(COLUMN_ID);

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_FOR_SYSTEM_HEADER,
                            AppText.ADDRESS, system.getId()), e.getMessage()));
        }

        return -1;
    }

    /**
     * Legt die Stadt in der Datenbank an
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Solaranlage
     * @return Id der Stadt | -1 bei Fehlern
     */
    private long createCity(Connection connection, SolarSystem system) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_CITY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, system.getPostalCode());
            statement.setString(2, system.getCity());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                return generatedKeys.getInt(COLUMN_GENERATED_PRIMARY);

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER,
                            AppText.CITY, system.getId()), e.getMessage()));
        }

        return -1;
    }

    /**
     * Legt die Adresse in der Datenbank an
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Solaranlage
     * @return Id der Adresse | -1 bei Fehlern
     */
    private long createAddress(Connection connection, SolarSystem system, long cityId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, system.getStreet());
            statement.setString(2, system.getHouseNr());
            statement.setLong(3, cityId);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
                return generatedKeys.getInt(COLUMN_GENERATED_PRIMARY);

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_CREATE_HEADER,
                            AppText.ADDRESS, system.getId()), e.getMessage()));
        }

        return -1;
    }

    /**
     * Lädt die Solaranlage mit übergebener Id aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id         des gesuchten Objektes
     * @return Solaranlage
     */
    @Override
    public SolarSystem read(Connection connection, long id) {
        SolarSystem system;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                system = new SolarSystem();
                system.setId(resultSet.getInt(COLUMN_ID));
                system.setDirection(Direction.valueOf(resultSet.getString(COLUMN_DIRECTION)));
                system.setStreet(resultSet.getString(COLUMN_ADDRESS_STREET));
                system.setHouseNr(resultSet.getString(COLUMN_ADDRESS_HOUSENR));
                system.setPostalCode(resultSet.getString(COLUMN_CITY_POSTAL_CODE));
                system.setCity(resultSet.getString(COLUMN_CITY_CITY));
                return system;
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_HEADER,
                            AppText.SOLAR_SYSTEM, id), e.getMessage()));
        }
        return null;
    }

    /**
     * Lädt alle Solar Anlagen aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste der Solar Anlagen
     */
    @Override
    public List<SolarSystem> readAll(Connection connection) {
        List<SolarSystem> systems = new ArrayList<>();
        SolarSystem system;
        try (PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                system = new SolarSystem();
                system.setId(resultSet.getInt(COLUMN_ID));
                system.setDirection(Direction.valueOf(resultSet.getString(COLUMN_DIRECTION)));
                system.setStreet(resultSet.getString(COLUMN_ADDRESS_STREET));
                system.setHouseNr(resultSet.getString(COLUMN_ADDRESS_HOUSENR));
                system.setPostalCode(resultSet.getString(COLUMN_CITY_POSTAL_CODE));
                system.setCity(resultSet.getString(COLUMN_CITY_CITY));
                systems.add(system);
            }

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER,
                            AppText.SOLAR_SYSTEMS), e.getMessage()));
        }
        return systems;
    }

    /**
     * Aktualisiert die Solaranlage in der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Objekt zum Aktualisieren
     */
    @Override
    public void update(Connection connection, SolarSystem system) {
        long addressId = createAddressIfNotExists(connection, system);
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setString(1, system.getDirection().toString());
            statement.setLong(2, addressId);
            statement.setLong(3, system.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_UPDATE_HEADER,
                            AppText.SOLAR_SYSTEM, system.getId()), e.getMessage()));
        }
    }

    /**
     * Löscht die Solaranlage aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param system     Objekt zum Löschen
     */
    @Override
    public void delete(Connection connection, SolarSystem system) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, system.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_DATABASE_TITLE,
                    String.format(AppText.TEMPLATE_ERROR_DATABASE_DELETE_HEADER,
                            AppText.SOLAR_SYSTEM, system.getId()), e.getMessage()));
        }
    }

    //endregion
}
