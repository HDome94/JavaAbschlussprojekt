package de.dhahn.dhsolarverwaltung.interfaces.model;

import de.dhahn.dhsolarverwaltung.client.model.Direction;
import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model Klasse zur Übertragung (Client / API) der Solar Anlage
 */
public class SystemDto implements Serializable {

    //region Konstanten

    //endregion


    //region Attribute
    private final long id;
    private final List<SolarDto> panels;
    private final Direction direction;
    //endregion


    //region Konstruktoren

    public SystemDto(SolarSystem system) {
        this.id = system.getId();
        this.direction = system.getDirection();
        this.panels = new ArrayList<>();
        for (SolarPanel panel : system.getSolarPanels())
            panels.add(new SolarDto(panel));
    }

    //endregion


    //region Methoden

    public long getId() {
        return id;
    }

    public List<SolarDto> getPanels() {
        return panels;
    }

    public Direction getDirection() {
        return direction;
    }

    //endregion
}
