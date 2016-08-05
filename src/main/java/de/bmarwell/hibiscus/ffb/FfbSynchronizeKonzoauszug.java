/*
 * (c) Copyright 2016 Hbiscus FFB Connector Developers.
 *
 * This file is part of Hbiscus FFB Connector.
 *
 * Hbiscus FFB Connector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Hbiscus FFB Connector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hbiscus FFB Connector.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.bmarwell.hibiscus.ffb;

import de.bmarwell.hibiscus.ffb.mobile.FfbMobileDepotwertRetriever;

import com.google.common.base.Preconditions;

import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

import javax.annotation.Resource;

/**
 * Lädt den Depotwert und erstellt dazu einen Umsatz.
 */
public class FfbSynchronizeKonzoauszug extends SynchronizeJobKontoauszug implements FfbSynchronizeJob {

  private static final I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  @Resource
  private FfbSynchronizeBackend backend = null;

  @Override
  public double getDepotwert(String login, String pin, String depotnummer) {
    Preconditions.checkNotNull(login, "FFB-login ist null.");
    Preconditions.checkNotNull(pin, "FFB-pin ist null.");
    Preconditions.checkNotNull(depotnummer, "FFB-depotnummer ist null.");

    FfbMobileDepotwertRetriever scraper = new FfbMobileDepotwertRetriever(login, pin, depotnummer);
    scraper.synchronize();

    // Logger.debug("Depotwert: [" + depotwert + "].");

    // TODO: Depowert ins konto setzen + als Umsatz.
    return scraper.depotwert();
  }



}
