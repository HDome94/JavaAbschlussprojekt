package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Model Klasse der Auswertungen (Strom Ein- / Verkauf) für die Charts
 */
public class ElectricitySaleEvaluation {

    //region Konstanten

    //endregion

    //region Attribute
    private final LongProperty SystemID;
    private final BooleanProperty isProduced;
    private final LocalDateTime dateTime;
    private final DoubleProperty total_watt;
    private final DoubleProperty avg_watt;
    private final DoubleProperty max_watt;
    private final DoubleProperty min_watt;
    private final DoubleProperty avg_price;
    private final DoubleProperty max_price;
    private final DoubleProperty min_price;
    //endregion


    //region Konstruktoren

    public ElectricitySaleEvaluation(long systemID, boolean isProduced, LocalDateTime dateTime, double total_watt,
                                     double avg_watt, double max_watt, double min_watt, double avg_price,
                                     double max_price, double min_price) {

        SystemID = new SimpleLongProperty(systemID);
        this.isProduced = new SimpleBooleanProperty(isProduced);
        this.dateTime = dateTime;
        this.total_watt = new SimpleDoubleProperty(total_watt);
        this.avg_watt = new SimpleDoubleProperty(avg_watt);
        this.max_watt = new SimpleDoubleProperty(max_watt);
        this.min_watt = new SimpleDoubleProperty(min_watt);
        this.avg_price = new SimpleDoubleProperty(avg_price);
        this.max_price = new SimpleDoubleProperty(max_price);
        this.min_price = new SimpleDoubleProperty(min_price);
    }

    //endregion


    //region Methoden

    public long getSystemID() {
        return SystemID.get();
    }

    public LongProperty systemIDProperty() {
        return SystemID;
    }

    public boolean isIsProduced() {
        return isProduced.get();
    }

    public BooleanProperty isProducedProperty() {
        return isProduced;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getTotal_watt() {
        return total_watt.get();
    }

    public DoubleProperty total_wattProperty() {
        return total_watt;
    }

    public double getAvg_watt() {
        return avg_watt.get();
    }

    public DoubleProperty avg_wattProperty() {
        return avg_watt;
    }

    public double getMax_watt() {
        return max_watt.get();
    }

    public DoubleProperty max_wattProperty() {
        return max_watt;
    }

    public double getMin_watt() {
        return min_watt.get();
    }

    public DoubleProperty min_wattProperty() {
        return min_watt;
    }

    public double getAvg_price() {
        return avg_price.get();
    }

    public DoubleProperty avg_priceProperty() {
        return avg_price;
    }

    public double getMax_price() {
        return max_price.get();
    }

    public DoubleProperty max_priceProperty() {
        return max_price;
    }

    public double getMin_price() {
        return min_price.get();
    }

    public DoubleProperty min_priceProperty() {
        return min_price;
    }

    @Override
    public String toString() {
        return "ElectricitySaleEvaluation{" +
                "SystemID=" + SystemID +
                ", isProduced=" + isProduced +
                ", dateTime=" + dateTime +
                ", total_watt=" + total_watt +
                ", avg_watt=" + avg_watt +
                ", max_watt=" + max_watt +
                ", min_watt=" + min_watt +
                ", avg_price=" + avg_price +
                ", max_price=" + max_price +
                ", min_price=" + min_price +
                '}';
    }

    //endregion
}
