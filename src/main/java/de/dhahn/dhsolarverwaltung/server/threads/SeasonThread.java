package de.dhahn.dhsolarverwaltung.server.threads;

import de.dhahn.dhsolarverwaltung.Main;
import de.dhahn.dhsolarverwaltung.server.logic.DayTimeSeasonsSwitcher;
import de.dhahn.dhsolarverwaltung.server.settings.ServerText;

/**
 * Diese Thread-Klasse ist für den Jahreszeiten Simulation zuständig
 */
public class SeasonThread extends Thread {

    //region Konstanten

    //endregion


    //region Attribute
    private final int sleepDuration;
    //endregion


    //region Konstruktoren

    public SeasonThread(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }
    //endregion


    //region Methoden

    @Override
    public void run() {
        while (Main.isRun()) {
            try {
                DayTimeSeasonsSwitcher.getInstance().switchSeason();
                System.out.println(ServerText.SEASON_CHANGED);
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                System.out.println(ServerText.ERROR_SEASON_NOT_CHANGED);
            }
        }
    }
    //endregion
}
