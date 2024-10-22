# Abschlussprojekt Java Kurs

Dies ist mein Abschlussprojekt des absolvierten Java-Kurses. Das Projekt ist eine nach dem **MVC-Architekturprinzip** aufgebaute JavaFX-Anwendung. Als Datenbank kommt **MariaDB** (über XAMPP) zum Einsatz. Die Datenbankzugriffe wurden nach dem **Observable Pattern** umgesetzt.

## Solarverwaltung mit Fake API (RMI)

Das Projekt besteht aus zwei Modulen:
- **Client**
- **API-Server** (Fake API zur Generierung von Messwerten)

Die Faktoren für die Berechnungen sind rein fiktiv und dienen der Simulation.

In der **Test Main**-Klasse können angepasste Test-Anlagen erstellt und rückwirkend Messwerte angelegt werden, um die Charts mit Daten zu füllen.

**Hinweis:** Seriennummern und das Verschieben der Komponenten wurden aus Einfachheitsgründen ausgelassen. In einem realen Programm wären diese Funktionen jedoch enthalten.

### Bekannte Probleme:
- Bei den Charts kann es vorkommen, dass sich die X-Achse nicht korrekt aktualisiert. Ein einfaches **Resizing des Fensters** behebt dieses Problem. Eine 100% verlässliche Lösung für das automatische Aktualisieren der Achsen (z.B. durch `autosize()` oder andere Methoden) habe ich bisher nicht gefunden.
