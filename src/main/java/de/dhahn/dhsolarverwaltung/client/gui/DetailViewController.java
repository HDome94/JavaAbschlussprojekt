package de.dhahn.dhsolarverwaltung.client.gui;

import de.dhahn.dhsolarverwaltung.client.gui.tableview.TableColumnBatteryCount;
import de.dhahn.dhsolarverwaltung.client.gui.tableview.TableColumnMaxPerfomance;
import de.dhahn.dhsolarverwaltung.client.gui.tableview.TableColumnPanelCount;
import de.dhahn.dhsolarverwaltung.client.gui.tableview.TableColumnTotalCapacity;
import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.Direction;
import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import de.dhahn.dhsolarverwaltung.client.settings.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

/**
 * Diese Klasse implementiert den Controller der Detail View
 */
public class DetailViewController {


    public static final String CAPACITY = "capacity";
    public static final String CHARGE = "charge";
    public static final String VALIDATOR_SELECTED_DIRECTION = "selectedDirection";
    //region Konstanten
    @FXML
    private Label lblSystemNr;
    @FXML
    private Label lblSystemNrTxt;

    @FXML
    private TextField txtStreet;
    @FXML
    private TextField txtHouseNr;
    @FXML
    private TextField txtPostalCode;
    @FXML
    private TextField txtCity;
    @FXML
    private ComboBox<Direction> cbxDirection;
    @FXML
    private TableView<SolarSystem> tableSolarSystems;
    @FXML
    private TableView<SolarPanel> tableSolarPanels;
    @FXML
    private TableView<Battery> tableBatteries;
    @FXML
    private VBox vboxDetailSection;

    @FXML
    private Button btnDeleteSelectedSystem;
    @FXML
    private Button btnDeleteCurrentSystem;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSaveCurrent;


    private SolarSystem selectedSystem;
    private boolean isNewSystem;
    private Validator validator = new Validator();


    /**
     * Init des Detail View Controllers
     */
    @FXML
    private void initialize() {
        initValidator();
        initSolarSystemsTable();
        initSolarPanelsTable();
        initBatteriesPanelsTable();
        cbxDirection.setItems(SolarSystemManager.getInstance().getDirections());

        showDetailSection(false);
    }

    /**
     * Init für die Validierungen der Textfelder
     */
    private void initValidator() {
        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtStreet.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input == null || input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtStreet.setText(error);
                    }
                })
                .decorates(txtStreet);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtHouseNr.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input == null || input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtHouseNr.setText(error);
                    }
                })
                .decorates(txtHouseNr);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtPostalCode.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input == null || input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtPostalCode.setText(error);
                    }
                })
                .decorates(txtPostalCode);

        validator.createCheck()
                .dependsOn(AppCommand.VALIDATOR_INPUT, txtCity.textProperty())
                .withMethod(context -> {
                    String input = context.get(AppCommand.VALIDATOR_INPUT);
                    String error = AppText.ERROR_EMPTY_TEXTBOX;
                    if (input == null || input.trim().isEmpty() || input.equals(error)) {
                        context.error(error);
                        txtCity.setText(error);
                    }
                })
                .decorates(txtCity);

        validator.createCheck()
                .dependsOn(VALIDATOR_SELECTED_DIRECTION, cbxDirection.valueProperty())
                .withMethod(context -> {
                    Direction selectedDirection = context.get(VALIDATOR_SELECTED_DIRECTION);

                    if (selectedDirection == null)
                        context.error(AppText.ERROR_EMPTY_TEXTBOX);

                })
                .decorates(cbxDirection);
    }

    /**
     * Init für Tableview der Systeme
     */
    private void initSolarSystemsTable() {
        TableColumn<SolarSystem, String> idCol = new TableColumn<>(AppText.NR);
        TableColumn<SolarSystem, Integer> countPanelCol = new TableColumn<>(AppText.PANEL_COUNT);
        TableColumn<SolarSystem, Double> maxPerformanceCol = new TableColumn<>(AppText.MAX_PERFORMANCE);
        TableColumn<SolarSystem, Integer> countBatteryCol = new TableColumn<>(AppText.BATTERY_COUNT);
        TableColumn<SolarSystem, Double> batteryCapacityCol = new TableColumn<>(AppText.OVERALL_BATTERY_CAPACITY);
        TableColumn<SolarSystem, String> directionCol = new TableColumn<>(AppText.ORIENTATION);
        TableColumn<SolarSystem, String> streetCol = new TableColumn<>(AppText.STREET);
        TableColumn<SolarSystem, String> houseNrCol = new TableColumn<>(AppText.HOUSE_NR);
        TableColumn<SolarSystem, String> postalCodeCol = new TableColumn<>(AppText.POSTAL_CODE);
        TableColumn<SolarSystem, String> cityCol = new TableColumn<>(AppText.CITY);

        idCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.ID));
        directionCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.DIRECTION));
        countPanelCol.setCellFactory(param -> new TableColumnPanelCount());
        maxPerformanceCol.setCellFactory(param -> new TableColumnMaxPerfomance());
        countBatteryCol.setCellFactory(param -> new TableColumnBatteryCount());
        batteryCapacityCol.setCellFactory(param -> new TableColumnTotalCapacity());
        streetCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.STREET));
        houseNrCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.HOUSE_NR));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.POSTAL_CODE));
        cityCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.CITY));

        tableSolarSystems.getColumns().add(idCol);
        tableSolarSystems.getColumns().add(countPanelCol);
        tableSolarSystems.getColumns().add(maxPerformanceCol);
        tableSolarSystems.getColumns().add(countBatteryCol);
        tableSolarSystems.getColumns().add(batteryCapacityCol);
        tableSolarSystems.getColumns().add(directionCol);
        tableSolarSystems.getColumns().add(streetCol);
        tableSolarSystems.getColumns().add(houseNrCol);
        tableSolarSystems.getColumns().add(postalCodeCol);
        tableSolarSystems.getColumns().add(cityCol);

        tableSolarSystems.setItems(SolarSystemManager.getInstance().getSolarSystems());

        tableSolarSystems.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    mouseEvent.getClickCount() == AppSettings.SCENE_SWITCH_CLICK_COUNT)
                editSelectedSystem();

        });
        tableSolarSystems.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                editSelectedSystem();

        });
    }

    /**
     * Init für Tableview der Solarpanels
     */
    private void initSolarPanelsTable() {
        TableColumn<SolarPanel, String> idCol = new TableColumn<>(AppText.NR);
        TableColumn<SolarPanel, Integer> brandCol = new TableColumn<>(AppText.BRAND);
        TableColumn<SolarPanel, Double> modelCol = new TableColumn<>(AppText.MODEL);
        TableColumn<SolarPanel, Integer> maximumWattCol = new TableColumn<>(AppText.MAX_PERFORMANCE);

        idCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.ID));
        brandCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.BRAND));
        modelCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.MODEL));
        maximumWattCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.MAXIMUM_WATT));

        tableSolarPanels.getColumns().add(idCol);
        tableSolarPanels.getColumns().add(brandCol);
        tableSolarPanels.getColumns().add(modelCol);
        tableSolarPanels.getColumns().add(maximumWattCol);

        // Events
        tableSolarPanels.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    mouseEvent.getClickCount() == AppSettings.SCENE_SWITCH_CLICK_COUNT)
                editSelectedPanel();

        });
        tableSolarPanels.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                editSelectedPanel();

        });
    }

    /**
     * Init für Tableview der Batterien
     */
    private void initBatteriesPanelsTable() {
        TableColumn<Battery, String> idCol = new TableColumn<>(AppText.NR);
        TableColumn<Battery, Integer> brandCol = new TableColumn<>(AppText.BRAND);
        TableColumn<Battery, Double> modelCol = new TableColumn<>(AppText.MODEL);
        TableColumn<Battery, Integer> maximumWattCol = new TableColumn<>(AppText.CAPACITY);
        TableColumn<Battery, Integer> chargeCol = new TableColumn<>(AppText.CHARGE);

        idCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.ID));
        brandCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.BRAND));
        modelCol.setCellValueFactory(new PropertyValueFactory<>(AppCommand.MODEL));
        maximumWattCol.setCellValueFactory(new PropertyValueFactory<>(CAPACITY));
        chargeCol.setCellValueFactory(new PropertyValueFactory<>(CHARGE));

        tableBatteries.getColumns().add(idCol);
        tableBatteries.getColumns().add(brandCol);
        tableBatteries.getColumns().add(modelCol);
        tableBatteries.getColumns().add(maximumWattCol);
        tableBatteries.getColumns().add(chargeCol);

        // Events
        tableBatteries.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    mouseEvent.getClickCount() == AppSettings.SCENE_SWITCH_CLICK_COUNT)
                editSelectedBattery();

        });
        tableBatteries.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                editSelectedBattery();

        });
    }

    /**
     * Setzt das zu bearbeitende System anhand der Auswahl der TableView.
     * Falls das aktuelle zu bearbeitende System ungespeicherte Änderungen hat, wird der Nutzer gefragt,
     * ob diese gespeichert werden sollen. Anschließend wird der Detailbereich neu geladen.
     */
    @FXML
    private void editSelectedSystem() {
        if (tableSolarSystems.getSelectionModel().getSelectedItem() == null) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_SELECT_SYSTEM_FIRST);
            return;
        }

        // Falls ungespeicherte Änderungen am aktuellen System bestehen nachfragen, ob gespeichert werden soll
        boolean saved = true;

        if (selectedSystem != null && getChangeCount() > 0) {
            saved = false;

            String alertContent = String.format(AppText.TEMPLATE_DETAIL_SYSTEM, selectedSystem.getId(),
                    selectedSystem.getStreet(), selectedSystem.getHouseNr(), selectedSystem.getPostalCode(),
                    selectedSystem.getCity());

            Optional<ButtonType> alertClickedButtonType =
                    AlertManager.getInstance().showAdvancedConfirmation(AppText.UNSAVED_CHANGES,
                            AppText.UNSAVED_CHANGES_HEADER, alertContent);

            if (alertClickedButtonType.isEmpty() ||
                    alertClickedButtonType.get() != ButtonType.YES && alertClickedButtonType.get() != ButtonType.NO)
                return;

            else if (alertClickedButtonType.get() == ButtonType.YES) saved = systemSave(null);
        }

        if (!saved) return;

        selectedSystem = tableSolarSystems.getSelectionModel().getSelectedItem();
        updateDetailsSection();
        isNewSystem = false;
    }


    /**
     * Aktualisiert die Elemente im Detail Bereich und blendet diesen ein
     */
    @FXML
    private void updateDetailsSection() {
        if (selectedSystem == null)
            return;

        lblSystemNr.setText(String.valueOf(selectedSystem.getId()));
        cbxDirection.getSelectionModel().select(selectedSystem.getDirection());
        txtStreet.setText(selectedSystem.getStreet());
        txtHouseNr.setText(selectedSystem.getHouseNr());
        txtPostalCode.setText(selectedSystem.getPostalCode());
        txtCity.setText(selectedSystem.getCity());

        tableSolarPanels.setItems(selectedSystem.getSolarPanels());
        tableBatteries.setItems(selectedSystem.getBatteries());
        resizeTableViewColumns();

        // Button nur im Edit mode anzeigen
        btnDeleteCurrentSystem.setVisible(!isNewSystem);

        showDetailSection(true);
    }

    /**
     * Richtet die breite der Spalten, der Solar-Panel und Batterie Tableview, nach Header und Inhalt aus
     */
    private void resizeTableViewColumns() {
        tableSolarPanels.getColumns().forEach(column -> {
            Text headerText = new Text(column.getText());
            headerText.setStyle(AppSettings.TABLE_COLUMN_FONT);
            double headerWidth = headerText.getLayoutBounds().getWidth();

            // Berechne die maximale Breite basierend auf den Inhalten der Spalte
            double maxContentWidth = 0;
            for (SolarPanel panel : tableSolarPanels.getItems()) {
                if (column.getCellData(panel) != null) {
                    Text contentText = new Text(column.getCellData(panel).toString());
                    double contentWidth = contentText.getLayoutBounds().getWidth();
                    if (contentWidth > maxContentWidth) {
                        maxContentWidth = contentWidth;
                    }
                }
            }

            double finalWidth = Math.max(headerWidth, maxContentWidth) + AppSettings.TABLE_COLUMN_PADDING;
            column.setPrefWidth(finalWidth);
        });

        tableBatteries.getColumns().forEach(column -> {
            Text headerText = new Text(column.getText());
            headerText.setStyle(AppSettings.TABLE_COLUMN_FONT);
            double headerWidth = headerText.getLayoutBounds().getWidth();

            // Berechne die maximale Breite basierend auf den Inhalten der Spalte
            double maxContentWidth = 0;
            for (Battery battery : tableBatteries.getItems()) {
                if (column.getCellData(battery) != null) {
                    Text contentText = new Text(column.getCellData(battery).toString());
                    double contentWidth = contentText.getLayoutBounds().getWidth();
                    if (contentWidth > maxContentWidth) {
                        maxContentWidth = contentWidth;
                    }
                }
            }

            double finalWidth = Math.max(headerWidth, maxContentWidth) + AppSettings.TABLE_COLUMN_PADDING;
            column.setPrefWidth(finalWidth);
        });
    }


    /**
     * Blendet den Detailbereich ein und aus
     */
    private void showDetailSection(boolean shown) {
        vboxDetailSection.setVisible(shown);
    }

    /**
     * Legt ein neues System an und lädt den Detailbereich
     */
    @FXML
    private void addNewSystem() {
        selectedSystem = new SolarSystem();
        isNewSystem = true;

        AlertManager.getInstance().showInformation(AppText.NEW_SYSTEM, AppText.NEW_SYSTEM_SELECTED_NOW_EDITABLE);
        updateDetailsSection();
    }

    /**
     * Löscht eine Solaranlage, sofern ausgewählt und vorhanden aus der Datenbank. Nutzer muss dies zunächst bestätigen.
     */
    @FXML
    private void deleteSelectedSystem(ActionEvent e) {
        SolarSystem systemToDelete = null;

        // Je nach Button ist ausgewähltes System in TableView oder im Detail Bereich gemeint
        if (e.getSource() == btnDeleteSelectedSystem)
            systemToDelete = tableSolarSystems.getSelectionModel().getSelectedItem();
        else if (e.getSource() == btnDeleteCurrentSystem)
            systemToDelete = selectedSystem;

        // Wenn nichts ausgewählt oder System nicht in Liste vorhanden zeige Error
        if (systemToDelete == null) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_DELETE_SELECT_SYSTEM_FIRST);
            return;
        } else if (!SolarSystemManager.getInstance().getSolarSystems().contains(systemToDelete)) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_DELETE_SELECTED_SYSTEM_NOT_IN_DB);
            return;
        }

        // Bestätigung
        String alertContent =  String.format(AppText.TEMPLATE_DETAIL_SYSTEM,
                systemToDelete.getId(), systemToDelete.getStreet(), systemToDelete.getHouseNr(),
                systemToDelete.getPostalCode(), systemToDelete.getCity());

        Optional<ButtonType> alertClickedButtonType = AlertManager.getInstance()
                .showConfirmation(AppText.CONFIRM_DELETE_TITLE, AppText.CONFIRM_DELETE_SYSTEM_HEADER, alertContent);

        if (alertClickedButtonType.isEmpty() || alertClickedButtonType.get() != ButtonType.OK) return;

        SolarSystemManager.getInstance().getSolarSystems().remove(systemToDelete);

        if (selectedSystem == systemToDelete) {
            selectedSystem = null;
            showDetailSection(false);
        }
    }

    /**
     * Öffnet das Detail Popup zum Anlegen einer neuen Batterie
     */
    @FXML
    private void addNewBattery() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);
            return;
        }

        SceneManager.getInstance().openBatteryDetailPopup(View.DETAIL_BATTERY, selectedSystem, null);
    }

    /**
     * Öffnet das Detail Popup zum Bearbeiten der ausgewählten Batterie
     */
    @FXML
    private void editSelectedBattery() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);
            return;
        }

        Battery selectedBattery = tableBatteries.getSelectionModel().getSelectedItem();
        if (selectedBattery == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_BATTERY_FIRST);
            return;
        }
        SceneManager.getInstance().openBatteryDetailPopup(View.DETAIL_BATTERY, selectedSystem, selectedBattery);
    }

    /**
     * Löscht die ausgewählte Batterie aus dem System, sofern der Nutzer dies bestätigt
     */
    @FXML
    private void deleteSelectedBattery() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);
            return;
        }

        Battery selectedBattery = tableBatteries.getSelectionModel().getSelectedItem();
        if (selectedBattery == null) {
            AlertManager.getInstance().showError(AppText.ERROR_DELETE_TITLE, AppText.ERROR_SELECT_BATTERY_FIRST);
            return;
        }

        Optional<ButtonType> alertClickedButtonType = AlertManager.getInstance().showConfirmation(
                AppText.CONFIRM_DELETE_TITLE, AppText.CONFIRM_DELETE_BATTERY_HEADER,
                String.format(AppText.TEMPLATE_DETAIL_BATTERY,
                        selectedBattery.getId(), selectedBattery.getBrand(), selectedBattery.getModel()));

        if (alertClickedButtonType.isEmpty() || alertClickedButtonType.get() != ButtonType.OK) return;

        selectedSystem.getBatteries().remove(selectedBattery);
    }

    /**
     * Öffnet das Detail Popup zum Anlegen eines neuen Panels
     */
    @FXML
    private void addNewPanel() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);
            return;
        }

        SceneManager.getInstance().openPanelDetailPopup(View.DETAIL_SOLARPANEL, selectedSystem, null);
    }

    /**
     * Öffnet das Detail Popup zum Bearbeiten des ausgewählten Panels
     */
    @FXML
    private void editSelectedPanel() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);
            return;
        }

        SolarPanel selectedPanel = tableSolarPanels.getSelectionModel().getSelectedItem();
        if (selectedPanel == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_PANEL_FIRST);
            return;
        }

        SceneManager.getInstance().openPanelDetailPopup(View.DETAIL_SOLARPANEL, selectedSystem, selectedPanel);
    }

    /**
     * Löscht das ausgewählte Panel aus dem System, sofern der Nutzer dies bestätigt
     */
    @FXML
    private void deleteSelectedPanel() {
        if (selectedSystem == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS,
                    AppText.ERROR_OPEN_DETAILS_SELECT_SYSTEM_FIRST);

            return;
        }

        SolarPanel selectedPanel = tableSolarPanels.getSelectionModel().getSelectedItem();
        if (selectedPanel == null) {
            AlertManager.getInstance().showError(AppText.ERROR_OPEN_DETAILS, AppText.ERROR_SELECT_PANEL_FIRST);
            return;
        }

        String alertContent = String.format(AppText.TEMPLATE_DETAIL_PANEL, selectedPanel.getId(),
                selectedPanel.getBrand(), selectedPanel.getModel(), selectedPanel.getMaximumWatt());

        Optional<ButtonType> alertClickedButtonType = AlertManager.getInstance().showConfirmation(
                AppText.CONFIRM_DELETE_TITLE, AppText.CONFIRM_DELETE_BATTERY_HEADER, alertContent);

        if (alertClickedButtonType.isEmpty() || alertClickedButtonType.get() != ButtonType.OK) return;

        selectedSystem.getSolarPanels().remove(selectedPanel);
    }

    /**
     * Wechselt zurück zur Main View (Dashboard)
     */
    @FXML
    private void backToMainView() {
        double width = SceneManager.getInstance().getCurrentWidth();
        double height = SceneManager.getInstance().getCurrentHeight();

        if (width == AppSettings.DEFAULT_WIDTH && height == AppSettings.DEFAULT_HEIGTH)
            SceneManager.getInstance().setScene(View.MAIN);
        else
            SceneManager.getInstance().setScene(View.MAIN, width, height);

    }

    /**
     * Speichert das aktuelle System oder legt ein neues System an und fügt es der Liste hinzu, sofern alle Eingaben
     * valide sind
     */
    @FXML
    private boolean systemSave(ActionEvent e) {
        if (selectedSystem == null) {
            backToMainView();
            return false;
        }

        boolean isValid = validator.validate();
        if (!isValid) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_EMPTY_FIELDS_HEADER,
                    AppText.ERROR_ALL_MASKED_FIELDS_MUST_FILLED);
            return false;
        }

        save();

        if (e != null && e.getSource() == btnSave)
            backToMainView();

        return true;
    }

    /**
     * Speichert das aktuelle System oder legt ein neues System an und fügt es der Liste hinzu
     */
    private void save() {
        if (isNewSystem) {
            selectedSystem.startBatchUpdate();
            selectedSystem.setDirection(cbxDirection.getValue());
            selectedSystem.setStreet(txtStreet.getText());
            selectedSystem.setHouseNr(txtHouseNr.getText());
            selectedSystem.setPostalCode(txtPostalCode.getText());
            selectedSystem.finishBatchUpdate();
            selectedSystem.setCity(txtCity.getText());

            SolarSystemManager.getInstance().getSolarSystems().add(selectedSystem);
            isNewSystem = false;

        } else {
            boolean isBatchUpdate = false;
            if (getChangeCount() > 1) {
                selectedSystem.startBatchUpdate();
                isBatchUpdate = true;
            }

            if (!selectedSystem.getDirection().equals(cbxDirection.getValue()))
                selectedSystem.setDirection(cbxDirection.getValue());

            if (!selectedSystem.getStreet().equals(txtStreet.getText()))
                selectedSystem.setStreet(txtStreet.getText());

            if (!selectedSystem.getHouseNr().equals(txtHouseNr.getText()))
                selectedSystem.setHouseNr(txtHouseNr.getText());

            if (!selectedSystem.getPostalCode().equals(txtPostalCode.getText()))
                selectedSystem.setPostalCode(txtPostalCode.getText());

            if (isBatchUpdate)
                selectedSystem.finishBatchUpdate();

            // Falls mehrere Änderungen setzte City neu, um Event auszulösen
            if (!selectedSystem.getCity().equals(txtCity.getText()) || isBatchUpdate)
                selectedSystem.setCity(txtCity.getText());

            isBatchUpdate = false;
        }
        AlertManager.getInstance().showInformation(AppText.SAVE_SUCCESSFUL, AppText.SAVE_SUCCESSFUL_SYSTEM_HEADER);
    }

    /**
     * Prüft wie viele direkte Attribute des Systems geändert wurden.
     * Nötig um nicht mehrere Events beim Speichern zu feuern.
     *
     * @return Anzahl an direkten Änderungen
     */
    private int getChangeCount() {
        int changeCount = 0;

        if (!selectedSystem.getDirection().equals(cbxDirection.getValue()))
            changeCount++;
        if (!selectedSystem.getStreet().equals(txtStreet.getText()))
            changeCount++;
        if (!selectedSystem.getHouseNr().equals(txtHouseNr.getText()))
            changeCount++;
        if (!selectedSystem.getPostalCode().equals(txtPostalCode.getText()))
            changeCount++;
        if (!selectedSystem.getCity().equals(txtCity.getText()))
            changeCount++;

        return changeCount;
    }
    //endregion
}
