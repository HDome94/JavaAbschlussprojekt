package de.dhahn.dhsolarverwaltung.client.logic.api;

import de.dhahn.dhsolarverwaltung.client.model.Measurement;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.interfaces.RemoteService;
import de.dhahn.dhsolarverwaltung.interfaces.model.SystemDto;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Diese Klasse stellt die Verbindung zur API des Wechselrichters her und liest die Daten aus.
 * Abruf der Daten aus eigenen Thread
 */
public class SolarAPI {

    //region Konstanten

    //endregion


    //region Attribute
    private static SolarAPI instance;
    //endregion


    //region Konstruktoren

    private SolarAPI() {
    }

    //endregion


    //region Methoden
    public static synchronized SolarAPI getInstance() {
        if (instance == null) instance = new SolarAPI();
        return instance;
    }

    /**
     * Liest die aktuelle Solarproduktion für das übergebene System aus.
     *
     * @param system abzufragendes System
     * @return Messwert für aktuelle abfrage
     * @throws MalformedURLException Server nicht gefunden
     * @throws NotBoundException     Service nicht gebunden
     * @throws RemoteException       Remote Exception
     */
    public Measurement getSolarMeasurement(SolarSystem system) throws MalformedURLException, NotBoundException, RemoteException {
        RemoteService service = (RemoteService) Naming.lookup(AppSettings.API_URL);
        SystemDto systemDto = new SystemDto(system);
        return new Measurement(java.time.LocalDateTime.now(), service.getSolarMeasurement(systemDto), true);
    }

    /**
     * Liest den aktuellen Stromverbrauch aus.
     *
     * @return Messwert für aktuelle abfrage
     * @throws MalformedURLException Server nicht gefunden
     * @throws NotBoundException     Service nicht gebunden
     * @throws RemoteException       Remote Exception
     */
    public Measurement getConsumptionMeasurement() throws MalformedURLException, NotBoundException, RemoteException {
        RemoteService service = (RemoteService) Naming.lookup(AppSettings.API_URL);
        return new Measurement(java.time.LocalDateTime.now(), service.getConsumptionMeasurement(), false);
    }

    //endregion
}
