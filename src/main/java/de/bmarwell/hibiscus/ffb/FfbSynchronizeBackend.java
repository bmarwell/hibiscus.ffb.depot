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

  private static final I18N i18n = Application.getPluginLoader().getPlugin(FfbDepotPlugin.class).getResources().getI18N();
  public static final String PROP_PASSWORT = "Passwort";

  @Override
  public String getName() {
    return i18n.tr("FIL Fondsbank (FFB)");
  }

  @Override
  public boolean supports(final Class<? extends SynchronizeJob> type, final Konto konto) {
    /* Es wird geprüft, ob das Konto null oder disabled ist. */
    final boolean superSupports = super.supports(type, konto);
    if (!superSupports) {
      return false;
    }

    /* Konto ist bei Bic oder Ktr valide. */
    try {
      if (konto.getBLZ().equals("50021120")
          || konto.getBic().equals("FFBKDEFFTHK")) {
        return true;
      }
    } catch (final RemoteException re) {
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
  protected JobGroup createJobGroup(final Konto konto) {
    return new FfbJobGroup(konto);
  }

  protected class FfbJobGroup extends JobGroup {

    protected FfbJobGroup(final Konto konto) {
      super(konto);
    }

    @Override
    protected void sync() throws Exception {
      final ProgressMonitor monitor = worker.getMonitor();
      final String kn = this.getKonto().getLongName();

      final int step = 100 / worker.getSynchronization().size();

      this.checkInterrupted();

      monitor.log(" ");
      monitor.log(i18n.tr("Synchronisiere FFB-Konto: {0}", kn));
      final String pin = Application.getCallback()
          .askPassword("Bitte geben Sie Ihre PIN für den FFB-Zugang " + this.getKonto().getKundennummer() + " ein.");

      Logger.info("processing jobs");
      for (final SynchronizeJob job : this.jobs) {
        this.checkInterrupted();

        final FfbSynchronizeJob ffbjob = (FfbSynchronizeJob) job;
        final double depotwert = ffbjob.getDepotwert(
            this.getKonto().getKundennummer(),
            pin,
            this.getKonto().getKontonummer());
        this.checkInterrupted();
        final Umsatz newUmsatz = (Umsatz) Settings.getDBService().createObject(Umsatz.class, null);

        final Date umsatzdatum = new Date();
        newUmsatz.setKonto(this.getKonto());
        newUmsatz.setKommentar("Depotwert");
        newUmsatz.setSaldo(depotwert);
        newUmsatz.setDatum(umsatzdatum);
        newUmsatz.setValuta(umsatzdatum);
        newUmsatz.setBetrag(0.00);
        newUmsatz.setArt("Depotwert");
        newUmsatz.store();
        this.getKonto().setSaldo(depotwert);
        this.getKonto().store();
        this.checkInterrupted();

        monitor.addPercentComplete(step);
      }
    }

  }
}
