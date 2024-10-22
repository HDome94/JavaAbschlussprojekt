package de.dhahn.dhsolarverwaltung.client.gui;

import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

/**
 * Diese Klasse implementiert den Controller des Panels Detail Popups
 */
public class SolarPanelDetailViewController {


    @FXML
    private Label lblPanelNr;
    @FXML
    private Label lblPanelNrTxt;
    @FXML
    private Label lblBrand;
    @FXML
    private Label lblModel;
    @FXML
    private Label lblMaximumWatt;
    @FXML
    private Label lblCount;


    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtMaximumWatt;
    @FXML
    private TextField txtCount;


    private SolarPanel panel;
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
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtMaximumWatt.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;

                    if (input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtMaximumWatt.setText(error);
                    } else {
                        error = AppText.ERROR_NUMERIC_TEXTBOX;

                        try {
                            Integer.parseInt(txtMaximumWatt.getText());

                        } catch (Exception e) {
                            context.error(error);
                            txtMaximumWatt.setText(error);
                        }
                    }
                })
                .decorates(txtMaximumWatt);
    }

    /**
     * Speichert das aktuelle Panel (im Edit Mode) oder legt die Panels an
     */
    @FXML
    private void save() {
        boolean isValid = validator.validate();
        if (!isValid) return;

        // Neue Panel Anlegen
        if (panel == null) {
            int count = Integer.parseInt(txtCount.getText());

            // Um mehrere gleichzeitig anzulegen
            for (int i = 1; i <= count; i++) {
                panel = new SolarPanel(txtBrand.getText(), txtModel.getText(), Integer.parseInt(txtMaximumWatt.getText()));
                system.getSolarPanels().add(panel);
            }

            // Bestehendes Panel editieren
        } else {
            boolean isBatchUpdate = false;
            if (getChangeCount() > 1) {
                panel.startBatchUpdate();
                isBatchUpdate = true;
            }

            if (!panel.getModel().equals(txtModel.getText())) {
                panel.setModel(txtModel.getText());
            }
            if (panel.getMaximumWatt() != Integer.parseInt(txtMaximumWatt.getText()))
                panel.setMaximumWatt(Integer.parseInt(txtMaximumWatt.getText()));

            if (isBatchUpdate)
                panel.finishBatchUpdate();

            // Falls mehrere Änderungen setzte Marke auf jeden Fall neu, um Event auszulösen
            if (!panel.getBrand().equals(txtBrand.getText()) || isBatchUpdate)
                panel.setBrand(txtBrand.getText());
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

        if (!panel.getBrand().equals(txtBrand.getText()))
            changeCount++;
        if (!panel.getModel().equals(txtModel.getText()))
            changeCount++;
        if (panel.getMaximumWatt() != Integer.parseInt(txtMaximumWatt.getText()))
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
     * Löscht das aktuelle Panel (nur im Edit Mode sichtbar)
     */
    @FXML
    private void deletePanel() {
        Optional<ButtonType> alertClickedButtonType = AlertManager.getInstance().showConfirmation(
                AppText.CONFIRM_DELETE_TITLE, AppText.CONFIRM_DELETE_PANEL_HEADER,
                String.format(AppText.TEMPLATE_DETAIL_PANEL,
                        panel.getId(), panel.getBrand(), panel.getModel(), panel.getMaximumWatt()));

        if (alertClickedButtonType.isEmpty() || alertClickedButtonType.get() != ButtonType.OK) return;

        system.getSolarPanels().remove(panel);
    }

    /**
     * Übergibt die aktuelle Solaranlage und das Panel, falls editiert wird.
     * Setzt ggf die Sichtbarkeit der Elemente für den Edit Mode
     *
     * @param system Die aktuelle Anlage die zur Batterie zugeordnet ist
     * @param panel  Panel zum Bearbeiten | null für neue anlegen
     */
    public void loadSystemAndPanel(SolarSystem system, SolarPanel panel) {
        this.system = system;
        if (panel != null) {
            this.panel = panel;
            lblPanelNr.setText(String.valueOf(panel.getId()));
            txtBrand.setText(panel.getBrand());
            txtModel.setText(panel.getModel());
            txtMaximumWatt.setText(String.valueOf(panel.getMaximumWatt()));

            lblCount.setVisible(false);
            txtCount.setVisible(false);
            txtCount.setText("0");
        }
    }
}
