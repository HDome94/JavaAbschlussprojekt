package de.dhahn.dhsolarverwaltung.client.test;

import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.model.Direction;
import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

/**
 * Diese Klasse erstellt Testdaten und Messwerte für die eingestellten Tage mit entsprechendem Intervall.
 * Diese werden in der Datenbank angelegt und werden benötigt, damit die Charts (alte) Daten zum Anzeigen haben
 */
public class TestMain {

    public static void main(String[] args) throws InterruptedException {
        TestData.setDaysToBuild(30); // Letzten 30 Tage
        TestData.setMeasureIntervall(10); // Alle 10 min
        TestData.setSystemsToBuild(5);
        TestData.setBatteriesToBuild(2);
        TestData.setPanelsToBuild(15);

        // Server Settings Durations ggf Anpassen um halbwegs synchron zu laufen
        // Standard Werte sind:
        //      Abfrage API (Client normal Mode) → 1min
        //      Abfrage API (Testdaten) → 0.1sek
        //      Tageszeitwechsel → 3 min
        //      Jahreszeitwechsel → 10 min
        // Zeitangaben werden beim Server entsprechend reduziert

        String[] serverArgs = new String[]{"buildTestData"};

        Thread serverThread = new Thread(() -> {
            try {
                de.dhahn.dhsolarverwaltung.server.main.Main.main(serverArgs);
            } catch (RemoteException | AlreadyBoundException e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();
        Thread.sleep(5000);

        TestData.buildTestData();
    }
}
