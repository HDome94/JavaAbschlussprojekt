package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.Observable;
import javafx.beans.property.*;

import java.io.Serializable;

/**
 * Model Klasse der Batterie
 */
public class Battery implements Serializable {

    //region Konstanten

    //endregion


    private final StringProperty brand;
    private final StringProperty model;
    private final DoubleProperty capacity;
    //region Attribute
    private LongProperty id;
    private DoubleProperty charge;

    private boolean isBatchUpdating = false;
    //endregion


    //region Konstruktoren
    public Battery(String brand, String model, double capacity) {
        this.id = new SimpleLongProperty();
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.capacity = new SimpleDoubleProperty(capacity);
        this.charge = new SimpleDoubleProperty();
    }

    public Battery(long id, String brand, String model, double capacity, double charge) {
        this(brand, model, capacity);
        this.id = new SimpleLongProperty(id);
        this.charge = new SimpleDoubleProperty(charge);
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

    public String getBrand() {
        return brand.get();
    }

    public void setBrand(String brand) {
        this.brand.set(brand);
    }

    public StringProperty brandProperty() {
        return brand;
    }

    public String getModel() {
        return model.get();
    }

    public void setModel(String model) {
        this.model.set(model);
    }

    public StringProperty modelProperty() {
        return model;
    }

    public double getCapacity() {
        return capacity.get();
    }

    public void setCapacity(double capacity) {
        this.capacity.set(capacity);
    }

    public DoubleProperty capacityProperty() {
        return capacity;
    }

    public double getCharge() {
        return charge.get();
    }

    public void setCharge(double chargeLevel) {
        this.charge.set(chargeLevel);
    }

    public DoubleProperty chargeProperty() {
        return charge;
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
        return "Battery{" +
                "brand=" + brand +
                ", model=" + model +
                ", capacity=" + capacity +
                ", id=" + id +
                ", charge=" + charge +
                ", isBatchUpdating=" + isBatchUpdating +
                '}';
    }

    public static Observable[] batteryObservables(Battery battery) {
        return new Observable[]{
                battery.brandProperty(), battery.modelProperty(), battery.capacityProperty(),
                battery.chargeProperty()
        };
    }

    //endregion
}
