package de.bmarwell.hibiscus.ffb.test;

import de.bmarwell.hibiscus.ffb.scraper.FfbPage;
import de.bmarwell.hibiscus.ffb.scraper.FfbScraper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScraperGetDepotwert {

  private static final Logger LOG = LoggerFactory.getLogger(TestScraperGetDepotwert.class);

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetDepotwertWithoutCredentials() {
    FfbScraper scraper = new FfbScraper();

    scraper.logon();

    LOG.debug("Aktuelle Seite: [{}].", scraper.getCurrentPage().getPath().toString());
    Assert.assertFalse(scraper.onCorrectPage(FfbPage.UEBERSICHT));
    Assert.assertFalse(scraper.onCorrectPage(FfbPage.LOGOFF));
    Assert.assertTrue(scraper.onCorrectPage(FfbPage.LOGIN));
  }

  /**
   * Tests with a valid test login.
   *
   * <p>Login taken from: <a href=
   * "http://www.wertpapier-forum.de/topic/31477-demo-logins-depots-einfach-mal-testen/page__view__findpost__p__567459">
   * Wertpapier-Forum</a>.<br> Login: 22222301<br> PIN: 91901</p>
   */
  @Test
  public void testGetDepotwertWithCredentials() {
    FfbScraper scraper = new FfbScraper("22222301", "91901");
    scraper.scrape();

    LOG.debug("Depotwert: [{}].", scraper.getDepotwert());

    Assert.assertTrue(scraper.getDepotwert() != null);
    Assert.assertTrue(scraper.getDepotwert().contains(","));
    Assert.assertTrue(scraper.getDepotwert().matches("[0-9]+,[0-9]{2}"));
  }

}
