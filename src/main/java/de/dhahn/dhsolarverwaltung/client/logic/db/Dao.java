package de.dhahn.dhsolarverwaltung.client.logic.db;

import java.sql.Connection;
import java.util.List;

/**
 * Interface für die Data Access Object Klassen
 *
 * @param <T> Model Klasse der Tabelle
 */
public interface Dao<T> {
    String COLUMN_GENERATED_PRIMARY = "insert_id";


    /**
     * Legt das übergebene Objekt in der Datenbank an
     * @param connection Verbindung zur Datenbank
     * @param object neues Objekt zum Anlegen
     * @param systemId Id der zugeordneten Solaranlage
     */
    void create(Connection connection, T object, long systemId);


    /**
     * Sucht das Objekt mit übergebener ID aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param id des gesuchten Objektes
     * @return gesuchtes Objekt
     */
    T read(Connection connection, long id);

    /**
     * Lädt alle Objekte aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @return Liste der Objekte
     */
    List<T> readAll(Connection connection);


    /**
     * Aktualisiert das übergebene Objekt aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param object Objekt zum Aktualisieren
     */
    void update(Connection connection, T object);


    /**
     * Löscht das übergebene Objekt aus der Datenbank
     *
     * @param connection Verbindung zur Datenbank
     * @param object Objekt zum Löschen
     */
    void delete(Connection connection, T object);
}
