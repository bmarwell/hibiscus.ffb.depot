package de.bmarwell.hibiscus.ffb.test;

import de.bmarwell.ffb.depot.client.FfbMobileClient;
import de.bmarwell.ffb.depot.client.err.FfbClientError;
import de.bmarwell.ffb.depot.client.json.LoginResponse;
import de.bmarwell.ffb.depot.client.json.MyFfbResponse;
import de.bmarwell.ffb.depot.client.value.FfbLoginKennung;
import de.bmarwell.ffb.depot.client.value.FfbPin;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestMobileGetDepotwert {

  private static final Logger LOG = LoggerFactory.getLogger(TestMobileGetDepotwert.class);

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetDepotwertWithoutCredentials() throws FailingHttpStatusCodeException, IOException, FfbClientError {
    FfbMobileClient mobileAgent = new FfbMobileClient();
    mobileAgent.logon();

    Assert.assertTrue(mobileAgent.loginInformation().isPresent());
    LoginResponse loginResponse = mobileAgent.loginInformation().get();
    LOG.debug("Login: [{}].", loginResponse);

    Assert.assertFalse(loginResponse.isLoggedIn());
  }

  /**
   * Tests with a valid test login.
   *
   * <p>Login taken from: <a href=
   * "http://www.wertpapier-forum.de/topic/31477-demo-logins-depots-einfach-mal-testen/page__view__findpost__p__567459">
   * Wertpapier-Forum</a>.<br> Login: 22222301<br> PIN: 91901</p>
   *
   * @throws FfbClientError
   *           Fehler beim Holen der Daten über HTTPS.
   * @throws MalformedURLException
   *           Fehler beim Erstellen des Clients.
   */
  @Test
  public void testGetDepotwertWithCredentials() throws FfbClientError, MalformedURLException {
    FfbLoginKennung ffblogin = FfbLoginKennung.of("22222301");
    FfbPin ffbpin = FfbPin.of("91901");

    FfbMobileClient mobileAgent = new FfbMobileClient(ffblogin, ffbpin);
    mobileAgent.logon();
    Assert.assertTrue(mobileAgent.loginInformation().isPresent());

    LoginResponse loginResponse = mobileAgent.loginInformation().get();
    LOG.debug("Login: [{}].", loginResponse);

    Assert.assertTrue(loginResponse.isLoggedIn());
    Assert.assertEquals("Customer", loginResponse.getUsertype());
    Assert.assertEquals("E1000590054", loginResponse.getLastname());

    MyFfbResponse ffbDepotInfo = mobileAgent.fetchAccountData();
    Assert.assertTrue(ffbDepotInfo.isLogin());
    Assert.assertFalse(ffbDepotInfo.isModelportfolio());
    Assert.assertTrue(ffbDepotInfo.getGesamtwert() != 0.00d);
    LOG.debug("MyFfb: [{}].", ffbDepotInfo);

    // Assert.assertTrue(mobileAgent.getDepotwert().contains(","));
    // Assert.assertTrue(mobileAgent.getDepotwert().matches("[0-9]+,[0-9]{2}"));
  }

}
