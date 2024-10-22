package de.dhahn.dhsolarverwaltung;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

/**
 * Start Klasse um Client und Server Module zu starten
 */
public class Main {
    private static BooleanProperty run = new SimpleBooleanProperty(true);

    //TODO Beenden der GUI -> closed alles

    public static void main(String[] args) throws InterruptedException {
        Thread serverThread = new Thread(() -> {
            try {
                de.dhahn.dhsolarverwaltung.server.main.Main.main(new String[]{});
            } catch (RemoteException | AlreadyBoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                de.dhahn.dhsolarverwaltung.client.Main.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
        Thread.sleep(2000);
        clientThread.start();

        run.addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                //clientThread.interrupt();
                serverThread.interrupt();
            }
        });

    }

    public static boolean isRun() {
        return run.get();
    }

    public static BooleanProperty runProperty() {
        return run;
    }

    public static void setRun(boolean run) {
        Main.run.set(run);
    }
}
