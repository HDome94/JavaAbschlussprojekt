package de.dhahn.dhsolarverwaltung.client.gui.tableview;

import de.dhahn.dhsolarverwaltung.client.model.SolarPanel;
import de.dhahn.dhsolarverwaltung.client.model.SolarSystem;
import de.dhahn.dhsolarverwaltung.client.settings.AppText;
import javafx.scene.control.TableCell;

/**
 * Diese Klasse repräsentiert eine angepasste TableCell die, die gesamtleistung des Solarsystems anzeigt
 */
public class TableColumnMaxPerfomance extends TableCell<SolarSystem, Double> {

    //region Konstanten

    //endregion


    //region Attribute

    //endregion


    //region Konstruktoren

    //endregion


    //region Methoden
    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getTableView().getItems() == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        SolarSystem solarSystem = getTableView().getItems().get(getIndex());

        double totalPerfomance = 0;
        for (SolarPanel panel : solarSystem.getSolarPanels())
            totalPerfomance += panel.getMaximumWatt();

        setText(String.format(AppText.TEMPLATE_WATT, totalPerfomance));

    }
    //endregion
}
