/*
 *  Copyright 2018 The hibiscus.ffb.depot contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.bmarwell.hibiscus.ffb;

import static java.util.Objects.requireNonNull;

import de.bmarwell.ffb.depot.client.FfbDepotUtils;
import de.bmarwell.ffb.depot.client.FfbMobileClient;
import de.bmarwell.ffb.depot.client.err.FfbClientError;
import de.bmarwell.ffb.depot.client.json.MyFfbResponse;
import de.bmarwell.ffb.depot.client.value.FfbDepotNummer;
import de.bmarwell.ffb.depot.client.value.FfbLoginKennung;
import de.bmarwell.ffb.depot.client.value.FfbPin;

import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;
import java.math.RoundingMode;
import javax.annotation.Resource;

/**
 * Lädt den Depotwert und erstellt dazu einen Umsatz.
 */
public class FfbSynchronizeKonzoauszug extends SynchronizeJobKontoauszug implements FfbSynchronizeJob {

  private static final String LOGIN_ERROR = "Login bei der FFB nicht erfolgreich! Bitte erneut versuchen oder Bug melden.";

  private static final I18N i18n = Application.getPluginLoader().getPlugin(FfbDepotPlugin.class).getResources().getI18N();

  @Resource
  private final FfbSynchronizeBackend backend = null;

  @Override
  public double getDepotwert(final String login, final String pin, final String depotnummer) {
    requireNonNull(login, "FFB-login ist null.");
    requireNonNull(pin, "FFB-pin ist null.");
    requireNonNull(depotnummer, "FFB-depotnummer ist null.");

    final FfbLoginKennung ffblogin = FfbLoginKennung.of(login);
    final FfbPin ffbpin = FfbPin.of(pin);

    try {
      final FfbMobileClient ffbClient = new FfbMobileClient(ffblogin, ffbpin);
      ffbClient.logon();

      if (!ffbClient.isLoggedIn()) {
        throw new IllegalStateException(LOGIN_ERROR);
      }

      final FfbDepotNummer depotNr = FfbDepotNummer.of(depotnummer);
      final MyFfbResponse accountData = ffbClient.fetchAccountData();

      // In den AccountDaten könnten mehere Depots sein. Es soll aber nur ein Depot
      // angegeben werden.
      return FfbDepotUtils.getGesamtBestand(accountData, depotNr).setScale(4, RoundingMode.HALF_UP).doubleValue();
    } catch (final FfbClientError error) {
      Logger.error(LOGIN_ERROR, error);
      throw new IllegalStateException(LOGIN_ERROR);
    }
  }



}
