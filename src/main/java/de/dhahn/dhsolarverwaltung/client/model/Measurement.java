package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.Observable;
import javafx.beans.property.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model Klasse des Messwertes
 */
public class Measurement implements Serializable {

    //region Konstanten

    //endregion


    private final DoubleProperty watt;
    private final BooleanProperty isProduced;
    //region Attribute
    private LongProperty id;
    private LocalDateTime datetime;
    //endregion


    //region Konstruktoren
    public Measurement(LocalDateTime datetime, double watt, boolean isProduced) {
        this.id = new SimpleLongProperty();
        this.datetime = datetime;
        this.watt = new SimpleDoubleProperty(watt);
        this.isProduced = new SimpleBooleanProperty(isProduced);
    }

    public Measurement(long id, LocalDateTime datetime, double watt, boolean isProduced) {
        this(datetime, watt, isProduced);
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

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public double getWatt() {
        return watt.get();
    }

    public void setWatt(double watt) {
        this.watt.set(watt);
    }

    public DoubleProperty wattProperty() {
        return watt;
    }

    public boolean isProduced() {
        return isProduced.get();
    }

    public void setProduced(boolean isProduced) {
        this.isProduced.set(isProduced);
    }

    public BooleanProperty isProducedProperty() {
        return isProduced;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "watt=" + watt +
                ", isProduced=" + isProduced +
                ", id=" + id +
                ", datetime=" + datetime +
                '}';
    }

    public static Observable[] measurementsObservable(Measurement solarMeasurement) {
        return new Observable[]{
                solarMeasurement.wattProperty(), solarMeasurement.isProducedProperty()
        };
    }
    //endregion
}
