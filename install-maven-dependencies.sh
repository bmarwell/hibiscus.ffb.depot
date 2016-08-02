#!/bin/bash
set -euo pipefail

BASEDIR="$(pwd)"
TARGETDIR="${BASEDIR}/target"
JAMEICARELEASE="2.6"
HIBISCUSRELEASE="2.6"

JAMEICALIB="https://www.willuhn.de/products/jameica/releases/$JAMEICARELEASE/jameica/jameica.zip"
JAMEICASRC="https://www.willuhn.de/products/jameica/releases/$JAMEICARELEASE/jameica/jameica.src.zip"
HIBISCUSLIB="https://www.willuhn.de/products/hibiscus/releases/$HIBISCUSRELEASE/hibiscus.zip"
HIBISCUSSRC="https://www.willuhn.de/products/hibiscus/releases/$HIBISCUSRELEASE/hibiscus.src.zip"

cleanup() {
    # aufraeumen
    if [ -d  "${TARGETDIR}/hibiscus" ]; then
        rm -r "${TARGETDIR}/hibiscus"
    fi
    if [ -f  "${TARGETDIR}/hibiscus-$HIBISCUSRELEASE.zip" ]; then
        rm -r "${TARGETDIR}/hibiscus-$HIBISCUSRELEASE.zip"
    fi
    if [ -f  "${TARGETDIR}/hibiscus-$HIBISCUSRELEASE.src.zip" ]; then
        rm -r "${TARGETDIR}/hibiscus-$HIBISCUSRELEASE.src.zip"
    fi
    if [ -d  "${TARGETDIR}/jameica" ]; then
        rm -r "${TARGETDIR}/jameica"
    fi
    if [ -f  "${TARGETDIR}/jameica-$JAMEICARELEASE.zip" ]; then
        rm -r "${TARGETDIR}/jameica-$JAMEICARELEASE.zip"
    fi
    if [ -f  "${TARGETDIR}/jameica-$JAMEICARELEASE.src.zip" ]; then
        rm -r "${TARGETDIR}/jameica-$JAMEICARELEASE.src.zip"
    fi
}

check_target_dir() {
    if  [ ! -e "${BASEDIR}/pom.xml" ]; then
        echo "Konnte BASEDIR nicht bestimmen [${BASEDIR}]." >&2
        exit 1
    fi
    if [ ! -e "${TARGETDIR}" ]; then
        echo "Konnte TARGETDIR nicht bestimmen [${TARGETDIR}]." >&2
        exit 1
    fi

}

install_jameica() {
    # Gibt leider kein Buildscript :-(
    # git clone "https://github.com/willuhn/jameica.git" "${JAMEICADIR}"
    cd "${TARGETDIR}"
    wget -O "$TARGETDIR/jameica-$JAMEICARELEASE.zip" "$JAMEICALIB"
    wget -O "$TARGETDIR/jameica-$JAMEICARELEASE.src.zip" "$JAMEICASRC"
    unzip "jameica-$JAMEICARELEASE.zip"
    mvn install:install-file -Dfile=jameica/jameica.jar -DgroupId=de.willuhn.jameica \
            -DartifactId=core -Dversion=$JAMEICARELEASE -Dpackaging=jar \
            -Dsources=jameica-$JAMEICARELEASE.src.zip
    # no sources :(
    mvn install:install-file -Dfile=jameica/lib/de_willuhn_util/de_willuhn_util.jar \
        -DartifactId=util -DgroupId=de.willuhn.jameica -Dversion=$JAMEICARELEASE -Dpackaging=jar
}

install_hibiscus() {
    # Gibt leider kein Buildscript :-(
    # git clone "https://github.com/willuhn/hibiscus.git" "${HIBISCUSDIR}"
    wget -O "$TARGETDIR/hibiscus-$HIBISCUSRELEASE.zip" "$HIBISCUSLIB"
    wget -O "$TARGETDIR/hibiscus-$HIBISCUSRELEASE.src.zip" "$HIBISCUSSRC"
    cd "${TARGETDIR}"
    unzip "hibiscus-$HIBISCUSRELEASE.zip"
    mvn install:install-file -Dfile=hibiscus/hibiscus.jar -DgroupId=de.willuhn.jameica \
            -DartifactId=hbci -Dversion=$HIBISCUSRELEASE -Dpackaging=jar \
            -Dsources=hibiscus-$HIBISCUSRELEASE.src.zip
}

check_target_dir
cleanup

echo "Installing Jameica..." >&2
sleep 2
install_jameica

echo "Installing Hibiscus..." >&2
sleep 2
install_hibiscus

echo "Cleanup" >&2
cleanup
echo "All done!" >&2
sleep 1

exit 0
