package de.dhahn.dhsolarverwaltung.server.threads;

import de.dhahn.dhsolarverwaltung.Main;
import de.dhahn.dhsolarverwaltung.server.logic.DayTimeSeasonsSwitcher;
import de.dhahn.dhsolarverwaltung.server.settings.ServerText;

/**
 * Diese Thread-Klasse ist für die Tageszeit Simulation zuständig
 */
public class DayTimeThread extends Thread {

    //region Konstanten

    //endregion


    //region Attribute
    private final int sleepDuration;
    //endregion


    //region Konstruktoren

    public DayTimeThread(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    //endregion


    //region Methoden

    @Override
    public void run() {
        while (Main.isRun()) {
            try {
                DayTimeSeasonsSwitcher.getInstance().switchDayTime();
                System.out.println(ServerText.DAY_TIME_CHANGED);
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                System.out.println(ServerText.ERROR_DAY_TIME_NOT_CHANGED);
            }
        }
    }

    //endregion
}
