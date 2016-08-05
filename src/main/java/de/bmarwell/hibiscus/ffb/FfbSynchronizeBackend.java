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

import de.willuhn.annotation.Lifecycle;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.synchronize.AbstractSynchronizeBackend;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * Synchronisiert Daten von FFB-Konten.
 */
@Lifecycle(Lifecycle.Type.CONTEXT)
public class FfbSynchronizeBackend extends AbstractSynchronizeBackend<FfbSynchronizeJobProvider> {

  private static final I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  public static final String PROP_PASSWORT = "Passwort";

  @Override
  public String getName() {
    return i18n.tr("FIL Fondsbank (FFB)");
  }

  @Override
  public boolean supports(Class<? extends SynchronizeJob> type, Konto konto) {
    /* Es wird geprüft, ob das Konto null oder disabled ist. */
    boolean superSupports = super.supports(type, konto);
    if (!superSupports) {
      return false;
    }

    /* Konto ist bei Bic oder Ktr valide. */
    try {
      if (konto.getBLZ().equals("50021120")
          || konto.getBic().equals("FFBKDEFFTHK")) {
        return true;
      }
    } catch (RemoteException re) {
      Logger.error("Kontodaten konnten nicht ermittelt werden - Prüfung auf FFB-Konto nicht möglich.", re);
    }

    /* Im Zweifel kein Support */
    return false;
  }

  @Override
  protected Class<FfbSynchronizeJobProvider> getJobProviderInterface() {
    return FfbSynchronizeJobProvider.class;
  }

  @Override
  protected JobGroup createJobGroup(Konto konto) {
    return new FfbJobGroup(konto);
  }

  protected class FfbJobGroup extends JobGroup {

    protected FfbJobGroup(Konto konto) {
      super(konto);
    }

    @Override
    protected void sync() throws Exception {
      ProgressMonitor monitor = worker.getMonitor();
      String kn = this.getKonto().getLongName();

      int step = 100 / worker.getSynchronization().size();

      this.checkInterrupted();

      monitor.log(" ");
      monitor.log(i18n.tr("Synchronisiere FFB-Konto: {0}", kn));
      String pin = Application.getCallback()
          .askPassword("Bitte geben Sie Ihre PIN für den FFB-Zugang " + this.getKonto().getKundennummer() + " ein.");

      Logger.info("processing jobs");
      for (SynchronizeJob job : this.jobs) {
        this.checkInterrupted();

        FfbSynchronizeJob ffbjob = (FfbSynchronizeJob) job;
        double depotwert = ffbjob.getDepotwert(
            this.getKonto().getKundennummer(),
            pin,
            this.getKonto().getKontonummer());
        Umsatz newUmsatz = (Umsatz) Settings.getDBService().createObject(Umsatz.class, null);
        Date umsatzdatum = new Date();
        newUmsatz.setKonto(this.getKonto());
        newUmsatz.setSaldo(depotwert);
        newUmsatz.setDatum(umsatzdatum);
        newUmsatz.setValuta(umsatzdatum);
        newUmsatz.setBetrag(0.00);
        newUmsatz.setArt("Depotwert");
        newUmsatz.store();
        this.getKonto().setSaldo(depotwert);

        monitor.addPercentComplete(step);
      }
    }

  }
}
