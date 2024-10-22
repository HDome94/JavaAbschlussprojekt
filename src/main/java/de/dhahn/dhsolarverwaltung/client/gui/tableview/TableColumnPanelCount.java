package de.dhahn.dhsolarverwaltung.client.gui.tableview;

import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import javafx.scene.control.TableCell;

/**
 * Diese Klasse repräsentiert eine angepasste TableCell die, die gesamtzahl der Panels des Systems anzeigt
 */
public class TableColumnPanelCount extends TableCell<SolarSystem, Integer> {

    //region Konstanten

    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    //endregion


    //region Methoden
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getTableView().getItems() == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        SolarSystem solarSystem = getTableView().getItems().get(getIndex());
        setText(String.valueOf(solarSystem.getSolarPanels().size()));
    }
    //endregion
}
