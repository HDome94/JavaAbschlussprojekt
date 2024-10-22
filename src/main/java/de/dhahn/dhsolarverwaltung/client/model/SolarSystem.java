package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.Observable;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model Klasse der Solar Anlage
 */
public class SolarSystem implements Serializable {

    //region Konstanten

    //endregion


    //region Attribute
    private final LongProperty id;
    private final StringProperty street;
    private final StringProperty houseNr;
    private final StringProperty postalCode;
    private final StringProperty city;
    private ObservableList<SolarPanel> solarPanels;
    private ObservableList<Battery> batteries;
    private Direction direction;
    private ObservableList<Measurement> solarMeasurements;
    private ObservableList<Measurement> consumptionMeasurements;
    private ObservableList<ElectricitySale> electricitySales;
    private ObservableList<ElectricitySale> electricityPurchases;

    private boolean isBatchUpdating = false;


    //endregion


    //region Konstruktoren

    public SolarSystem() {
        id = new SimpleLongProperty();
        street = new SimpleStringProperty();
        houseNr = new SimpleStringProperty();
        postalCode = new SimpleStringProperty();
        city = new SimpleStringProperty();

        solarPanels =
                FXCollections.observableList(new ArrayList<>(), SolarPanel::solarPanelObservables);
        batteries =
                FXCollections.observableList(new ArrayList<>(), Battery::batteryObservables);
        solarMeasurements =
                FXCollections.observableList(new ArrayList<>(), Measurement::measurementsObservable);
        consumptionMeasurements =
                FXCollections.observableList(new ArrayList<>(), Measurement::measurementsObservable);
        electricitySales =
                FXCollections.observableList(new ArrayList<>(), ElectricitySale::electricitySalesObservable);
        electricityPurchases =
                FXCollections.observableList(new ArrayList<>(), ElectricitySale::electricitySalesObservable);
    }

    //endregion


    //region Methoden

    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public StringProperty streetProperty() {
        return street;
    }

    public String getHouseNr() {
        return houseNr.get();
    }

    public void setHouseNr(String houseNr) {
        this.houseNr.set(houseNr);
    }

    public StringProperty houseNrProperty() {
        return houseNr;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    public ObservableList<SolarPanel> getSolarPanels() {
        return solarPanels;
    }

    public void setSolarPanels(List<SolarPanel> solarPanels) {
        this.solarPanels =
                FXCollections.observableList(solarPanels, SolarPanel::solarPanelObservables);
    }

    public ObservableList<Battery> getBatteries() {
        return batteries;
    }

    public void setBatteries(List<Battery> batteries) {
        this.batteries =
                FXCollections.observableList(batteries, Battery::batteryObservables);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ObservableList<Measurement> getSolarMeasurements() {
        return solarMeasurements;
    }

    public void setSolarMeasurements(List<Measurement> solarMeasurements) {
        this.solarMeasurements =
                FXCollections.observableList(solarMeasurements, Measurement::measurementsObservable);
    }

    public ObservableList<Measurement> getConsumptionMeasurements() {
        return consumptionMeasurements;
    }

    public void setConsumptionMeasurements(List<Measurement> consumptionMeasurements) {
        this.consumptionMeasurements =
                FXCollections.observableList(consumptionMeasurements, Measurement::measurementsObservable);
    }

    public ObservableList<ElectricitySale> getElectricitySales() {
        return electricitySales;
    }

    public void setElectricitySales(List<ElectricitySale> electricitySales) {
        this.electricitySales =
                FXCollections.observableList(electricitySales, ElectricitySale::electricitySalesObservable);
    }

    public ObservableList<ElectricitySale> getElectricityPurchases() {
        return electricityPurchases;
    }

    public void setElectricityPurchases(List<ElectricitySale> electricityPurchases) {
        this.electricityPurchases =
                FXCollections.observableList(electricityPurchases, ElectricitySale::electricitySalesObservable);
    }

    public void startBatchUpdate() {
        isBatchUpdating = true;
    }

    public void finishBatchUpdate() {
        isBatchUpdating = false;
    }

    public boolean isBatchUpdating() {
        return isBatchUpdating;
    }

    @Override
    public String toString() {
        return "SolarSystem{" +
                "id=" + id +
                ", street=" + street +
                ", houseNr=" + houseNr +
                ", postalCode=" + postalCode +
                ", city=" + city +
                ", solarPanels=" + solarPanels +
                ", batteries=" + batteries +
                ", direction=" + direction +
                ", solarMeasurements=" + solarMeasurements +
                ", consumptionMeasurements=" + consumptionMeasurements +
                ", electricitySales=" + electricitySales +
                ", electricityPurchases=" + electricityPurchases +
                ", isBatchUpdating=" + isBatchUpdating +
                '}';
    }

//endregion
}


