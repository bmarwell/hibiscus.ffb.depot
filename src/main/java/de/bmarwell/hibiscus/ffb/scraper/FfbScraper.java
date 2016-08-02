package de.bmarwell.hibiscus.ffb.scraper;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FfbScraper {
  private static final String LOGINFORM = "loginform";

  private static final String PAGE_LOGON = "https://www.ffb.de/login/login.jsp";

  private final WebClient webClient;

  private HtmlPage currentPage;

  private byte[] pin = new String("").getBytes();

  private String user = new String("");

  private String depotwert = null;

  public FfbScraper() {
    this.webClient = new WebClient();
  }

  public FfbScraper(String user, String pin) {
    this();
    this.user = user;
    this.pin = pin.getBytes();
  }

  public void scrape() {
    try {
      logon();
      findCurrentDepotwert();
    } finally {
      if (onCorrectPage(FfbPage.UEBERSICHT) && webClient.getWebConnection() != null) {
        logoff();
      }
      webClient.close();
    }
  }

  public void logoff() {
    FrameWindow navFrame = currentPage.getFrameByName("nav");

    HtmlPage navPage = (HtmlPage) navFrame.getEnclosedPage();

    DomNodeList<DomElement> divs = navPage.getElementsByTagName("div");

    for (DomElement div : divs) {
      HtmlDivision realdiv = (HtmlDivision) div;
      if (realdiv.getAttribute("title").equals("Abmelden")) {
        try {
          realdiv.click();
          break;
        } catch (IOException ioEx) {
          // TODO Auto-generated catch block
          ioEx.printStackTrace();
        }
      }
    }

    webClient.close();
  }

  public String findCurrentDepotwert() {
    if (!onCorrectPage(FfbPage.UEBERSICHT)) {
      throw new RuntimeException("On wrong page");
    }
    FrameWindow mainFrame = currentPage.getFrameByName("main");
    HtmlPage enclosingPage = (HtmlPage) mainFrame.getEnclosedPage();
    // #depotBlockView > tfoot > tr:nth-child(3) > td.fRight.fTableCell > div
    // //*[@id="depotBlockView"]/tfoot/tr[3]/td[3]/div
    List<HtmlTableCell> gesamtwertdiv = (List<HtmlTableCell>) enclosingPage
        .getByXPath("//table[@id='depotBlockView']/tfoot/tr/td");

    Preconditions.checkNotNull(gesamtwertdiv, "gesamtwertdiv");

    for (HtmlTableCell node : gesamtwertdiv) {
      String cellcontent = node.getTextContent();

      if (cellcontent.trim().endsWith(" EUR")) {
        depotwert = cellcontent.trim();
        break;
      }
    }

    if (!depotwert.endsWith(" EUR")) {
      return null;
    }

    depotwert = depotwert.replace(" EUR", "").replace(".", "").trim();

    if (!depotwert.contains(",")) {
      return null;
    }

    // TODO: checks.

    return depotwert;
  }

  public boolean onCorrectPage(FfbPage ffbpage) {
    if (getCurrentPage().getPath().startsWith(ffbpage.getPathStartingFragmet())) {
      return true;
    }

    return false;
  }

  public URL getCurrentPage() {
    return currentPage.getUrl();
  }

  public void logon() {
    try {
      URL logonpage = new URL(PAGE_LOGON);
      HtmlPage loginpage = webClient.getPage(logonpage);
      HtmlForm loginform = loginpage.getFormByName(LOGINFORM);
      HtmlInput depotnummer = loginform.getInputByName("login");
      depotnummer.setValueAttribute(user);
      HtmlInput pininput = loginform.getInputByName("pin");
      pininput.setValueAttribute(new String(pin));

      HtmlSubmitInput submit = loginform.getFirstByXPath("//input[@type='submit']");
      currentPage = submit.click();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FailingHttpStatusCodeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public String getDepotwert() {
    return this.depotwert;
  }
}
