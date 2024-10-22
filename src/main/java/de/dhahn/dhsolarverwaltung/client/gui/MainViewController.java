package de.dhahn.dhsolarverwaltung.client.gui;

import de.dhahn.dhsolarverwaltung.client.logic.SolarSystemManager;
import de.dhahn.dhsolarverwaltung.client.logic.db.DbManager;
import de.dhahn.dhsolarverwaltung.client.model.*;
import de.dhahn.dhsolarverwaltung.client.settings.AppCommand;
import de.dhahn.dhsolarverwaltung.client.settings.AppSettings;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import de.dhahn.dhsolarverwaltung.client.settings.View;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse implementiert den Controller der Main View
 */
public class MainViewController {

    //TODO Lokalisierung
    //TODO Statistiken einbauen resize Fix

    @FXML
    private Label lblElectricitySalesTxt;
    @FXML
    private Label lblElectricitySales;
    @FXML
    private Label lblElectricityPurchaseTxt;
    @FXML
    private Label lblElectricityPurchase;
    @FXML
    private Label lblOverallPerformanceTxt;
    @FXML
    private Label lblOverallPerformance;
    @FXML
    private Label lblOverallConsumptionTxt;
    @FXML
    private Label lblOverallConsumption;
    @FXML
    private Label lblSystemCountTxt;
    @FXML
    private Label lblSystemCount;
    @FXML
    private Label lblMaximumPerformanceTxt;
    @FXML
    private Label lblMaximumPerformance;
    @FXML
    private Label lblBatteryOverallCountTxt;
    @FXML
    private Label lblBatteryOverallCount;
    @FXML
    private Label lblOverallCapacityTxt;
    @FXML
    private Label lblOverallCapacity;
    @FXML
    private Label lblOverallChargeTxt;
    @FXML
    private Label lblOverallCharge;


    @FXML
    private AreaChart<String, Number> productionChart;
    @FXML
    private CategoryAxis dateAxisProductionChart;
    @FXML
    private NumberAxis yAxisProductionChart;
    @FXML
    private BarChart<String, Number> electricitySalesChart;
    @FXML
    private CategoryAxis dateAxisElectricitySalesChart;
    @FXML
    private NumberAxis yAxisElectricitySalesChart;

    private final XYChart.Series<String, Number> productionSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> consumptionSeries = new XYChart.Series<>();

    private final XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> purchaseSeries = new XYChart.Series<>();


    @FXML
    private void initialize() {
        SolarSystemManager.getInstance().getSolarSystems()
                .addListener((ListChangeListener<SolarSystem>) change -> reloadHeader());

        // Setze innere Listener, Messwerte und Strom Ein-Verkäufe von ApiThread deshalb runLater.
        for (SolarSystem system : SolarSystemManager.getInstance().getSolarSystems()) {
            system.getSolarPanels().addListener((ListChangeListener<SolarPanel>)
                    change -> reloadHeader());
            system.getBatteries().addListener((ListChangeListener<Battery>)
                    change -> reloadHeader());
            system.getSolarMeasurements().addListener((ListChangeListener<Measurement>)
                    change -> reloadHeaderShort());
            system.getConsumptionMeasurements().addListener((ListChangeListener<Measurement>)
                    change -> reloadHeaderShort());
            system.getElectricityPurchases().addListener((ListChangeListener<ElectricitySale>)
                    change -> reloadHeaderShort());
            system.getElectricitySales().addListener((ListChangeListener<ElectricitySale>)
                    change -> reloadHeaderShort());
        }

        // Initialisiere die Charts mit den Datenreihen
        Font chartFont = Font.font("Arial", FontWeight.BOLD, 14);

        productionSeries.setName(AppText.PRODUCTION);
        consumptionSeries.setName(AppText.CONSUMPTION);
        yAxisProductionChart.setTickLabelFont(chartFont);
        dateAxisProductionChart.setLabel(AppText.DATE_AXIS_LABEL);
        dateAxisProductionChart.setTickLabelFont(chartFont);

        productionChart.getData().addAll(productionSeries, consumptionSeries);

        salesSeries.setName(AppText.SALE);
        purchaseSeries.setName(AppText.PURCHASE);
        yAxisElectricitySalesChart.setLabel(AppSettings.CURRENCY);
        yAxisElectricitySalesChart.setTickLabelFont(chartFont);
        dateAxisElectricitySalesChart.setLabel(AppText.DATE_AXIS_LABEL);
        dateAxisElectricitySalesChart.setTickLabelFont(chartFont);

        electricitySalesChart.getData().addAll(salesSeries, purchaseSeries);

        reloadHeader();
        updateCharts(LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }

    /**
     * Aktualisiert alle Header Informationen
     */
    private void reloadHeader() {
        lblElectricitySales.setText(String.format(AppText.TEMPLATE_CASH,
                SolarSystemManager.getInstance().getElectricityRevenueLastDay()));

        lblElectricityPurchase.setText(String.format(AppText.TEMPLATE_CASH,
                SolarSystemManager.getInstance().getElectricityCostsLastDay()));

        lblOverallPerformance.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallPerformance()));

        lblOverallConsumption.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallConsumption()));

        lblSystemCount.setText(String.valueOf(SolarSystemManager.getInstance().getSolarSystems().size()));

        lblMaximumPerformance.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getMaximumPerformance()));

        lblBatteryOverallCount.setText(String.valueOf(SolarSystemManager.getInstance().getOverallBatteriesCount()));

        lblOverallCapacity.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getOverallBatteriesCapacity()));

        lblOverallCharge.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallCharge()));
    }

    /**
     * Aktualisiert die Header Informationen Leistung Verbrauch und Akkustand.
     */
    private void reloadHeaderShort() {
        lblElectricitySales.setText(String.format(AppText.TEMPLATE_CASH,
                SolarSystemManager.getInstance().getElectricityRevenueLastDay()));

        lblElectricityPurchase.setText(String.format(AppText.TEMPLATE_CASH,
                SolarSystemManager.getInstance().getElectricityCostsLastDay()));

        lblOverallPerformance.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallPerformance()));

        lblOverallConsumption.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallConsumption()));

        lblOverallCharge.setText(String.format(AppText.TEMPLATE_WATT,
                SolarSystemManager.getInstance().getCurrentOverallCharge()));
    }


    /**
     * Zum Aktualisieren der Statistiken
     *
     * @param startDate Start Datum der Auswertung
     * @param endDate   End Datum der Auswertung
     */
    private void updateCharts(LocalDateTime startDate, LocalDateTime endDate) {
        electricitySalesChart.setVisible(false);
        productionChart.setVisible(false);

        String dateAxisText;
        Duration d = Duration.between(startDate, endDate);

        if (d.toDays() <= AppCommand.GROUP_OFF_BY_HOUR)
            dateAxisText = AppCommand.DATE_AXIS_PATTERN_HOUR;

        else if (d.toDays() <= AppCommand.GROUP_OFF_BY_DAY)
            dateAxisText = AppCommand.DATE_AXIS_PATTERN_DAY;

        else if (d.toDays() <= AppCommand.GROUP_OFF_BY_MONTH)
            dateAxisText = AppCommand.DATE_AXIS_PATTERN_MONTH;

        else
            dateAxisText = AppCommand.DATE_AXIS_PATTERN_YEAR;

        updateProductionChart(startDate, endDate, dateAxisText);
        uodateElectricitySalesChart(startDate, endDate, dateAxisText);

        electricitySalesChart.setVisible(true);
        productionChart.setVisible(true);
    }

    /**
     * Aktualisiert die Area Chart für die Produktion und Verbrauch
     *
     * @param startDate    Anfangsdatum
     * @param endDate      Enddatum
     * @param dateAxisText X-Achsen Beschriftung (formatiertes Datum)
     */
    private void updateProductionChart(LocalDateTime startDate, LocalDateTime endDate, String dateAxisText) {
        productionSeries.getData().clear();
        consumptionSeries.getData().clear();

        List<MeasurementEvaluation> solarMeasurements =
                DbManager.getInstance().readMeasurementEvaluations(true, startDate, endDate);

        List<MeasurementEvaluation> consumptionMeasurements =
                DbManager.getInstance().readMeasurementEvaluations(false, startDate, endDate);


        // Formatieren der Zeitstempel
        List<String> filteredDates = new ArrayList<>();
        formatAndFilterDatesForProductionChart(solarMeasurements, filteredDates, dateAxisText);
        formatAndFilterDatesForProductionChart(consumptionMeasurements, filteredDates, dateAxisText);

        // Setze die formatierten Zeitwerte als Kategorien
        try {
            dateAxisProductionChart.setCategories(FXCollections.observableList(filteredDates));
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_DATE_AXIS, e.getMessage());
        }

        // Füge die Messwerte zur Serie hinzu
        solarMeasurements.forEach(m -> addToSeries(productionSeries, m, dateAxisText));
        consumptionMeasurements.forEach(m -> addToSeries(consumptionSeries, m, dateAxisText));

        // Falls 20 oder mehr Zeitstempel auf der X Date Achse drehe Label um 90°
        if (dateAxisProductionChart.getCategories().size() >= 20)
            dateAxisProductionChart.setTickLabelRotation(90);
        else
            dateAxisProductionChart.setTickLabelRotation(0);

        dateAxisProductionChart.autosize();
    }

    /**
     * Fügt ein Wert der Chart Serie hinzu
     *
     * @param series       Chart Serie wo hinzugefügt werden soll
     * @param evaluation   Messwert der hinzugefügt werden soll
     * @param dateAxisText Text der Datumsachse
     */
    private void addToSeries(XYChart.Series<String, Number> series, MeasurementEvaluation evaluation, String dateAxisText) {
        String dateString =  evaluation.getDateTime().format(DateTimeFormatter.ofPattern(dateAxisText));
        series.getData().add(new XYChart.Data<>(dateString, evaluation.getAvg_watt()));
    }

    /**
     * Fügt ein Wert der Chart Serie hinzu
     *
     * @param series       Chart Serie wo hinzugefügt werden soll
     * @param evaluation   Messwert der hinzugefügt werden soll
     * @param dateAxisText Text der Datumsachse
     */
    private void addToSeries(XYChart.Series<String, Number> series, ElectricitySaleEvaluation evaluation,
                             String dateAxisText) {

        String dateString =  evaluation.getDateTime().format(DateTimeFormatter.ofPattern(dateAxisText));
        double price = evaluation.getTotal_watt() / 1000 * evaluation.getAvg_price();

        series.getData().add(new XYChart.Data<>(dateString, price));
    }

    /**
     * Formatiert die Datumswerte für die X Achse nach dem übergebenen Pattern
     *
     * @param evaluations   Liste der Auswertungen
     * @param filteredDates Liste für die gefilterten Datum Strings
     * @param dateAxisText  Date Pattern der Achse
     */
    private void formatAndFilterDatesForProductionChart(List<MeasurementEvaluation> evaluations,
                                                        List<String> filteredDates, String dateAxisText) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateAxisText);
        List<String> formattedDates = evaluations.stream()
                .map(evaluation -> formatter.format(evaluation.getDateTime()))
                .toList();

        for (String date : formattedDates)
            if (!filteredDates.contains(date))
                filteredDates.add(date);
    }

    /**
     * Formatiert die Datumswerte für die X Achse nach dem übergebenen Pattern
     *
     * @param evaluations   Liste der Auswertungen
     * @param filteredDates Liste für die gefilterten Datum Strings
     * @param dateAxisText  Date Pattern der Achse
     */
    private void formatAndFilterDatesForSalesChart(List<ElectricitySaleEvaluation> evaluations,
                                                   List<String> filteredDates, String dateAxisText) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateAxisText);
        List<String> formattedDates = evaluations.stream()
                .map(evaluation -> formatter.format(evaluation.getDateTime()))
                .toList();

        for (String date : formattedDates)
            if (!filteredDates.contains(date))
                filteredDates.add(date);
    }

    /**
     * Aktualisiert die Bar Chart für Strom Ein und Verkäufe
     *
     * @param startDate    Anfangsdatum
     * @param endDate      Enddatum
     * @param dateAxisText X-Achsen Beschriftung (formatiertes Datum)
     */
    private void uodateElectricitySalesChart(LocalDateTime startDate, LocalDateTime endDate, String dateAxisText) {
        salesSeries.getData().clear();
        purchaseSeries.getData().clear();

        List<ElectricitySaleEvaluation> electricityPurchases =
                DbManager.getInstance().readElectricitySaleEvaluations(true, startDate, endDate);

        List<ElectricitySaleEvaluation> electricitySales =
                DbManager.getInstance().readElectricitySaleEvaluations(false, startDate, endDate);


        // Formatieren der Zeitstempel
        List<String> filteredDates = new ArrayList<>();
        formatAndFilterDatesForSalesChart(electricityPurchases, filteredDates, dateAxisText);
        formatAndFilterDatesForSalesChart(electricitySales, filteredDates, dateAxisText);

        // Setze die formatierten Zeitwerte als Kategorien
        try {
            dateAxisElectricitySalesChart.setCategories(FXCollections.observableList(filteredDates));
        } catch (Exception e) {
            AlertManager.getInstance().showError(AppText.ERROR, AppText.ERROR_DATE_AXIS, e.getMessage());
        }

        // Füge die Messwerte zur Serie hinzu
        electricitySales.forEach(m -> addToSeries(salesSeries, m, dateAxisText));
        electricityPurchases.forEach(m -> addToSeries(purchaseSeries, m, dateAxisText));


        // Falls 20 oder mehr Zeitstempel auf der X Date Achse drehe Label um 90°
        if (dateAxisElectricitySalesChart.getCategories().size() >= 20)
            dateAxisElectricitySalesChart.setTickLabelRotation(90);

        else
            dateAxisElectricitySalesChart.setTickLabelRotation(0);

        yAxisElectricitySalesChart.autosize();
        dateAxisElectricitySalesChart.autosize();
    }


    /**
     * Filter der Charts → Letzter Tag
     */
    @FXML
    private void filterLastDay() {
        updateCharts(LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }

    /**
     * Filter der Charts → Letzten 2 Tage
     */
    @FXML
    private void filterLastTwoDays() {
        updateCharts(LocalDateTime.now().minusDays(2), LocalDateTime.now());
    }

    /**
     * Filter der Charts → Letzten Monat
     */
    @FXML
    private void filterLastMonth() {
        updateCharts(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
    }

    /**
     * Filter der Charts → Letzten 3 Monate
     */
    @FXML
    private void filterLastThreeMonths() {
        updateCharts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
    }

    /**
     * Filter der Charts → Letztes Jahr
     */
    @FXML
    private void filterLastYear() {
        updateCharts(LocalDateTime.now().minusYears(1), LocalDateTime.now());
    }


    /**
     * Filter der Charts → Letzten 5 Jahre
     */
    @FXML
    private void filterLastFiveYears() {
        updateCharts(LocalDateTime.now().minusYears(5), LocalDateTime.now());
    }

    /**
     * Filter der Charts → Gesamter Zeitraum
     */
    @FXML
    private void filterAll() {
        updateCharts(LocalDateTime.of(1970, 1, 1, 0, 0, 0),
                LocalDateTime.now());
    }


    /**
     * Wechselt zur Detailansicht und übergibt die Größe, sofern verändert
     */
    @FXML
    private void switchToDetail() {
        double width = SceneManager.getInstance().getCurrentWidth();
        double height = SceneManager.getInstance().getCurrentHeight();

        if (width == AppSettings.DEFAULT_WIDTH && height == AppSettings.DEFAULT_HEIGTH)
            SceneManager.getInstance().setScene(View.DETAIL);

        else
            SceneManager.getInstance().setScene(View.DETAIL, width, height);
    }
}