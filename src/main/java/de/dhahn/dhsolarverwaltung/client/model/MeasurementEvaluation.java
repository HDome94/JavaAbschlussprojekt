package de.dhahn.dhsolarverwaltung.client.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Model Klasse der Messwert Auswertungen (Produktion Verbrauch) für die Charts
 */
public class MeasurementEvaluation {

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
    //endregion


    //region Konstruktoren

    public MeasurementEvaluation(long systemID, boolean isProduced, LocalDateTime dateTime, double total_watt, double avg_watt, double max_watt, double min_watt) {
        SystemID = new SimpleLongProperty(systemID);
        this.isProduced = new SimpleBooleanProperty(isProduced);
        this.dateTime = dateTime;
        this.total_watt = new SimpleDoubleProperty(total_watt);
        this.avg_watt = new SimpleDoubleProperty(avg_watt);
        this.max_watt = new SimpleDoubleProperty(max_watt);
        this.min_watt = new SimpleDoubleProperty(min_watt);
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


    @Override
    public String toString() {
        return "MeasurementEvaluation{" +
                "SystemID=" + SystemID +
                ", isProduced=" + isProduced +
                ", dateTime=" + dateTime +
                ", total_watt=" + total_watt +
                ", avg_watt=" + avg_watt +
                ", max_watt=" + max_watt +
                ", min_watt=" + min_watt +
                '}';
    }
//endregion
}
