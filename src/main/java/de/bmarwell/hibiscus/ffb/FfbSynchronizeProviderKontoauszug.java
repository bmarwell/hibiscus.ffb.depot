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

import com.google.common.collect.ImmutableList;

import de.willuhn.annotation.Lifecycle;
import de.willuhn.annotation.Lifecycle.Type;
import de.willuhn.jameica.hbci.SynchronizeOptions;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

@Lifecycle(Type.CONTEXT)
public class FfbSynchronizeProviderKontoauszug implements FfbSynchronizeJobProvider {

  @Resource
  private FfbSynchronizeBackend backend = null;

  // Liste der von diesem Backend implementierten Jobs
  private static final List<Class<? extends SynchronizeJob>> JOBS = ImmutableList
      .<Class<? extends SynchronizeJob>>of(FfbSynchronizeKonzoauszug.class);

  @Override
  public List<SynchronizeJob> getSynchronizeJobs(Konto konto) {
    Class<SynchronizeJobKontoauszug> type = SynchronizeJobKontoauszug.class;

    List<SynchronizeJob> jobs = new LinkedList<SynchronizeJob>();

    for (Konto kt : backend.getSynchronizeKonten(konto)) {
      try {
        if (!backend.supports(type, konto)) {
          continue;
        }

        final SynchronizeOptions options = new SynchronizeOptions(kt);

        if (!options.getSyncKontoauszuege()) {
          continue;
        }

        SynchronizeJobKontoauszug job = backend.create(type, kt);
        job.setContext(SynchronizeJob.CTX_ENTITY, kt);
        jobs.add(job);
      } catch (RemoteException re) {
        Logger.error("unable to load synchronize jobs", re);
      } catch (ApplicationException ae) {
        Logger.error("unable to load synchronize jobs", ae);
      }
    }

    return jobs;

  }

  @Override
  public List<Class<? extends SynchronizeJob>> getJobTypes() {
    return JOBS;
  }

  @Override
  public boolean supports(Class<? extends SynchronizeJob> type, Konto konto) {
    return true;
  }

  @Override
  public int compareTo(Object other) {
    return 1;
  }

}
