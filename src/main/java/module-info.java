module de.dhahn.dhsolarverwaltung {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires org.kordamp.bootstrapfx.core;
    requires org.mariadb.jdbc;
    requires java.sql;
    requires java.base;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;


    exports de.dhahn.dhsolarverwaltung.client.gui;
    opens de.dhahn.dhsolarverwaltung.client.gui to javafx.fxml;
    exports de.dhahn.dhsolarverwaltung.client;
    opens de.dhahn.dhsolarverwaltung.client to javafx.fxml;

    exports de.dhahn.dhsolarverwaltung.interfaces;
    opens de.dhahn.dhsolarverwaltung.interfaces to java.rmi;
    exports de.dhahn.dhsolarverwaltung.client.model;

    exports de.dhahn.dhsolarverwaltung.client.logic.api;
    exports de.dhahn.dhsolarverwaltung.interfaces.model;
}