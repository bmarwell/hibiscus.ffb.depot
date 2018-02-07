[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Build Status](https://travis-ci.org/bmhm/hibiscus.ffb.depot.svg?branch=master)](https://travis-ci.org/bmhm/hibiscus.ffb.depot) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/19baee31526d4429a038962efbf4e395)](https://www.codacy.com/app/bmarwell/hibiscus-ffb-depot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bmhm/hibiscus.ffb.depot&amp;utm_campaign=Badge_Grade) 

# hibiscus.ffb.depot
Hibiscus Connector for FFB (FIL Fondsbank).

## Beschreibung
Der Hibiscus-Connector für FFB-Depots (FIL Fondsbank, ehemals Frankfurter Fondsbank) stellt eine Möglichkeit da, in der Software [Hibiskus](https://github.com/willuhn/hibiscus) ein FFB-Depotkonto zu synchronisieren. Es müssen keine Klimmzüge wie "mark as offline" oder ähnliches gemacht werden. Lediglich die [BIC oder Bankleitzahl](https://www.sparkasse.de/service/sepa.html) müssen eingetragen sein.

 * *BIC* -- FFBKDEFFTHK
 * *Bankleitzahl* -- 5002 1120

## Installation

### Per Update-URL
Siehe Beschreibung und Update-URL unter: [bmhm.github.io](https://bmhm.github.io/projects/hibiscus/).

Die Update-URL lautet:
```bash
https://bmhm.github.io/repositories/hibiscus/
```


### Aus dem Release
Man entpackt das .zip oder .tar.bz2-File einfach in das Jameica-Plugins-Verzeichnis.
Es fehlt aber dann die Hibiscus-Library [slf4j-jameica](https://github.com/bmhm/slf4j-jameica).


### Aus den Quellen
Mit dem Maven-Assembly-Plugin wird ein Archiv erstellt (.zip, .tar.bz2 o.ä.), welches alle benötigten .jar-Dateien sowie die plugin.xml enhtält.
Es fehlt aber dann die Hibiscus-Library [slf4j-jameica](https://github.com/bmhm/slf4j-jameica).

```bash
mvn package
cd <hibiscusdir>/plugins/
tar xvf <mvnprojectpath>/target/hibiscus.ffb.depot.tar.bz2
```

Verzeichnisstruktur:
```bash
$ tar -tf target/hibiscus.ffb.depot-plugin.tar.bz2 
hibiscus.ffb.depot/plugin.xml
hibiscus.ffb.depot/LICENSE.md
hibiscus.ffb.depot/hibiscus.ffb.depot.jar
hibiscus.ffb.depot/hibiscus.ffb.depot-javadoc.jar
hibiscus.ffb.depot/hibiscus.ffb.depot-sources.jar
hibiscus.ffb.depot/lib/jackson-core-2.9.4.jar
hibiscus.ffb.depot/lib/jackson-annotations-2.9.4.jar
hibiscus.ffb.depot/lib/jackson-databind-2.9.4.jar
hibiscus.ffb.depot/lib/ffb.depot.client-0.2.0.jar
hibiscus.ffb.depot/lib/jackson-datatype-jsr310-2.9.4.jar
hibiscus.ffb.depot/lib/jackson-datatype-jdk8-2.9.4.jar
hibiscus.ffb.depot/lib/error_prone_annotations-2.2.0.jar
hibiscus.ffb.depot/lib/jersey-hk2-2.26.jar
hibiscus.ffb.depot/lib/jersey-common-2.26.jar
hibiscus.ffb.depot/lib/javax.ws.rs-api-2.1.jar
hibiscus.ffb.depot/lib/javax.annotation-api-1.2.jar
hibiscus.ffb.depot/lib/javax.inject-2.5.0-b42.jar
hibiscus.ffb.depot/lib/osgi-resource-locator-1.0.1.jar
hibiscus.ffb.depot/lib/hk2-locator-2.5.0-b42.jar
hibiscus.ffb.depot/lib/aopalliance-repackaged-2.5.0-b42.jar
hibiscus.ffb.depot/lib/hk2-api-2.5.0-b42.jar
hibiscus.ffb.depot/lib/javax.inject-1.jar
hibiscus.ffb.depot/lib/hk2-utils-2.5.0-b42.jar
hibiscus.ffb.depot/lib/javassist-3.22.0-CR2.jar
hibiscus.ffb.depot/lib/jersey-client-2.26.jar
```

## Status des Projekts
Derzeit funktioniert das Projekt. Der FFB-Client ist in ein eigenes Projekt namens [FFB Depot Client](https://github.com/bmhm/ffb.depot.client/) ausgelagert. Dort wird das FFB-HTTP-Protokoll auch beschrieben. Es ist ein JSON-basierte Protokoll von Fidelity.
Die Synchronisierung innerhalb von Hibiskus klappt per PIN-Abfrage.


## Protokoll
siehe [FFB Depot Client/README.md](https://github.com/bmhm/ffb.depot.client/blob/master/README.md).
