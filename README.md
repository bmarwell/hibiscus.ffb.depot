[![license](https://img.shields.io/badge/Licence-GPLv2%2B-brightgreen.svg)]() [![Build Status](https://travis-ci.org/bmhm/hibiscus.ffb.depot.svg?branch=master)](https://travis-ci.org/bmhm/hibiscus.ffb.depot) [![codecov](https://codecov.io/gh/bmhm/hibiscus.ffb.depot/branch/master/graph/badge.svg)](https://codecov.io/gh/bmhm/hibiscus.ffb.depot) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/19baee31526d4429a038962efbf4e395)](https://www.codacy.com/app/bmarwell/hibiscus-ffb-depot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bmhm/hibiscus.ffb.depot&amp;utm_campaign=Badge_Grade) 

# hibiscus.ffb.depot
Hibiscus Connector for FFB (FIL Fondsbank).

## Beschreibung
Der Hibiscus-Connector für FFB-Depots (FIL Fondsbank, ehemals Frankfurter Fondsbank) stellt eine Möglichkeit da, in der Software [Hibiskus](https://github.com/willuhn/hibiscus) ein FFB-Depotkonto zu synchronisieren. Es müssen keine Klimmzüge wie "mark as offline" oder ähnliches gemacht werden. Lediglich die [BIC oder Bankleitzahl](https://www.sparkasse.de/service/sepa.html) müssen eingetragen sein.

 * *BIC* -- FFBKDEFFTHK
 * *Bankleitzahl* -- 5002 1120

## Installation
### Derzeit
Hibiscus erwartet eine spezielle Verzeichnisstruktur. Diese muss derzeit per Hand angelegt werden.
```bash
mvn package
cp target/hibiscus.ffb.depot-*.jar <hibiscusdir>/plugins/hibiscus.ffb.depot/hibiscus.ffb.depot.jar
cp plugin.xml <hibiscusdir>/plugins/hibiscus.ffb.depot/plugin.xml
cp ~/.m2/repository/..../gson-2.6.2.jar  <hibiscusdir>/plugins/hibiscus.ffb.depot/lib/
cp ~/.m2/repository/..../guava-19.0.jar  <hibiscusdir>/plugins/hibiscus.ffb.depot/lib/
```

### Später
Mit dem Maven-Assembly-Plugin wird ein Archiv erstellt (.zip, .tar.bz2 o.ä.), welches alle benötigten .jar-Dateien sowie die plugin.xml enhtält.
```bash
mvn package
cd <hibiscusdir>/plugins/
unzip <mvnprojectpath>/target/hibiscus.ffb.depot.tar.bz2
```

Verzeichnisstruktur:
```bash
$ pwd
~/.config/hibiscus/plugins
$ ls -l hibiscus.ffb.depot/
hibiscus.ffb.depot/lib/gson-2.6.2.jar
hibiscus.ffb.depot/lib/guava-19.0.jar
hibiscus.ffb.depot/lib/licenses.txt
hibiscus.ffb.depot/hibiscus.ffb.depot.jar
hibiscus.ffb.depot/plugin.xml
hibiscus.ffb.depot/license.txt
hibiscus.ffb.depot/info.txt
```


## Status des Projekts
Derzeit funktioniert der Scraper, der bald in ein eigenes Projekt namens [FFB Depot Client](https://github.com/bmhm/ffb.depot.client/) ausgelagert wird. Dort wird das Protokoll auch beschrieben sein. Es wird das JSON-basierte Protokoll von Fidelity benutzt.
Die Synchronisierung innerhalb von Hibiskus klappt mangels PIN-Abfrage noch nicht.


## Protokoll
siehe [FFB Depot Client/README.md](https://github.com/bmhm/ffb.depot.client/blob/master/README.md).
