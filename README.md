# hibiscus.ffb.depot
Hibiscus Connector for FFB (FIL Fondsbank).

## Beschreibung
Der Hibiscus-Connector für FFB-Depots (FIL Fondsbank, ehemals Frankfurter Fondsbank) stellt eine Möglichkeit da, in der Software [Hibiskus](https://github.com/willuhn/hibiscus) ein FFB-Depotkonto zu synchronisieren. Es müssen keine Klimmzüge wie "mark as offline" oder ähnliches gemacht werden. Lediglich die [BIC oder Bankleitzahl](https://www.sparkasse.de/service/sepa.html] müssen eingetragen sein.

 * *BIC* -- FFBKDEFFTHK
 * *Bankleitzahl* -- 5002 1120
 
 
## Status des Projekts
Derzeit funktioniert der Scraper, der bald in ein eigenes Projekt namens [FFB Depot Client](https://github.com/bmhm/ffb.depot.client/) ausgelagert wird. Dort wird das Protokoll auch beschrieben sein. Es wird das JSON-basierte Protokoll von Fidelity benutzt.
Die Synchronisierung innerhalb von Hibiskus klappt mangels PIN-Abfrage noch nicht.


## Protokoll
siehe [FFB Depot Client/README.md](https://github.com/bmhm/ffb.depot.client/blob/master/README.md).
