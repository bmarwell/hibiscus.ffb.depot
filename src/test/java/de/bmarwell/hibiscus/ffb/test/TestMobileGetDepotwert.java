package de.bmarwell.hibiscus.ffb.test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.bmarwell.ffb.depot.client.FfbClientConfiguration;
import de.bmarwell.ffb.depot.client.FfbMobileClient;
import de.bmarwell.ffb.depot.client.err.FfbClientError;
import de.bmarwell.ffb.depot.client.json.LoginResponse;
import de.bmarwell.ffb.depot.client.json.MyFfbResponse;
import de.bmarwell.ffb.depot.client.value.FfbLoginKennung;
import de.bmarwell.ffb.depot.client.value.FfbPin;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.willuhn.logging.Level;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMobileGetDepotwert {

  private static final Logger LOG = LoggerFactory.getLogger(TestMobileGetDepotwert.class);

  public static final FfbLoginKennung LOGIN = FfbLoginKennung.of("22222301");
  public static final FfbPin PIN = FfbPin.of("91901");


  @Rule
  public WireMockRule wiremock = new WireMockRule(wireMockConfig().dynamicPort());
  private FfbMobileClient client;
  private FfbMobileClient clientWithoutCredentials;


  @Before
  public void setUp() throws Exception {
    de.willuhn.logging.Logger.setLevel(Level.DEBUG);
    final FfbClientConfiguration config = () -> URI.create("http://localhost:" + wiremock.port());

    this.client = new FfbMobileClient(LOGIN, PIN, config);
    this.clientWithoutCredentials = new FfbMobileClient(config);
  }

  @Test
  public void testGetDepotwertWithoutCredentials() throws FfbClientError {
    clientWithoutCredentials.logon();

    assertFalse(clientWithoutCredentials.isLoggedIn());
    final LoginResponse loginResponse = clientWithoutCredentials.loginInformation();
    LOG.debug("Login: [{}].", loginResponse);

    assertFalse(loginResponse.isLoggedIn());
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
    client.logon();
    final LoginResponse loginResponse = client.loginInformation();
    assertTrue(loginResponse.isLoggedIn());

    LOG.debug("Login: [{}].", loginResponse);

    assertTrue(loginResponse.isLoggedIn());
    Assert.assertEquals("Customer", loginResponse.getUsertype());
    Assert.assertEquals("E1000590054", loginResponse.getLastname());

    final MyFfbResponse ffbDepotInfo = client.fetchAccountData();
    assertFalse(ffbDepotInfo.isModelportfolio());
    assertFalse(ffbDepotInfo.getGesamtwert().equals(BigDecimal.ZERO));
    LOG.debug("MyFfb: [{}].", ffbDepotInfo);
  }

}
