package de.dhahn.dhsolarverwaltung.client;

import de.dhahn.dhsolarverwaltung.client.gui.SceneManager;
import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.logic.api.SolarAPI;
import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.ElectricitySale;
import de.dhahn.dhsolarverwaltung.client.model.Measurement;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.client.threads.SolarApiThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Main Klasse des Clients, startet den Thread für die API Abfrage und die App
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        SolarApiThread solarApiThread = new SolarApiThread(AppSettings.MEASUREMENT_INTERVALL);
        solarApiThread.start();

        de.dhahn.dhsolarverwaltung.Main.runProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue)
                solarApiThread.interrupt();

            Platform.exit();
        });

        SceneManager.getInstance().setStage(stage);
    }


}