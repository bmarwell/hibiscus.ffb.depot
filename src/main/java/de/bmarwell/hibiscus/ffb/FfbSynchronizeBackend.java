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
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.synchronize.AbstractSynchronizeBackend;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Synchronisiert Daten von FFB-Konten.
 */
@Lifecycle(Lifecycle.Type.CONTEXT)
public class FfbSynchronizeBackend extends AbstractSynchronizeBackend<FfbSynchronizeJobProvider> {

  private static final I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  @Override
  public String getName() {
    return i18n.tr("FIL Fondsbank (FFB)");
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

      Logger.info("processing jobs");
      for (SynchronizeJob job : this.jobs) {
        this.checkInterrupted();

        FfbSynchronizeJob ffbjob = (FfbSynchronizeJob) job;
        ffbjob.setDepotwert();

        monitor.addPercentComplete(step);
      }
    }

  }
}
