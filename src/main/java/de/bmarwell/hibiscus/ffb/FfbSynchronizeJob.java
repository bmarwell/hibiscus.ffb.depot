package de.bmarwell.hibiscus.ffb;

import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob;

public interface FfbSynchronizeJob extends SynchronizeJob {
  public double getDepotwert(String login, String pin, String depotnummer);

}
