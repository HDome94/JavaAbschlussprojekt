package de.dhahn.dhsolarverwaltung.interfaces;

import de.dhahn.dhsolarverwaltung.interfaces.model.SystemDto;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Diese Schnittstelle definiert die Methoden der Solar API
 */
public interface RemoteService extends Remote {
    //double getSolarMeasurement(List<SolarPanel> panels, Directions direction) throws RemoteException;
    double getSolarMeasurement(SystemDto systemDto) throws RemoteException;

    double getConsumptionMeasurement() throws RemoteException;
}
