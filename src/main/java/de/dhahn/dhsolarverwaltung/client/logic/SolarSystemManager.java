package de.dhahn.dhsolarverwaltung.client.logic;

import de.dhahn.dhsolarverwaltung.client.logic.db.DbManager;
import de.dhahn.dhsolarverwaltung.client.model.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse stellt den Zugriff auf die Daten zur Laufzeit zur Verfügung
 */
public class SolarSystemManager {

    //region Konstanten
    private static SolarSystemManager instance;
    //endregion


    //region Attribute
    private final ObservableList<SolarSystem> solarSystems;
    private final ObservableList<Direction> directions;
    //endregion


    //region Konstruktoren

    private SolarSystemManager() {
        directions = FXCollections.observableList(Arrays.stream(Direction.values()).toList());

        solarSystems = FXCollections.observableList(DbManager.getInstance().readSolarSystems(), system -> new Observable[]{
                system.streetProperty(), system.houseNrProperty(), system.postalCodeProperty(),
                system.cityProperty()
        });

        solarSystems.addListener((ListChangeListener<SolarSystem>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    for (SolarSystem system : change.getAddedSubList()) {
                        // Inner-Change Listener für innere Observable Lists → für neue Systeme registrieren
                        setInnerListener(system);

                        DbManager.getInstance().create(system);
                    }

                } else if (change.wasUpdated()) {
                    for (SolarSystem system : change.getList().subList(change.getFrom(), change.getTo()))
                        if (!system.isBatchUpdating())
                            DbManager.getInstance().update(system);

                } else if (change.wasRemoved()) {
                    for (SolarSystem system : change.getRemoved())
                        DbManager.getInstance().delete(system);
                }
            }
        });

        // Inner-Change Listener für innere Observable Lists → für bestehende Systeme registrieren
        for (SolarSystem system : solarSystems)
            setInnerListener(system);
    }

    //endregion


    //region Methoden
    public static synchronized SolarSystemManager getInstance() {
        if (instance == null) instance = new SolarSystemManager();
        return instance;
    }

    public synchronized ObservableList<SolarSystem> getSolarSystems() {
        return solarSystems;
    }

    public synchronized ObservableList<Direction> getDirections() {
        return directions;
    }

    /**
     * Fügt für die inneren Observable Lists die Listener hinzu
     *
     * @param system System wo die Listener hinzugefügt werden
     */
    private void setInnerListener(SolarSystem system) {
        system.getSolarPanels().addListener((ListChangeListener<SolarPanel>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (SolarPanel panel : innerChange.getAddedSubList())
                        DbManager.getInstance().create(panel, system.getId());

                else if (innerChange.wasUpdated())
                    for (SolarPanel panel : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo())) {
                        if (!panel.isBatchUpdating())
                            DbManager.getInstance().update(panel);
                    }

                else if (innerChange.wasRemoved())
                    for (SolarPanel panel : innerChange.getRemoved())
                        DbManager.getInstance().delete(panel);
            }
        });

        system.getBatteries().addListener((ListChangeListener<Battery>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (Battery battery : innerChange.getAddedSubList())
                        DbManager.getInstance().create(battery, system.getId());

                else if (innerChange.wasUpdated()) {
                    for (Battery battery : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo()))
                        if (!battery.isBatchUpdating())
                            DbManager.getInstance().update(battery);
                } else if (innerChange.wasRemoved())
                    for (Battery battery : innerChange.getRemoved())
                        DbManager.getInstance().delete(battery);

            }
        });

        system.getSolarMeasurements().addListener((ListChangeListener<Measurement>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (Measurement measurement : innerChange.getAddedSubList())
                        DbManager.getInstance().create(measurement, system.getId());

                else if (innerChange.wasUpdated())
                    for (Measurement measurement : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo()))
                        DbManager.getInstance().update(measurement);

                else if (innerChange.wasRemoved())
                    for (Measurement measurement : innerChange.getRemoved())
                        DbManager.getInstance().delete(measurement);
            }
        });

        system.getConsumptionMeasurements().addListener((ListChangeListener<Measurement>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (Measurement measurement : innerChange.getAddedSubList())
                        DbManager.getInstance().create(measurement, system.getId());

                else if (innerChange.wasUpdated())
                    for (Measurement measurement : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo()))
                        DbManager.getInstance().update(measurement);

                else if (innerChange.wasRemoved())
                    for (Measurement measurement : innerChange.getRemoved())
                        DbManager.getInstance().delete(measurement);
            }
        });

        system.getElectricitySales().addListener((ListChangeListener<ElectricitySale>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (ElectricitySale electricitySale : innerChange.getAddedSubList())
                        DbManager.getInstance().create(electricitySale, system.getId());

                else if (innerChange.wasUpdated())
                    for (ElectricitySale electricitySale : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo()))
                        DbManager.getInstance().update(electricitySale);

                else if (innerChange.wasRemoved())
                    for (ElectricitySale electricitySale : innerChange.getRemoved())
                        DbManager.getInstance().delete(electricitySale);
            }
        });

        system.getElectricityPurchases().addListener((ListChangeListener<ElectricitySale>) innerChange -> {
            while (innerChange.next()) {

                if (innerChange.wasAdded())
                    for (ElectricitySale electricitySale : innerChange.getAddedSubList())
                        DbManager.getInstance().create(electricitySale, system.getId());

                else if (innerChange.wasUpdated())
                    for (ElectricitySale electricitySale : innerChange.getList().subList(innerChange.getFrom(), innerChange.getTo()))
                        DbManager.getInstance().update(electricitySale);

                else if (innerChange.wasRemoved())
                    for (ElectricitySale electricitySale : innerChange.getRemoved())
                        DbManager.getInstance().delete(electricitySale);
            }
        });
    }

    /**
     * Berechnet die Gesamtzahl der Speicher aller Systeme
     *
     * @return Gesamtanzahl der Speicher
     */
    public synchronized int getOverallBatteriesCount() {
        int count = 0;
        for (SolarSystem system : solarSystems)
            count += system.getBatteries().size();
        return count;
    }

    /**
     * Berechnet die gesamtkapazität aller Speicher
     *
     * @return Kapazität der Speicher
     */
    public synchronized double getOverallBatteriesCapacity() {
        double capacity = 0;
        for (SolarSystem system : solarSystems)
            for (Battery battery : system.getBatteries())
                capacity += battery.getCapacity();
        return capacity;
    }

    /**
     * Berechnet die maximale Gesamtleistung aller Anlagen zusammen
     *
     * @return Maximale Leistung aller Systeme
     */
    public synchronized double getMaximumPerformance() {
        double overallPerformance = 0;
        for (SolarSystem system : solarSystems)
            if (!system.getSolarPanels().isEmpty())
                for (SolarPanel panel : system.getSolarPanels())
                    overallPerformance += panel.getMaximumWatt();

        return overallPerformance;
    }

    /**
     * Berechnet die Gesamtproduktion der Systeme zusammen (Letzte Messwerte je System verrechnen)
     *
     * @return Produktion aller Systeme
     */
    public synchronized double getCurrentOverallPerformance() {
        double overallPerformance = 0;
        List<SolarSystem> tempSolarSystems = new ArrayList<>(solarSystems);

        for (SolarSystem system : tempSolarSystems)
            if (!system.getSolarMeasurements().isEmpty())
                overallPerformance += system.getSolarMeasurements().getLast().getWatt();
        return overallPerformance;
    }

    /**
     * Berechnet den Gesamtverbrauch der Systeme zusammen (Letzte Messwerte je System verrechnen)
     *
     * @return Verbrauch aller Systeme
     */
    public synchronized double getCurrentOverallConsumption() {
        double overallPerformance = 0;
        List<SolarSystem> tempSolarSystems = new ArrayList<>(solarSystems);

        for (SolarSystem system : tempSolarSystems)
            if (!system.getConsumptionMeasurements().isEmpty())
                overallPerformance += system.getConsumptionMeasurements().getLast().getWatt();

        return overallPerformance;
    }

    /**
     * Berechnet den Gesamtakkustand aller Systeme
     * @return Akkustand
     */
    public synchronized double getCurrentOverallCharge() {
        double overallCharge = 0;
        List<SolarSystem> tempSolarSystems = new ArrayList<>(solarSystems);

        for (SolarSystem system : tempSolarSystems)
            for (Battery battery : system.getBatteries())
                overallCharge += battery.getCharge();

        return overallCharge;
    }

    /**
     * Berechnet den Erlös der Stromverkäufe der letzten 24h
     * @return Kosten
     */
    public synchronized double getElectricityRevenueLastDay() {
        double revenue = 0;
        List<SolarSystem> tempSolarSystems = new ArrayList<>(solarSystems);
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);

        for (SolarSystem system : tempSolarSystems)
            for (ElectricitySale purchased : system.getElectricityPurchases())
                if (purchased.getDateTime().isAfter(lastDay))
                    revenue += purchased.getWatt() / 1000 * purchased.getPricePerKW();

        return revenue;
    }

    /**
     * Berechnet die Stromkosten der letzten 24h
     * @return Kosten
     */
    public synchronized double getElectricityCostsLastDay() {
        double costs = 0;
        List<SolarSystem> tempSolarSystems = new ArrayList<>(solarSystems);
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);

        for (SolarSystem system : tempSolarSystems)
            for (ElectricitySale sale : system.getElectricitySales())
                if (sale.getDateTime().isAfter(lastDay))
                    costs += sale.getWatt() / 1000 * sale.getPricePerKW();

        return costs;
    }
    //endregion
}
