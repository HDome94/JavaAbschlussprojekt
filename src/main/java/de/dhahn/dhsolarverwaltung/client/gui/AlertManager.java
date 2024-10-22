package de.dhahn.dhsolarverwaltung.client.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Diese Klasse stellt die Alerts in unterschiedlichen Varianten zur Verfügung
 */
public class AlertManager {

    //region Konstanten

    //endregion


    //region Attribute
    private static AlertManager instance;
    //endregion


    //region Konstruktoren

    private AlertManager() {
    }

    //endregion


    //region Methoden
    public static synchronized AlertManager getInstance() {
        if (instance == null) instance = new AlertManager();
        return instance;
    }

    /**
     * Zeigt ein Fehlerfenster an
     *
     * @param title   Titel des Fensters
     * @param header  Header Text
     * @param content Content Text
     * @return Gedrückter Button
     */
    public synchronized Optional<ButtonType> showError(String title, String header, String content) {
        return createAlert(Alert.AlertType.ERROR, title, header, content).showAndWait();
    }

    /**
     * Zeigt ein Fehlerfenster an
     *
     * @param title  Titel des Fensters
     * @param header Header Text
     * @return Gedrückter Button
     */
    public synchronized Optional<ButtonType> showError(String title, String header) {
        return createAlert(Alert.AlertType.ERROR, title, header).showAndWait();
    }

    /**
     * Zeigt ein Bestätigungsfenster an
     *
     * @param title   Titel des Fensters
     * @param header  Header Text
     * @param content Content Text
     * @return Gedrückter Button
     */
    public synchronized Optional<ButtonType> showConfirmation(String title, String header, String content) {
        return createAlert(Alert.AlertType.CONFIRMATION, title, header, content).showAndWait();
    }

    /**
     * Zeigt ein Bestätigungsfenster an
     *
     * @param title  Titel des Fensters
     * @param header Header Text
     * @return Gedrückter Button
     */
    public synchronized Optional<ButtonType> showConfirmation(String title, String header) {
        return createAlert(Alert.AlertType.CONFIRMATION, title, header).showAndWait();
    }

    /**
     * Zeigt ein Bestätigungsfenster an
     *
     * @param title   Titel des Fensters
     * @param header  Header Text
     * @param content Content Text
     * @return Gedrückter Button
     */
    public synchronized Optional<ButtonType> showAdvancedConfirmation(String title, String header, String content) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, content);
        setCustomConfirmationButtons(alert);
        return alert.showAndWait();
    }

    /**
     * Zeigt ein Informationsfenster an
     *
     * @param title   Titel des Fensters
     * @param header  Header Text
     * @param content Content Text
     */
    public synchronized void showInformation(String title, String header, String content) {
        createAlert(Alert.AlertType.INFORMATION, title, header, content).show();
    }

    /**
     * Zeigt ein Informationsfenster an
     *
     * @param title  Titel des Fensters
     * @param header Header Text
     */
    public synchronized void showInformation(String title, String header) {
        createAlert(Alert.AlertType.INFORMATION, title, header).show();
    }

    /**
     * Erstellt ein Alert nach übergebenen Werten
     *
     * @param type    Typ des Alerts
     * @param title   Titel des Fensters
     * @param header  Header Text
     * @param content Content Text
     * @return konfigurierter Alert
     */
    private synchronized Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }

    /**
     * Erstellt ein Alert nach übergebenen Werten
     *
     * @param type   Typ des Alerts
     * @param title  Titel des Fensters
     * @param header Header Text
     * @return konfigurierter Alert
     */
    private synchronized Alert createAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        return alert;
    }

    /**
     * Setzt die Buttons Ja / Nein / Abbrechen zum Alert
     *
     * @param alert Alert in dem die Buttons gesetzt werden sollen
     */
    private synchronized void setCustomConfirmationButtons(Alert alert) {
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    //endregion
}
