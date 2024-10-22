package de.dhahn.dhsolarverwaltung.client.logic.db;

import de.dhahn.dhsolarverwaltung.client.gui.AlertManager;
import de.dhahn.dhsolarverwaltung.client.model.*;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Diese Klasse stellt die übergreifenden CRUD Operationen zur Verfügung und leitet diese an die DAOs weiter
 */
public class DbManager {


    //TODO Verdichtung der DB Messwerte

    //region Konstanten
    private static DbManager instance;
    //endregion


    //region Attribute
    private final String CONNECTION_STRING = AppSettings.DB_PROTOCOL + AppSettings.DB_DOMAIN + ":" +
            AppSettings.DB_PORT + "/" + AppSettings.DB_NAME;
    private final DaoSolarSystem daoSolarSystem;
    private final DaoSolarPanel daoSolarPanel;
    private final DaoBattery daoBattery;
    private final DaoMeasurement daoMeasurement;
    private final DaoElectricitySale daoElectricitySale;


    private final Queue<DbOperation> operationQueue;
    private int retryCount;
    private boolean isProcessingQueue;

    //endregion


    //region Konstruktoren

    private DbManager() {
        daoSolarSystem = new DaoSolarSystem();
        daoSolarPanel = new DaoSolarPanel();
        daoBattery = new DaoBattery();
        daoMeasurement = new DaoMeasurement();
        daoElectricitySale = new DaoElectricitySale();

        operationQueue = new LinkedList<>();
        isProcessingQueue = false;
    }

    //endregion


    //region Methoden
    public static synchronized DbManager getInstance() {
        if (instance == null) instance = new DbManager();
        return instance;
    }

    /**
     * Baut eine Verbindung zur Datenbank anhand der Einstellungen auf.
     *
     * @return aufgebaute Verbindung | null
     */
    private synchronized Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING, AppSettings.DB_USERNAME, AppSettings.DB_PASSWORD);

            if (!isProcessingQueue && !operationQueue.isEmpty())
                startQueueProcessor();

            return connection;
        } catch (SQLException e) {
            Platform.runLater(() -> AlertManager.getInstance()
                    .showError(AppText.ERROR_DATABASE_TITLE, AppText.ERROR_ESTABLISH_DB_CONNECTION));
        }
        return null;
    }

    /**
     * Fügt die Operation der Queue hinzu und startet den Queue Thread sofern noch nicht gestartet
     *
     * @param operation Datenbank operation
     */
    private synchronized void insertOperationToQueue(DbOperation operation) {
        operationQueue.offer(operation);

        if (!isProcessingQueue)
            startQueueProcessor();
    }

    private synchronized void startQueueProcessor() {
        isProcessingQueue = true;
        retryCount = 5;

        // Damit nicht direkt der nächste Versuch ausgeführt wird, sondern nach einer Minute
        try {
            Thread.sleep(AppSettings.DB_RETRY_INTERVALL);
        } catch (InterruptedException e) {
            System.out.println(AppText.ERROR_QUEUE_INTERRUPTED);
        }

        Thread queueThread = new Thread(() -> {
            while (!operationQueue.isEmpty() && retryCount != 0) {
                int currentRetryCount = retryCount;
                processQueue();

                try {
                    // Falls Queue nicht leer und letzter Versuch erfolglos, Warte Retry Intervall
                    if (!operationQueue.isEmpty() && currentRetryCount != retryCount)
                        Thread.sleep(AppSettings.DB_RETRY_INTERVALL);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            isProcessingQueue = false;
        });

        queueThread.start();
    }

    /**
     * Versucht die Queue auszuführen
     */
    private synchronized void processQueue() {
        DbOperation operation = operationQueue.poll();
        if (operation != null) {
            try (Connection connection = getConnection()) {
                if (connection != null)
                    operation.runWithConnection(connection);

                else {
                    // Wenn weiterhin keine Verbindung möglich ist, verringere den Retry-Count und füge Operation wieder ein
                    retryCount--;
                    insertOperationToQueue(operation);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //region Create

    /**
     * Legt das neue System in der Datenbank an. Anschließend werden alle Inneren Objekte in die Datenbank geschrieben
     *
     * @param system Neues System das in DB gespeichert werden soll
     */
    public synchronized void create(SolarSystem system) {
        if (system.getId() != 0) return;

        try (Connection connection = getConnection()) {

            if (connection != null)
                daoSolarSystem.create(connection, system);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else {
                insertOperationToQueue(retryConnection -> {
                    daoSolarSystem.create(retryConnection, system);

                    if (system.getId() != 0)
                        createNestedObjects(system);
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (system.getId() != 0)
            createNestedObjects(system);

    }

    /**
     * Legt die inneren Objekte der Solaranlage in der Datenbank an
     *
     * @param system Solaranlage
     */
    private synchronized void createNestedObjects(SolarSystem system) {
        for (SolarPanel panel : system.getSolarPanels())
            create(panel, system.getId());
        for (Battery battery : system.getBatteries())
            create(battery, system.getId());
        for (Measurement measurement : system.getSolarMeasurements())
            create(measurement, system.getId());
        for (Measurement measurement : system.getConsumptionMeasurements())
            create(measurement, system.getId());
        for (ElectricitySale electricitySale : system.getElectricitySales())
            create(electricitySale, system.getId());
    }

    /**
     * Legt das Panel in der Datenbank an.
     *
     * @param panel    Panel zum Anlegen
     * @param systemId Id des Systems
     */
    public synchronized void create(SolarPanel panel, long systemId) {
        if (panel.getId() != 0 || systemId == 0) return;

        try (Connection connection = getConnection()) {
            if (connection != null)
                daoSolarPanel.create(connection, panel, systemId);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoSolarPanel.create(retryConnection, panel, systemId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Legt die Batterie in der Datenbank an.
     *
     * @param battery  Batterie zum Anlegen
     * @param systemId Id des Systems
     */
    public synchronized void create(Battery battery, long systemId) {
        if (battery.getId() != 0 || systemId == 0) return;

        try (Connection connection = getConnection()) {
            if (connection != null)
                daoBattery.create(connection, battery, systemId);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoBattery.create(retryConnection, battery, systemId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Legt den Messwert Produktion / Verbrauch in der Datenbank an.
     *
     * @param measurement Messwert
     * @param systemId    Id des Systems
     */
    public synchronized void create(Measurement measurement, long systemId) {
        if (measurement.getId() != 0 || systemId == 0) return;

        try (Connection connection = getConnection()) {
            if (connection != null)
                daoMeasurement.create(connection, measurement, systemId);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoMeasurement.create(retryConnection, measurement, systemId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Legt den Messwert Einkauf / Verkauf von Strom in der Datenbank an.
     *
     * @param electricitySale Messwert Einkauf / Verkauf
     * @param systemId        Id des Systems
     */
    public synchronized void create(ElectricitySale electricitySale, long systemId) {
        if (electricitySale.getId() != 0 || systemId == 0) return;

        try (Connection connection = getConnection()) {
            if (connection != null)
                daoElectricitySale.create(connection, electricitySale, systemId);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoElectricitySale.create(retryConnection, electricitySale, systemId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Read

    /**
     * Lädt alle Solar Anlagen aus der Datenbank und lädt anschließend die inneren Objekte
     *
     * @return Liste der Solar Anlagen
     */
    public synchronized List<SolarSystem> readSolarSystems() {
        List<SolarSystem> systems = new ArrayList<>();

        try (Connection connection = getConnection()) {
            if (connection != null)
                systems = daoSolarSystem.readAll(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (SolarSystem system : systems)
            loadNestedElemtents(system);

        return systems;
    }

    /**
     * Lädt die inneren Objekte der Anlage
     *
     * @param system Solaranlage die geladen wird
     */
    private synchronized void loadNestedElemtents(SolarSystem system) {
        if (system.getId() == 0) return;

        system.setSolarPanels(readSolarPanelsBySystem(system.getId()));
        system.setBatteries(readBatteriesBySystem(system.getId()));
        system.setSolarMeasurements(readMeasurementsBySystem(system.getId(), true));
        system.setConsumptionMeasurements(readMeasurementsBySystem(system.getId(), false));
        system.setElectricitySales(readElectricitySalesBySystem(system.getId(), false));
        system.setElectricityPurchases(readElectricitySalesBySystem(system.getId(), true));
    }

    /**
     * Lädt die Solaranlage mit der übergebenen Id aus der Datenbank.
     *
     * @param id Id der Solar Anlage
     * @return Solaranlage
     */
    public synchronized SolarSystem readSolarSystem(long id) {
        SolarSystem system = null;

        try (Connection connection = getConnection()) {
            if (connection != null)
                system = daoSolarSystem.read(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (system != null)
            loadNestedElemtents(system);

        return system;
    }

    /**
     * Lädt alle Solarpanels aus der Datenbank
     *
     * @return Liste der Solarpanels
     */
    public synchronized List<SolarPanel> readSolarPanels() {
        List<SolarPanel> panels = new ArrayList<>();

        try (Connection connection = getConnection()) {
            if (connection != null)
                panels = daoSolarPanel.readAll(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panels;
    }

    /**
     * Lädt die Panels, die zu dem System zugeordnet sind
     *
     * @param id des Systems
     * @return Liste der Solarpanels
     */
    public synchronized List<SolarPanel> readSolarPanelsBySystem(long id) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoSolarPanel.readBySystemId(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt das Panel mit übergebener Id aus der Datenbank
     *
     * @param id des Panels
     * @return Solarpanel mit der übergebener Id
     */
    public synchronized SolarPanel readSolarPanel(long id) {
        SolarPanel panel = null;

        try (Connection connection = getConnection()) {
            if (connection != null)
                panel = daoSolarPanel.read(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    /**
     * Lädt alle Batterien aus der Datenbank
     *
     * @return Liste der Batterien
     */
    public synchronized List<Battery> readBatteries() {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoBattery.readAll(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Batterien, die zu dem System zugeordnet sind
     *
     * @param id des Systems
     * @return Liste der Batterien
     */
    public synchronized List<Battery> readBatteriesBySystem(long id) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoBattery.readBySystemId(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Batterie mit übergebener Id aus der Datenbank
     *
     * @param id der Batterie
     * @return Batterie mit der übergebener Id
     */
    public synchronized Battery readBattery(long id) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoBattery.read(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lädt alle Messwerte Strom Produktion Verbrauch aus der Datenbank
     *
     * @return Liste der Messwerte
     */
    public synchronized List<Measurement> readMeasurements() {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoMeasurement.readAll(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte der letzten 24h, die zu dem System zugeordnet sind
     *
     * @param id des Systems
     * @return Liste der Messwerte
     */
    public synchronized List<Measurement> readMeasurementsBySystem(long id, boolean produced) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                LocalDateTime startDate = LocalDateTime.now().minusDays(1);
                return daoMeasurement.readBySystemId(connection, id, produced, startDate, LocalDateTime.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte im übergebenen Zeitraum, die zu dem System zugeordnet sind
     *
     * @param id        des Systems
     * @param produced  Produktion oder Verbrauchs Messwert
     * @param startDate Anfangsdatum der Spanne
     * @param endDate   Enddatum der Spanne
     * @return Liste der Messwerte
     */
    public synchronized List<Measurement> readMeasurementsBySystem(long id, boolean produced, LocalDateTime startDate, LocalDateTime endDate) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Duration d = Duration.between(startDate, endDate);

                if (d.toDays() > 2)
                    return daoMeasurement.readBySystemId(connection, id, produced, startDate, endDate);
                else
                    return daoMeasurement.readBySystemId(connection, id, produced, startDate, endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte im übergebenen Zeitraum.
     *
     * @param produced  Produktion oder Verbrauchs Messwert
     * @param startDate Anfangsdatum der Spanne
     * @param endDate   Enddatum der Spanne
     * @return Liste der Messwerte
     */
    public synchronized List<Measurement> readMeasurementsForDateSpan(boolean produced, LocalDateTime startDate, LocalDateTime endDate) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoMeasurement.readAllInDateSpan(connection, produced, startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt den Messwert mit übergebener Id aus der Datenbank
     *
     * @param id des Messwertes
     * @return Messwert mit übergebener Id
     */
    public synchronized Measurement readMeasurement(int id) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoMeasurement.read(connection, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lädt die Messwerte der letzten 24h, die zu dem System zugeordnet sind
     *
     * @param id des Systems
     * @return Liste der Messwerte
     */
    public synchronized List<ElectricitySale> readElectricitySalesBySystem(long id, boolean isPurchased) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                LocalDateTime startDate = LocalDateTime.now().minusDays(1);
                return daoElectricitySale.readBySystemId(connection, id, isPurchased, startDate, LocalDateTime.now());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte im übergebenen Zeitraum, die zu dem System zugeordnet sind
     *
     * @param id des Systems
     * @return Liste der Messwerte
     */
    public synchronized List<ElectricitySale> readElectricitySalesBySystem(long id, boolean isPurchased, LocalDateTime startDate, LocalDateTime endDate) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                return daoElectricitySale.readBySystemId(connection, id, isPurchased, startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte im übergebenen Zeitraum, die zu dem System zugeordnet sind
     *
     * @param produced  Produktion oder Verbrauchs Messwert
     * @param startDate Anfangsdatum der Spanne
     * @param endDate   Enddatum der Spanne
     * @return Liste der Messwerte
     */
    public synchronized List<MeasurementEvaluation> readMeasurementEvaluations(boolean produced, LocalDateTime startDate, LocalDateTime endDate) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Duration d = Duration.between(startDate, endDate);

                if (d.toDays() <= 2)
                    return daoMeasurement.readEvaluations(connection, produced, startDate, endDate, AppCommand.GROUP_BY_HOUR);
                else if (d.toDays() <= 30)
                    return daoMeasurement.readEvaluations(connection, produced, startDate, endDate, AppCommand.GROUP_BY_DAY);
                else if (d.toDays() <= 730)
                    return daoMeasurement.readEvaluations(connection, produced, startDate, endDate, AppCommand.GROUP_BY_MONTH);
                else
                    return daoMeasurement.readEvaluations(connection, produced, startDate, endDate, AppCommand.GROUP_BY_YEAR);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Lädt die Messwerte im übergebenen Zeitraum, die zu dem System zugeordnet sind
     *
     * @param purchased Strom Ein- oder Verkauf
     * @param startDate Anfangsdatum der Spanne
     * @param endDate   Enddatum der Spanne
     * @return Liste der Messwerte
     */
    public synchronized List<ElectricitySaleEvaluation> readElectricitySaleEvaluations(boolean purchased, LocalDateTime startDate, LocalDateTime endDate) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Duration d = Duration.between(startDate, endDate);

                if (d.toDays() <= 2)
                    return daoElectricitySale.readEvaluations(connection, purchased, startDate, endDate, AppCommand.GROUP_BY_HOUR);
                else if (d.toDays() <= 30)
                    return daoElectricitySale.readEvaluations(connection, purchased, startDate, endDate, AppCommand.GROUP_BY_DAY);
                else if (d.toDays() <= 730)
                    return daoElectricitySale.readEvaluations(connection, purchased, startDate, endDate, AppCommand.GROUP_BY_MONTH);
                else
                    return daoElectricitySale.readEvaluations(connection, purchased, startDate, endDate, AppCommand.GROUP_BY_YEAR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
    //endregion

    //region Update

    /**
     * Aktualisiert das übergebene Objekt in der Datenbank. Innere Objekte werden nicht berücksichtigt
     *
     * @param system System das aktualisiert werden soll
     */
    public synchronized void update(SolarSystem system) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoSolarSystem.update(connection, system);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoSolarSystem.update(retryConnection, system));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert das übergebene Objekt in der Datenbank
     *
     * @param panel Panel das aktualisiert werden soll
     */
    public synchronized void update(SolarPanel panel) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoSolarPanel.update(connection, panel);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoSolarPanel.update(retryConnection, panel));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Aktualisiert das übergebene Objekt in der Datenbank
     *
     * @param battery Batterie die aktualisiert werden soll
     */
    public synchronized void update(Battery battery) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoBattery.update(connection, battery);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoBattery.update(retryConnection, battery));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert das übergebene Objekt in der Datenbank
     *
     * @param measurement Messwert der aktualisiert werden soll
     */
    public synchronized void update(Measurement measurement) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoMeasurement.update(connection, measurement);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoMeasurement.update(retryConnection, measurement));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert das übergebene Objekt in der Datenbank
     *
     * @param electricitySale Messwert der aktualisiert werden soll
     */
    public synchronized void update(ElectricitySale electricitySale) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoElectricitySale.update(connection, electricitySale);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoElectricitySale.update(retryConnection, electricitySale));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Delete

    /**
     * Löscht das System mit allen Komponenten und Messwerten aus der Datenbank
     *
     * @param system System das gelöscht werden soll
     */
    public synchronized void delete(SolarSystem system) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                delete(connection, system);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> delete(retryConnection, system));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht das System mit allen Komponenten und Messwerten aus der Datenbank
     *
     * @param system     System das gelöscht werden soll
     * @param connection Verbindung zur Datenbank
     */
    private void delete(Connection connection, SolarSystem system) {
        for (Battery battery : system.getBatteries())
            delete(connection, battery);
        for (SolarPanel panel : system.getSolarPanels())
            delete(connection, panel);
        for (Measurement measurement : system.getSolarMeasurements())
            delete(connection, measurement);
        for (Measurement measurement : system.getConsumptionMeasurements())
            delete(connection, measurement);
        for (ElectricitySale electricitySale : system.getElectricitySales())
            delete(connection, electricitySale);
        for (ElectricitySale electricityPurchase : system.getElectricityPurchases())
            delete(connection, electricityPurchase);

        daoSolarSystem.delete(connection, system);
    }

    /**
     * Löscht das übergebene Panel aus der Datenbank
     *
     * @param panel Panel das gelöscht werden soll
     */
    public synchronized void delete(SolarPanel panel) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoSolarPanel.delete(connection, panel);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoSolarPanel.delete(retryConnection, panel));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht das übergebene Panel aus der Datenbank
     *
     * @param panel Panel das gelöscht werden soll
     */
    public synchronized void delete(Connection connection, SolarPanel panel) {
        try {
            if (connection != null)
                daoSolarPanel.delete(connection, panel);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoSolarPanel.delete(retryConnection, panel));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht die übergebene Batterie aus der Datenbank
     *
     * @param battery Batterie zum Löschen
     */
    public synchronized void delete(Battery battery) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoBattery.delete(connection, battery);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoBattery.delete(retryConnection, battery));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht die übergebene Batterie aus der Datenbank
     *
     * @param battery Batterie zum Löschen
     */
    public synchronized void delete(Connection connection, Battery battery) {
        try {
            if (connection != null)
                daoBattery.delete(connection, battery);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoBattery.delete(retryConnection, battery));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param measurement Messwert Strom Produktion / Verbrauch
     */
    public synchronized void delete(Measurement measurement) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoMeasurement.delete(connection, measurement);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoMeasurement.delete(retryConnection, measurement));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param measurement Messwert Strom Produktion / Verbrauch
     */
    public synchronized void delete(Connection connection, Measurement measurement) {
        try {
            if (connection != null)
                daoMeasurement.delete(connection, measurement);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoMeasurement.delete(retryConnection, measurement));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param electricitySale Messwert Strom Ein-/Verkauf
     */
    public synchronized void delete(ElectricitySale electricitySale) {
        try (Connection connection = getConnection()) {
            if (connection != null)
                daoElectricitySale.delete(connection, electricitySale);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoElectricitySale.delete(retryConnection, electricitySale));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht den übergebenen Messwert aus der Datenbank
     *
     * @param electricitySale Messwert Strom Ein-/Verkauf
     */
    private synchronized void delete(Connection connection, ElectricitySale electricitySale) {
        try {
            if (connection != null)
                daoElectricitySale.delete(connection, electricitySale);

                // Falls keine Verbindung möglich, in Queue aufnehmen
            else
                insertOperationToQueue(retryConnection -> daoElectricitySale.delete(retryConnection, electricitySale));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //endregion
}
