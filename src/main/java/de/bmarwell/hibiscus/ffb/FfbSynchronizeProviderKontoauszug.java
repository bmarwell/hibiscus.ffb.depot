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

import static org.slf4j.LoggerFactory.getLogger;

import de.willuhn.annotation.Lifecycle;
import de.willuhn.annotation.Lifecycle.Type;
import de.willuhn.jameica.hbci.SynchronizeOptions;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;

@Lifecycle(Type.CONTEXT)
public class FfbSynchronizeProviderKontoauszug implements FfbSynchronizeJobProvider {

  private static final org.slf4j.Logger LOG = getLogger(FfbSynchronizeProviderKontoauszug.class);

  @Resource
  private final FfbSynchronizeBackend backend = null;

  // Liste der von diesem Backend implementierten Jobs
  private static final List<Class<? extends SynchronizeJob>> JOBS = Collections.singletonList(
      FfbSynchronizeKonzoauszug.class);

  @Override
  public List<SynchronizeJob> getSynchronizeJobs(final Konto konto) {
    final Class<SynchronizeJobKontoauszug> type = SynchronizeJobKontoauszug.class;

    final List<SynchronizeJob> jobs = new LinkedList<>();

    for (final Konto kt : backend.getSynchronizeKonten(konto)) {
      try {
        if (!backend.supports(type, konto)) {
          continue;
        }

        final SynchronizeOptions options = new SynchronizeOptions(kt);

        if (!options.getSyncSaldo()) {
          continue;
        }

        final SynchronizeJobKontoauszug job = backend.create(type, kt);
        job.setContext(SynchronizeJob.CTX_ENTITY, kt);
        jobs.add(job);
      } catch (final RemoteException | ApplicationException re) {
        Logger.error("unable to load synchronize jobs", re);
      }
    }

    return jobs;

  }

  @Override
  public List<Class<? extends SynchronizeJob>> getJobTypes() {
    return JOBS;
  }

  @Override
  public boolean supports(final Class<? extends SynchronizeJob> type, final Konto konto) {
    if (null == konto) {
      LOG.error("Null-Konto übergeben!", konto);
      return false;
    }

    return isFfbKonto(konto);
  }

  private boolean isFfbKonto(final Konto konto) {
    try {
      if ("50021120".equals(konto.getBLZ()) || "".equals(konto.getBic())) {
        return true;
      }
    } catch (final RemoteException e) {
      LOG.error("Fehler beim Abfragen von Kontonummer oder BLZ für Konto [{}].", konto, e);

      return false;
    }

    LOG.debug("Konto [{}] ist kein FFB-Konto.", konto);
    return false;
  }

  @Override
  public int compareTo(final Object other) {
    return 0;
  }

}
