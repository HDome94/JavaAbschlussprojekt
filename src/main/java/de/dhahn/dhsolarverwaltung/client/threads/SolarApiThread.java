package de.dhahn.dhsolarverwaltung.client.threads;

import de.dhahn.dhsolarverwaltung.Main;
import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.logic.api.SolarAPI;
import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.ElectricitySale;
import de.dhahn.dhsolarverwaltung.client.model.Measurement;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/**
 * Dieser Thread läuft im Hintergrund und fragt im eingestellten intervall die SolarAPI ab und ermittelt aus
 * Produktion und Verbrauch je System die differenz. Falls mehr Strom produziert wurde, werden die Batterien geladen,
 * falls nicht voll, ansonsten wird der Strom verkauft. Falls die Produktion nicht ausreicht werden zuerst die
 * Speicher entleert. Für die Messwerte Produktion und Verbrauch sowie ggf die Strom Ein- und Verkäufe werden für
 * das jeweilige System protokolliert und die aktuelle Ladung der Batterien gesetzt.
 * Der Thread startet 5sek versetzt damit die App Zeit zum Initialisieren hat.
 */
public class SolarApiThread extends Thread {

    //region Konstanten

    //endregion


    //region Attribute
    private final int sleepDuration;
    //endregion


    //region Konstruktoren

    public SolarApiThread(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    //endregion


    //region Methoden


    @Override
    public synchronized void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            return;
        }

        while (Main.isRun()) {
            try {
                for (SolarSystem system : SolarSystemManager.getInstance().getSolarSystems()) {
                    Measurement solarMeasurement = SolarAPI.getInstance().getSolarMeasurement(system);
                    Measurement consumptionMeasurement = SolarAPI.getInstance().getConsumptionMeasurement();

                    if (solarMeasurement == null || consumptionMeasurement == null) return;

                    Platform.runLater(() -> processMeasurementsAndSales(system, solarMeasurement, consumptionMeasurement));
                }

                Thread.sleep(sleepDuration);

            } catch (InterruptedException e) {
                return;

            } catch (MalformedURLException | NotBoundException | RemoteException e) {
                Platform.runLater(() -> AlertManager.getInstance().showError(AppText.ERROR_API_TITLE,
                        String.format(AppText.TEMPLATE_ERROR_DATABASE_READ_ALL_HEADER, AppText.MEASUREMENTS),
                        e.getMessage()));
            }
        }
    }

    /**
     * Verarbeitet die Messwerte für das System. Wird von FX Thread ausgeführt um keine
     * Prüft, ob mehr produziert oder verbraucht wurde und ent-/lädt die Speicher.
     * Falls zu viel oder zu wenig Strom vorhanden ist, wird Strom Ein-/Verkauft.
     * Am Ende wird alles den Listen im System hinzugefügt.
     * @param system Die Solaranlage für die Messwerte
     * @param solarMeasurement Produktionsmesswert
     * @param consumptionMeasurement Verbrauchsmesswert
     */
    private void processMeasurementsAndSales(SolarSystem system, Measurement solarMeasurement, Measurement consumptionMeasurement ) {
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
                electricitySale = new ElectricitySale(LocalDateTime.now(), difference, false,
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

        // Wenn Batterien nicht ausgereicht haben -> Kaufe Strom
        if (difference < -0.0001)
            electricitySale = new ElectricitySale(LocalDateTime.now(), -difference, true,
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
}
