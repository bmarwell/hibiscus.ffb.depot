#!/bin/bash
set -euo pipefail

BASEDIR="$(pwd)"
TARGETDIR="${BASEDIR}/target"
HIBISCUSRELEASE="2.6"

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
    rm -fr "${TARGETDIR}/jameica-meta"
}

check_target_dir() {
    if  [ ! -e "${BASEDIR}/pom.xml" ]; then
        echo "Konnte BASEDIR nicht bestimmen [${BASEDIR}]." >&2
        exit 1
    fi
    if [ ! -e "${TARGETDIR}" ]; then
        mkdir -p "${TARGETDIR}"
    fi
    if [ ! -e "${TARGETDIR}" ]; then
        echo "Konnte TARGETDIR nicht bestimmen [${TARGETDIR}]." >&2
        exit 1
    fi

}

install_jameica() {
    # Gibt leider kein Buildscript :-(
    # git clone "https://github.com/willuhn/jameica.git" "${JAMEICADIR}"
    git clone https://github.com/bmhm/jameica-meta ${TARGETDIR}/jameica-meta
    cd "${TARGETDIR}/jameica-meta"
    git submodule init
    git submodule update
    mvn install
    cd ..
    rm -fr "${TARGETDIR}/jameica-meta"
}

install_hibiscus() {
    # Gibt leider kein Buildscript :-(
    # git clone "https://github.com/willuhn/hibiscus.git" "${HIBISCUSDIR}"
    wget -O "$TARGETDIR/hibiscus-$HIBISCUSRELEASE.zip" "$HIBISCUSLIB"
    wget -O "$TARGETDIR/hibiscus-$HIBISCUSRELEASE.src.zip" "$HIBISCUSSRC"
    cd "${TARGETDIR}"
    unzip "hibiscus-$HIBISCUSRELEASE.zip"
    mvn install:install-file -Dfile=hibiscus/hibiscus.jar -DgroupId=de.willuhn.jameica \
            -DartifactId=jameica-hbci -Dversion=$HIBISCUSRELEASE -Dpackaging=jar \
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
