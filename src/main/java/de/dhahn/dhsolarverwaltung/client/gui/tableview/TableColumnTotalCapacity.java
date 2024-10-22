package de.dhahn.dhsolarverwaltung.client.gui.tableview;

import de.dhahn.dhsolarverwaltung.client.model.Battery;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.scene.control.TableCell;

/**
 * Diese Klasse repräsentiert eine angepasste TableCell die, die gesamt Kapazität der Speicher des Systems anzeigt
 */
public class TableColumnTotalCapacity extends TableCell<SolarSystem, Double> {

    //region Konstanten

    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    //endregion


    //region Methoden
    @Override
    protected void updateItem(Double item, boolean empty) {

        if (empty || getTableView().getItems() == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        SolarSystem solarSystem = getTableView().getItems().get(getIndex());

        double totalCapacity = 0;
        for (Battery battery : solarSystem.getBatteries())
            totalCapacity += battery.getCapacity();

        setText(String.format(AppText.TEMPLATE_WATT, totalCapacity));

    }
    //endregion
}
