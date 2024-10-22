package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.Observable;
import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Model Klasse für Strom Ein- und Verkäufe
 */
public class ElectricitySale {

    //region Konstanten

    //endregion


    //region Attribute
    LongProperty id;
    LocalDateTime dateTime;
    DoubleProperty watt;
    BooleanProperty isPurchased;
    DoubleProperty pricePerKW;
    //endregion


    //region Konstruktoren
    public ElectricitySale(LocalDateTime datetime, double watt, boolean isPurchased, double pricePerKW) {
        this.id = new SimpleLongProperty();
        this.dateTime = datetime;
        this.watt = new SimpleDoubleProperty(watt);
        this.isPurchased = new SimpleBooleanProperty(isPurchased);
        this.pricePerKW = new SimpleDoubleProperty(pricePerKW);
    }

    public ElectricitySale(long id, LocalDateTime datetime, double watt, boolean isPurchased, double pricePerKW) {
        this(datetime, watt, isPurchased, pricePerKW);
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public boolean isIsPurchased() {
        return isPurchased.get();
    }

    public void setIsPurchased(boolean isPurchased) {
        this.isPurchased.set(isPurchased);
    }

    public BooleanProperty isPurchasedProperty() {
        return isPurchased;
    }

    public double getPricePerKW() {
        return pricePerKW.get();
    }

    public void setPricePerKW(double pricePerKW) {
        this.pricePerKW.set(pricePerKW);
    }

    public DoubleProperty pricePerKWProperty() {
        return pricePerKW;
    }

    @Override
    public String toString() {
        return "ElectricitySale{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", watt=" + watt +
                ", isPurchased=" + isPurchased +
                ", pricePerKW=" + pricePerKW +
                '}';
    }

    public static Observable[] electricitySalesObservable(ElectricitySale electricitySale) {
        return new Observable[]{
                electricitySale.wattProperty(), electricitySale.isPurchasedProperty(),
                electricitySale.pricePerKWProperty()
        };
    }
    //endregion
}
