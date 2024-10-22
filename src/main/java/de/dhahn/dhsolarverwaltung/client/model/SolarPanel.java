package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.Observable;
import javafx.beans.property.*;

import java.io.Serializable;

/**
 * Model Klasse des Solar Panels
 */
public class SolarPanel implements Serializable {

    //region Konstanten

    //endregion

    //region Attribute
    private LongProperty id;
    private final StringProperty brand;
    private final StringProperty model;
    private final IntegerProperty maximumWatt;

    private boolean isBatchUpdating = false;
    //endregion


    //region Konstruktoren

    public SolarPanel(String brand, String model, int maximumWatt) {
        this.id = new SimpleLongProperty();
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.maximumWatt = new SimpleIntegerProperty(maximumWatt);
    }

    public SolarPanel(long id, String brand, String model, int maximumWatt) {
        this(brand, model, maximumWatt);
        this.id = new SimpleLongProperty(id);
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

    public int getMaximumWatt() {
        return maximumWatt.get();
    }

    public void setMaximumWatt(int maximumWatt) {
        this.maximumWatt.set(maximumWatt);
    }

    public IntegerProperty maximumWattProperty() {
        return maximumWatt;
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
        return "SolarPanel{" +
                "id=" + id +
                ", brand=" + brand +
                ", model=" + model +
                ", maximumWatt=" + maximumWatt +
                ", isBatchUpdating=" + isBatchUpdating +
                '}';
    }

    public static Observable[] solarPanelObservables(SolarPanel panel) {
        return new Observable[]{
                panel.brandProperty(), panel.modelProperty(), panel.maximumWattProperty()};
    }
    //endregion
}
