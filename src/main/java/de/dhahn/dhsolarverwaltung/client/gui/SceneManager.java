package de.dhahn.dhsolarverwaltung.client.gui;

import de.dhahn.dhsolarverwaltung.client.Main;
import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import de.dhahn.dhsolarverwaltung.client.settings.Icon;
import de.dhahn.dhsolarverwaltung.client.settings.View;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.net.URL;

/**
 * Implementiert die Steuerlogik, um zwischen den Szenen zu wechseln
 */
public class SceneManager {


    //region Konstanten

    //endregion


    //region Attribute
    private static SceneManager instance;

    private Stage stage;
    private Stage popupStage;
    //endregion


    //region Konstruktoren

    private SceneManager() {
    }

    //endregion


    //region Methoden
    public static synchronized SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    /**
     * Setzt die Stage für den Szenen manager und setzt den Titel und das Icon.
     * Anschließend wird die Main View geöffnet
     *
     * @param stage Stage
     */
    public synchronized void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle(AppText.APP_TITLE);


        URL appIconUrl = Main.class.getResource(Icon.APP_ICON_FILE);
        if (appIconUrl != null)
            this.stage.getIcons().add(new Image(appIconUrl.toString()));

        stage.setOnCloseRequest(event -> de.dhahn.dhsolarverwaltung.Main.setRun(false));

        setScene(View.MAIN);
    }

    /**
     * Wechselt auf die übergebene Szene (settings. Views)
     *
     * @param fileName Dateiname der View
     */
    public synchronized void setScene(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fileName));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_LOAD_SCENE);
        }
    }

    /**
     * Wechselt auf die übergebene Szene (settings. Views) mit aktueller Fenstergröße
     *
     * @param fileName Dateiname der View
     */
    public synchronized void setScene(String fileName, double width, double height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fileName));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_LOAD_SCENE);
        }
    }

    /**
     * Gibt die aktuelle breite des Hauptfensters zurück
     * @return breite Hauptfenster
     */
    public double getCurrentWidth(){
        return stage.getWidth();
    }

    /**
     * Gibt die aktuelle höhe des Hauptfensters zurück
     * @return höhe Hauptfenster
     */
    public double getCurrentHeight(){
        return stage.getHeight();
    }



    /**
     * Öffnet die Detailansicht in einem Pop-up-Fenster und übergibt das Element an den Controller.
     *
     * @param view    View die geöffnet werden soll
     * @param system  aktuelles System das bearbeitet wird
     * @param battery aktuelle Batterie das bearbeitet werden soll | null für neue
     */
    public synchronized void openBatteryDetailPopup(String view, SolarSystem system, Battery battery) {
        try {
            popupStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(view));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());

            if (fxmlLoader.getController() instanceof BatteryDetailViewController controller)
                controller.loadSystemAndBattery(system, battery);

            popupStage.setScene(scene);
            popupStage.show();
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_LOAD_SCENE);
        }
    }

    /**
     * Öffnet die Detailansicht in einem Pop-up-Fenster und übergibt das Element an den Controller.
     *
     * @param view   View die geöffnet werden soll
     * @param system aktuelles System das bearbeitet wird
     * @param panel  aktuelles Panel das bearbeitet werden soll | null für neue
     */
    public synchronized void openPanelDetailPopup(String view, SolarSystem system, SolarPanel panel) {
        try {
            popupStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(view));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());

            if (fxmlLoader.getController() instanceof SolarPanelDetailViewController controller)
                controller.loadSystemAndPanel(system, panel);

            popupStage.setScene(scene);
            popupStage.show();
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_LOAD_SCENE);
        }
    }

    /**
     * Schließt das Pop-up-Fenster
     */
    public synchronized void closePopup() {
        popupStage.close();
    }


    //endregion
}
