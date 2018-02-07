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
    rm -fr "${TARGETDIR}/slf4j-jameica"
}

check_target_dir() {
  mkdir -p $HOME/.m2/repository || true

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
  FOUND_META=$(find $HOME/.m2/repository/ -name "jameica-meta*" | wc -l)
  if [ $FOUND_META -gt 0 ]; then
    echo "jameica-meta found, not reinstalling." >&2
    return
  fi

  # Gibt leider kein Buildscript :-(
  # git clone "https://github.com/willuhn/jameica.git" "${JAMEICADIR}"
  echo "jameica-meta NOT found, now installing." >&2
  git clone https://github.com/bmhm/jameica-meta ${TARGETDIR}/jameica-meta
  cd "${TARGETDIR}/jameica-meta"
  git submodule init
  git submodule update
  mvn -B install
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
    mvn -B install:install-file -Dfile=hibiscus/hibiscus.jar -DgroupId=de.willuhn.jameica \
            -DartifactId=jameica-hbci -Dversion=$HIBISCUSRELEASE -Dpackaging=jar \
            -Dsources=hibiscus-$HIBISCUSRELEASE.src.zip
}

install_slf4j-jameica() {
  FOUND_SJ=$(find $HOME/.m2/repository/ -name "slf4j-jameica" | wc -l)
  if [ $FOUND_SJ -gt 0 ]; then
    echo "slf4j-jameica found, not reinstalling." >&2
    return
  fi

  echo "slf4j-jameica NOT found, no reinstalling." >&2
  git clone git@github.com:bmhm/slf4j-jameica.git ${TARGETDIR}/slf4j-jameica
  cd "${TARGETDIR}/slf4j-jameica"
  mvn -B -T2 install
}

check_target_dir
cleanup

echo "Installing Jameica..." >&2
sleep 2
install_jameica

echo "Installing Hibiscus..." >&2
sleep 2
install_hibiscus

echo "Installing slf4j-jameica" >&2
install_slf4j-jameica

echo "Cleanup" >&2
cleanup
echo "All done!" >&2
sleep 1

exit 0
