Anforderungen – Pals & Palms (Tomodachi Life-ähnliches Spiel mit ähnlichem Design)
Funktionale Anforderungen
1. Spielwelt & Umgebung
•	FA-01 Das Spiel besitzt eine Insel mit mehreren betretbaren Räumen (z.B. Park, Einkaufszentrum, Badezimmer, Wohnung)
•	FA-02 Das Bad hat eine begrenzte Kapazität – nur ein Bewohner kann gleichzeitig das Badezimmer betreten
•	FA-03 Es gibt einen gemeinsamen Kühlschrank mit einem Lebensmittelvorrat, der von Bewohnern genutzt werden kann
•	FA-04 Die Spielzeit läuft kontinuierlich ab und ist in Tageszeiten (Morgen, Mittag, Abend, Nacht) unterteilt
•	FA-05 Die Tageszeit beeinflusst das Verhalten der Bewohner (z.B. schlafen nachts)
2. Bewohner
•	FA-06 Es gibt mehrere Bewohner, die sich gleichzeitig und unabhängig voneinander auf der Insel bewegen
•	FA-07 Jeder Bewohner besitzt Statuswerte: Hunger, Müdigkeit, Stimmung und Hygiene
•	FA-08 Statuswerte verändern sich automatisch über Zeit (z.B. Hunger sinkt kontinuierlich)
•	FA-09 Bewohner handeln autonom, um ihre Bedürfnisse zu erfüllen (z.B. geht bei Hunger selbstständig zum Kühlschrank)
•	FA-10 Jeder Bewohner besitzt ein eigenes Inventar, in dem Items gespeichert werden können
•	FA-33 Das Spiel startet mit zwei vordefinierten Bewohnerinnen: Nina (kurze blonde Haare, blaue Augen, helle Haut) und Victoria (lange braune Haare, blaue Augen, helle Haut) 
•	FA-34 Nina und Victoria kennen sich zu Spielbeginn nicht – ihr Beziehungswert startet bei null 
•	FA-35 Der Spieler kann eigene Bewohner erstellen, indem er Name, Geschlecht, Haarfarbe, Haarlänge, Augenfarbe und Hautfarbe festlegt 
•	FA-36 Die Insel hat eine maximale Kapazität von 4 Bewohnern gleichzeitig
3. Interaktionen zwischen Bewohnern
•	FA-11 Befinden sich zwei Bewohner im selben Raum, können sie miteinander interagieren
•	FA-12 Interaktionen umfassen: Gespräch führen, streiten, Geschenke geben
•	FA-13 Interaktionen beeinflussen den Beziehungswert zwischen zwei Bewohnern
•	FA-14 Ab einem bestimmten Beziehungswert wird eine Freundschaft zwischen zwei Bewohnern geschlossen
•	FA-15 Nach einem Streit meidet ein Bewohner den anderen für eine bestimmte Zeit
4. Events
•	FA-16 Der EventManager erzeugt in unregelmäßigen Abständen zufällige globale Events
•	FA-17 Folgende Events sind vorhanden: Konzert im Park, Regentag, Supermarkt-Lieferung, Geburtstag
•	FA-18 Events beeinflussen das Verhalten aller Bewohner (z.B. strömen alle zum Park bei einem Konzert)
•	FA-19 Events werden über eine gemeinsame Queue an die Bewohner kommuniziert
5. Spieler-Interaktion
•	FA-20 Der Spieler kann jederzeit einem Bewohner ein Item geben
•	FA-21 Der Spieler kann zwei Bewohner manuell zu einer Interaktion zusammenbringen
•	FA-22 Der Spieler kann einen neuen Bewohner auf die Insel einziehen lassen
•	FA-37 Beim ersten Spielstart führt das Spiel den Spieler durch ein Tutorial, das anhand von Nina und Victoria erklärt, wie man zwei Bewohner miteinander bekannt macht 
•	FA-38 Während des Spiels ist jederzeit ein „Menü"-Button sichtbar, der ein Ingame-Menü öffnet 
•	FA-39 Das Ingame-Menü enthält folgende Optionen: „Neuen Bewohner erstellen", „Inventar aufrufen", „Spielregeln anzeigen", „Spielstand manuell speichern", „Zurück zur Insel“ und „Zurück zum Homescreen“
•	FA-40 Die Option „Neuen Bewohner erstellen" ist nur auswählbar, wenn aktuell weniger als 4 Bewohner auf der Insel leben; andernfalls ist sie deaktiviert und zeigt einen Hinweis 
•	FA-41 Die Bewohnererstellung findet in einem separaten Screen statt, in dem der Spieler Name, Geschlecht, Haarfarbe, Haarlänge, Augenfarbe und Hautfarbe des neuen Bewohners auswählt
6. Darstellung
•	FA-24 Das Spiel besitzt eine GUI, die den aktuellen Spielzustand darstellt
•	FA-25 Die GUI zeigt die Positionen der Bewohner, ihren aktuellen Status sowie die Tageszeit an
•	FA-26 Die GUI aktualisiert sich in einem festen Intervall (Refresh-Rate)
•	FA-27 Das Spiel zeigt beim Start einen Homescreen an, bevor das Spiel beginnt
•	FA-28 Der Homescreen zeigt den Spieltitel „IsleMates" in einer dekorativen, farbigen Schriftart an
•	FA-29 Der Homescreen bietet beim ersten Starten zwei Optionen: „Spiel starten" und „Regeln lesen"
•	FA-29,5 Der Homescreen bietet bei den nächsten Spielestarts drei Optionen: „Spiel fortsetzen“, „neues Spiel starten“ und „Regeln lesen“
•	FA-30 Über „Regeln lesen" gelangt der Spieler zu einem Regelwerk-Screen, der die Spielregeln und -mechaniken erklärt
•	FA-31 Der Regelwerk-Screen besitzt einen „Zurück"-Button, der den Spieler zum Homescreen zurücknavigiert
•	FA-32 Über „Spiel starten" wird das Spiel gestartet und der Homescreen wird durch die Spielansicht ersetzt
•	FA-42 Der Bewohnererstellungs-Screen bietet für jedes Merkmal (Geschlecht, Haarfarbe, Haarlänge, Augenfarbe, Hautfarbe) eine Auswahl an vordefinierten Optionen 
•	FA-43 Der Bewohnererstellungs-Screen besitzt einen „Zurück"-Button, der den Spieler ohne Änderungen zum Spielscreen zurückbringt
7. Spielstand & Persistenz
•	FA-44 Das Spiel speichert den Spielstand automatisch in regelmäßigen Abständen, sodass kein Fortschritt verloren geht
•	FA-45 Der Spielstand umfasst: alle Bewohner mit ihrem Aussehen, ihren Statuswerten und Inventar, alle Beziehungswerte, die aktuelle Tageszeit sowie alle laufenden Events
•	FA-46 Beim Spielstart wird geprüft, ob ein gespeicherter Spielstand existiert – ist dies der Fall, wird dieser geladen; andernfalls startet das Spiel neu mit Nina und Victoria
________________________________________
Nicht-funktionale Anforderungen
8. Multithreading & Nebenläufigkeit
•	NFA-01 Das Spiel ist als Multithreading-Anwendung implementiert, in der Nebenläufigkeit essentiell ist
•	NFA-02 Jeder Bewohner läuft in einem eigenen Thread
•	NFA-03 GameClock, EventManager, InputHandler und UI-Render laufen jeweils in separaten Threads
•	NFA-04 Alle Threads laufen echt-parallel und sind nicht sequentiell voneinander abhängig
9. Datenkonsistenz & Synchronisation
•	NFA-05 Auf den Kühlschrank wird ausschließlich synchronisiert zugegriffen, um Race Conditions zu verhindern
•	NFA-07 Das Badezimmer ist durch einen Mutex geschützt – nur ein Bewohner darf es gleichzeitig betreten
•	NFA-08 Beziehungsobjekte zwischen Bewohnern sind durch ReentrantLocks gegen gleichzeitige Schreibzugriffe geschützt
•	NFA-09 Die EventQueue ist als BlockingQueue implementiert (Producer-Consumer-Muster)
•	NFA-10 Der globale Spielzustand (für das Rendering) ist durch ein ReadWriteLock geschützt – mehrere Threads dürfen lesen, aber nur einer schreiben
10. Technologie
•	NFA-11 Das Spiel ist vollständig in Java implementiert
•	NFA-12 Externe Libraries dürfen eingesetzt werden, dürfen aber nicht den Kern der Anwendung darstellen
•	NFA-13 Das Spiel läuft auf einer selbst geschriebenen GUI
11. Wartbarkeit & Erweiterbarkeit
•	NFA-14 Neue Bewohner können zur Laufzeit hinzugefügt werden, ohne das System neu zu starten
12. Performance
•	NFA-16 Die GUI rendert mit einem festen, konfigurierbaren Intervall und blockiert dabei nicht die Spiellogik
•	NFA-17 Das Spiel reagiert auf Spieler-Input in Echtzeit, unabhängig vom aktuellen Spielgeschehen
•	NFA-19 Das automatische Speichern läuft in einem eigenen Thread und blockiert weder die Spiellogik noch den UI-Thread 
•	NFA-20 Das Tutorial wird nur beim ersten Spielstart angezeigt und danach nicht mehr wiederholt, sofern kein Reset erfolgt

________________________________________
Ideen für spätere Spielmechaniken (Vorschläge, nicht Teil der Pflichtanforderungen)
•	Freundschaft & Geschenke: bevorzugte Geschenktypen pro Bewohner, kleine Stimmungs-Boni bei Treffern, seltene „Legendär“-Items mit Beziehungs-Storybits.
•	Kochen & Kühlschrank: einfache Rezepte aus 2–3 Zutaten, gemeinsames Kochen im Apartment erhöht Stimmung und füllt Hunger stärker als Rohkost.
•	Schlaf & Routine: festes Bett, „zu spät auf“-Malus; gelegentliche Traum-Events (kurze Textblasen) mit leichtem Mood-Effekt am nächsten Morgen.
•	Wetter & Tageszeit: Regen reduziert Outdoor-Aktivität und lenkt ins Apartment/Mall; Sonne erhöht Park-Wahrscheinlichkeit; Nacht drosselt Streit-Wahrscheinlichkeit.
•	Konzert & Gruppen-Events: temporärer Sammelpunkt im Park, geteilte Stimmungs-Buffs, optionale Foto-/Erinnerungs-Notiz im Spielverlauf.
•	Eifersucht & Drama: wenn Beziehungen stark asymmetrisch sind, seltene Konflikt-Events mit temporärer „Meidung“ (erweitert FA-15).
•	Dekoration: Möbel/Skins fürs Apartment (nur kosmetisch oder +1 Stimmung), freischaltbar über Spielzeit oder Mini-Ziele.

 
