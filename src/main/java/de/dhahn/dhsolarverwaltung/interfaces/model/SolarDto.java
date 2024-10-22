package de.dhahn.dhsolarverwaltung.interfaces.model;

import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;

import java.io.Serializable;

/**
 * Model Klasse zur Übertragung (Client / API) des Solarpanels
 */
public class SolarDto implements Serializable {

    //region Konstanten

    //endregion


    //region Attribute
    private final long id;
    private final String brand;
    private final String model;
    private final int maximumWatt;
    //endregion


    //region Konstruktoren

    public SolarDto(SolarPanel panel) {
        this.id = panel.getId();
        this.brand = panel.getBrand();
        this.model = panel.getModel();
        this.maximumWatt = panel.getMaximumWatt();
    }

    //endregion


    //region Methoden

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getMaximumWatt() {
        return maximumWatt;
    }

    //endregion
}
