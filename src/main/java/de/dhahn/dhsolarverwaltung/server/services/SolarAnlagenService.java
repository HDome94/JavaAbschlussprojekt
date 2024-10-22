package de.dhahn.dhsolarverwaltung.server.services;

import de.dhahn.dhsolarverwaltung.interfaces.RemoteService;
import de.dhahn.dhsolarverwaltung.interfaces.model.SolarDto;
import de.dhahn.dhsolarverwaltung.interfaces.model.SystemDto;
import de.dhahn.dhsolarverwaltung.server.logic.DayTimeSeasonsSwitcher;
import de.dhahn.dhsolarverwaltung.server.model.DayTimes;
import de.dhahn.dhsolarverwaltung.server.model.Seasons;
import de.dhahn.dhsolarverwaltung.server.settings.ServerSettings;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 * Diese Klasse stellt den Solar Anlagen Service zur Verfügung.
 * Dieser dient nur als Fake API um Messwerte für Produktion und Verbrauch zu generieren.
 * Dies geschieht anhand Tageszeit / Jahreszeit und Ausrichtung der Anlage
 */
public class SolarAnlagenService extends UnicastRemoteObject implements RemoteService {


    //region Konstanten

    //endregion


    //region Attribute
    private DayTimes dayTime;
    private Seasons season;
    //endregion


    //region Konstruktoren
    public SolarAnlagenService() throws RemoteException {
    }

    //endregion


    //region Methoden

    /**
     * Simuliert die Abfrage des Wechselrichters wie viel Strom produziert wurde.
     * Wird mit aktueller Tages und Jahreszeit berechnet
     *
     * @param systemDto abzufragendes System (DTO Model)
     * @return produzierte Watt Menge des Systems
     * @throws RemoteException falls ein Fehler auftritt
     */
    @Override
    public double getSolarMeasurement(SystemDto systemDto) throws RemoteException {
        loadCurrentDayTimeAndSeason();

        double maximalWatts = 0;
        for (SolarDto panel : systemDto.getPanels()) {
            maximalWatts += panel.getMaximumWatt();
        }

        if (maximalWatts == 0) return 0;

        Random random = new Random();
        double randomValue = random.nextDouble(0, maximalWatts);
        return randomValue * season.getFactor() * dayTime.getFactor(systemDto.getDirection());
    }

    /**
     * Lädt aktuelle Tages und Jahreszeit aus dem Switcher
     */
    private void loadCurrentDayTimeAndSeason() {
        dayTime = DayTimeSeasonsSwitcher.getInstance().getCurrentDayTime();
        season = DayTimeSeasonsSwitcher.getInstance().getCurrentSeason();
    }

    /**
     * Simuliert den verbrauchten Strom. Wird mit aktueller Tages und Jahreszeit berechnet
     *
     * @return verbrauchter Strom in Watt
     * @throws RemoteException falls ein Fehler auftritt
     */
    @Override
    public double getConsumptionMeasurement() throws RemoteException {
        loadCurrentDayTimeAndSeason();

        Random random = new Random();
        double randomValue = random.nextDouble(ServerSettings.MIN_CONSUMPTION, ServerSettings.MAX_CONSUMPTION);

        // Je nach Tageszeit mit anderen Faktor verrechnen
        randomValue = switch (dayTime) {
            case MORNING -> randomValue * 0.7;
            case MIDDAY -> randomValue * 0.3;
            case AFTERNOON -> randomValue * 0.8;
            case EVENING -> randomValue * 1;
            case NIGHT -> randomValue * 0.2;
        };

        // Je nach Jahreszeit mit anderen Faktor verrechnen
        return switch (season) {
            case SPRING -> randomValue * 0.7;
            case SUMMER -> randomValue * 0.5;
            case FALL -> randomValue * 0.8;
            case WINTER -> randomValue * 1;
        };
    }

    //endregion
}
