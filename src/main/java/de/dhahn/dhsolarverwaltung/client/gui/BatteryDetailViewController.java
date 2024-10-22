package de.dhahn.dhsolarverwaltung.client.gui;

import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

/**
 * Diese Klasse implementiert den Controller des Batterie Detail Popups
 */
public class BatteryDetailViewController {


    @FXML
    private Label lblBatteryNr;
    @FXML
    private Label lblBatteryNrTxt;
    @FXML
    private Label lblBrand;
    @FXML
    private Label lblModel;
    @FXML
    private Label lblCapacity;
    @FXML
    private Label lblCount;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtCapacity;
    @FXML
    private TextField txtCount;

    @FXML
    private Button btnDelete;

    private Battery battery;
    private SolarSystem system;

    private final Validator validator = new Validator();


    /**
     * Legt die Checks für die Validation an
     */
    @FXML
    private void initialize() {
        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtCount.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtCount.setText(error);
                    } else {
                        error = AppText.ERROR_NUMERIC_TEXTBOX;
                        try {
                            Integer.parseInt(txtCount.getText());
                        } catch (Exception e) {
                            context.error(error);
                            txtCount.setText(error);
                        }
                    }
                })
                .decorates(txtCount);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtBrand.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtBrand.setText(error);
                    }
                })
                .decorates(txtBrand);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtModel.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtModel.setText(error);
                    }
                })
                .decorates(txtModel);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtCapacity.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtCapacity.setText(error);
                    } else {
                        error = AppText.ERROR_NUMERIC_TEXTBOX;
                        try {
                            Double.parseDouble(txtCapacity.getText());
                        } catch (Exception e) {
                            context.error(error);
                            txtCapacity.setText(error);
                        }
                    }
                })
                .decorates(txtCapacity);

        btnDelete.setVisible(false);
    }

    /**
     * Speichert die aktuelle Batterie (im Edit Mode) oder legt die Batterien an
     */
    @FXML
    private void save() {
        boolean isValid = validator.validate();
        if (!isValid) return;

        // Neue Batterie Anlegen
        if (battery == null) {
            int count = Integer.parseInt(txtCount.getText());

            // Um mehrere gleichzeitig anzulegen
            for (int i = 1; i <= count; i++) {
                battery = new Battery(txtBrand.getText(), txtModel.getText(), Double.parseDouble(txtCapacity.getText()));
                system.getBatteries().add(battery);
            }

            // Bestehende Batterie editieren
        } else {
            boolean isBatchUpdate = false;
            if (getChangeCount() > 1) {
                battery.startBatchUpdate();
                isBatchUpdate = true;
            }

            if (!battery.getModel().equals(txtModel.getText()))
                battery.setModel(txtModel.getText());
            if (battery.getCapacity() != Double.parseDouble(txtCapacity.getText()))
                battery.setCapacity(Double.parseDouble(txtCapacity.getText()));

            if (isBatchUpdate)
                battery.finishBatchUpdate();

            // Falls mehrere Änderungen setzte Marke auf jeden Fall neu, um Event auszulösen
            if (!battery.getBrand().equals(txtBrand.getText()) || isBatchUpdate)
                battery.setBrand(txtBrand.getText());
        }

        SceneManager.getInstance().closePopup();
    }

    /**
     * Prüft wie viele Änderungen es gibt.
     * Nötig um nicht mehrere Events beim Speichern zu feuern.
     *
     * @return Anzahl an Änderungen
     */
    private int getChangeCount() {
        int changeCount = 0;

        if (!battery.getBrand().equals(txtBrand.getText()))
            changeCount++;
        if (!battery.getModel().equals(txtModel.getText()))
            changeCount++;
        if (battery.getCapacity() != Double.parseDouble(txtCapacity.getText()))
            changeCount++;

        return changeCount;
    }

    /**
     * Bricht den Vorgang ab und schließt das Popup
     */
    @FXML
    private void cancel() {
        SceneManager.getInstance().closePopup();
    }

    /**
     * Löscht die aktuelle Batterie (nur im Edit Mode sichtbar)
     */
    @FXML
    private void deleteBattery() {
        Optional<ButtonType> alertClickedButtonType = AlertManager.getInstance().showConfirmation(
                AppText.CONFIRM_DELETE_TITLE, AppText.CONFIRM_DELETE_BATTERY_HEADER,
                String.format(AppText.TEMPLATE_DETAIL_BATTERY,
                        battery.getId(), battery.getBrand(), battery.getModel()));

        if (alertClickedButtonType.isEmpty() || alertClickedButtonType.get() != ButtonType.OK) return;

        system.getBatteries().remove(battery);
    }

    /**
     * Übergibt die aktuelle Solaranlage und die Batterie, falls editiert wird.
     * Setzt ggf die Sichtbarkeit der Elemente für den Edit Mode
     *
     * @param system  Die aktuelle Anlage die zur Batterie zugeordnet ist
     * @param battery Batterie zum Bearbeiten | null für neue anlegen
     */
    public void loadSystemAndBattery(SolarSystem system, Battery battery) {
        this.system = system;
        if (battery != null) {
            this.battery = battery;
            lblBatteryNr.setText(String.valueOf(battery.getId()));
            txtBrand.setText(battery.getBrand());
            txtModel.setText(battery.getModel());
            txtCapacity.setText(String.valueOf(battery.getCapacity()));

            lblCount.setVisible(false);
            txtCount.setVisible(false);
            btnDelete.setVisible(true);
            txtCount.setText("0");
        }
    }

}
