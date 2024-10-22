package de.dhahn.dhsolarverwaltung.client.test;

import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.logic.api.SolarAPI;
import de.dhahn.dhsolarverwaltung.client.model.*;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Baut Testsysteme und fügt sie der Datenbank hinzu.
 * Anschließend werden für die Anzahl der Tage die Messwerte erstellt. Dies kann je nach Tage etwas in Anspruch nehmen.
 * Wird für die Statistiken benötigt damit diese Daten zum Anzeigen haben
 *
 */
public class TestData {

    //region Konstanten
    private static int DAYS_TO_BUILD = 30;
    private static int MEASURE_INTERVALL = 10;
    private static int SYSTEMS_TO_BUILD = 5;
    private static int PANELS_TO_BUILD = 15;
    private static int BATTERIES_TO_BUILD = 3;
    private static int CREATE_MEASUREMENTS_SLEEP = 100;
    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    private TestData() {
    }

    //endregion


    //region Methoden

    public static int getDaysToBuild() {
        return DAYS_TO_BUILD;
    }

    public static void setDaysToBuild(int daysToBuild) {
        DAYS_TO_BUILD = daysToBuild;
    }

    public static int getMeasureIntervall() {
        return MEASURE_INTERVALL;
    }

    public static void setMeasureIntervall(int measureIntervall) {
        MEASURE_INTERVALL = measureIntervall;
    }

    public static int getSystemsToBuild() {
        return SYSTEMS_TO_BUILD;
    }

    public static void setSystemsToBuild(int systemsToBuild) {
        SYSTEMS_TO_BUILD = systemsToBuild;
    }

    public static int getPanelsToBuild() {
        return PANELS_TO_BUILD;
    }

    public static void setPanelsToBuild(int panelsToBuild) {
        PANELS_TO_BUILD = panelsToBuild;
    }

    public static int getBatteriesToBuild() {
        return BATTERIES_TO_BUILD;
    }

    public static void setBatteriesToBuild(int batteriesToBuild) {
        BATTERIES_TO_BUILD = batteriesToBuild;
    }

    /**
     * Baut die Testsysteme und kreiert anschließend rückwirkende Messwerte
     */
    public static void buildTestData() {
        for (int i = 0; i < SYSTEMS_TO_BUILD; i++)
            buildTestSystem();

        buildMesswerte();
    }

    /**
     * Baut ein Testsystem mit Solarpanels und Batterien und fügt es der Datenbank hinzu
     */
    private static void buildTestSystem() {
        SolarSystem system = new SolarSystem();

        for (int i = 0; i < PANELS_TO_BUILD; i++) {
            system.getSolarPanels().add(new SolarPanel("JaSolar", "MODEL 1", 420));
        }

        for (int i = 0; i < BATTERIES_TO_BUILD; i++) {
            system.getBatteries().add(new Battery("Anker", "MODEL 1", 4500));
        }

        Random random = new Random();
        int directionIndex = random.nextInt(0, Direction.values().length);

        system.setCity("Berlin");
        system.setPostalCode("45454");
        system.setStreet("Hubertusstraße");
        system.setHouseNr("75A");

        system.setDirection(Direction.values()[directionIndex]);
        SolarSystemManager.getInstance().getSolarSystems().add(system);
    }


    /**
     * Baut rückwirkend Messwerte für die Statistik → Dauert je nach menge etwas
     */
    private static void buildMesswerte() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_TO_BUILD);
        Duration d = Duration.between(startDate, now);
        while (d.toMinutes() >= 10) {
            try {
                for (SolarSystem system : SolarSystemManager.getInstance().getSolarSystems()) {
                    Measurement solarMeasurement = SolarAPI.getInstance().getSolarMeasurement(system);
                    Measurement consumptionMeasurement = SolarAPI.getInstance().getConsumptionMeasurement();
                    if (solarMeasurement == null || consumptionMeasurement == null) return;

                    solarMeasurement.setDatetime(startDate);
                    consumptionMeasurement.setDatetime(startDate);
                    System.out.println(startDate);

                    processMeasurementsAndSales(system, solarMeasurement, consumptionMeasurement, startDate);
                }

                Thread.sleep(CREATE_MEASUREMENTS_SLEEP);
            } catch (InterruptedException e) {
                return;
            } catch (MalformedURLException | NotBoundException | RemoteException e) {
                e.printStackTrace();
            }
            startDate = startDate.plusMinutes(MEASURE_INTERVALL);
            d = Duration.between(startDate, now);
        }
    }

    /**
     * Verarbeitet die Messwerte für das System. Wird von FX Thread ausgeführt um keine
     * Prüft, ob mehr produziert oder verbraucht wurde und ent-/lädt die Speicher.
     * Falls zu viel oder zu wenig Strom vorhanden ist, wird Strom Ein-/Verkauft.
     * Am Ende wird alles den Listen im System hinzugefügt.
     *
     * @param system                 Die Solaranlage für die Messwerte
     * @param solarMeasurement       Produktionsmesswert
     * @param consumptionMeasurement Verbrauchsmesswert
     */
    private static void processMeasurementsAndSales(SolarSystem system, Measurement solarMeasurement,
                                             Measurement consumptionMeasurement, LocalDateTime dateTime) {

        ElectricitySale electricitySale = null;

        double difference = solarMeasurement.getWatt() - consumptionMeasurement.getWatt();

        // Wenn mehr produziert als verbraucht wurde -> Lade Batterien sonst Verkaufe Strom
        if (difference > 0) {
            for (Battery battery : system.getBatteries()) {
                double missingCharge = battery.getCapacity() - battery.getCharge();

                if (missingCharge <= 0.0001)
                    continue;

                // Wenn in eine Battery passt
                if (difference <= missingCharge)
                    battery.setCharge(battery.getCharge() + difference);

                    // Falls mehr Ertrag als in aktuelle Battery passt
                else {
                    battery.setCharge(battery.getCharge() + missingCharge);
                    difference -= missingCharge;
                }

                if (difference <= 0)
                    break;
            }

            // Falls alle Batterien voll sind, kann Strom verkauft werden
            if (difference > 0)
                electricitySale = new ElectricitySale(dateTime, difference, false,
                        AppSettings.PRICE_PER_KW_SALED);

            // Falls nicht genug produziert wurde, erst Batterien entladen sonst Strom kaufen
        } else if (difference < 0)
            for (Battery battery : system.getBatteries()) {

                // Wenn aktuelle Battery genug Kapazität hat
                if (difference + battery.getCharge() >= 0) {
                    battery.setCharge(battery.getCharge() + difference);
                    difference = 0;
                    break;

                } else {
                    difference += battery.getCharge();
                    battery.setCharge(0);
                }
            }

        // Wenn Batterien nicht ausgereicht haben → Kaufe Strom
        if (difference < -0.0001)
            electricitySale = new ElectricitySale(dateTime, -difference, true,
                    AppSettings.PRICE_PER_KW_PURCHASED);

        // Wenn Strom Ein- oder verkauft wurde
        if (electricitySale != null) {
            if (electricitySale.isIsPurchased())
                system.getElectricityPurchases().add(electricitySale);
            else
                system.getElectricitySales().add(electricitySale);
        }

        system.getSolarMeasurements().add(solarMeasurement);
        system.getConsumptionMeasurements().add(consumptionMeasurement);
    }

    //endregion
}
