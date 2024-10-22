package de.dhahn.dhsolarverwaltung.client.logic.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Dieses Interface dient zum Einfügen, einer DB Operation mit übergebener Connection, in die Queue
 */
@FunctionalInterface
public interface DbOperation{
    void runWithConnection(Connection connection) throws SQLException;
}
