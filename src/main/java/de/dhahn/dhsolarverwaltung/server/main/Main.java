package de.dhahn.dhsolarverwaltung.server.main;

import de.dhahn.dhsolarverwaltung.server.services.SolarAnlagenService;
import de.dhahn.dhsolarverwaltung.server.settings.ServerSettings;
import de.dhahn.dhsolarverwaltung.server.settings.ServerText;
import de.dhahn.dhsolarverwaltung.server.threads.DayTimeThread;
import de.dhahn.dhsolarverwaltung.server.threads.SeasonThread;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Die Main Klasse des Servers (Fake-Server für SolarAPI)
 */
public class Main {

    /**
     * Startet den Solar Service der API mit den hinterlegten Einstellungen.
     *
     * @throws RemoteException       - Remote Exception
     * @throws AlreadyBoundException - falls Port schon genutzt wird
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        if (args.length != 0 && args[0].equals("buildTestData")) {
            ServerSettings.DAY_TIME_DURATION = 30_000;
            ServerSettings.SEASON_DURATION = 100_000;
        }

        Registry registry = LocateRegistry.createRegistry(ServerSettings.PORT);
        registry.bind(ServerSettings.ROUTE_SOLAR_SERVICE, new SolarAnlagenService());

        DayTimeThread dayTimeThread = new DayTimeThread(ServerSettings.DAY_TIME_DURATION); //
        SeasonThread seasonThread = new SeasonThread(ServerSettings.SEASON_DURATION); //

        dayTimeThread.start();
        seasonThread.start();

        System.out.println(ServerText.SERVER_STARTED);
    }
}
