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

import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;

/**
 * Lädt den Depotwert und erstellt dazu einen Umsatz.
 */
public class FfbSynchronizeKonzoauszug extends SynchronizeJobKontoauszug implements FfbSynchronizeJob {
  private static final String LOGINFORM = "loginform";

  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private static final String PAGE_LOGON = "https://www.ffb.de/login/login.jsp";

  @Resource
  private FfbSynchronizeBackend backend = null;

  private final WebClient webClient;

  private HtmlPage currentPage;

  public FfbSynchronizeKonzoauszug() {
    webClient = new WebClient();
  }

  @Override
  public void setDepotwert() {
    logon();

    String depotwert = getCurrentDepotwert();

    logoff();
  }

  private void logoff() {
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

  private String getCurrentDepotwert() {
    FrameWindow mainFrame = currentPage.getFrameByName("main");
    HtmlPage enclosingPage = (HtmlPage) mainFrame.getEnclosedPage();
    // #depotBlockView > tfoot > tr:nth-child(3) > td.fRight.fTableCell > div
    //
    HtmlDivision gesamtwertdiv = enclosingPage.getFirstByXPath("//*[@id='depotBlockView']/tfoot/tr[3]/td[3]/div");
    String depotwert = gesamtwertdiv.getNodeValue();

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

  private void logon() {
    try {
      URL logonpage = new URL(PAGE_LOGON);
      HtmlPage loginpage = webClient.getPage(logonpage);
      HtmlForm loginform = loginpage.getFormByName(LOGINFORM);
      HtmlInput depotnummer = loginform.getInputByName("login");
      HtmlInput pin = loginform.getInputByName("pin");

      HtmlSubmitInput submit = loginform.getFirstByXPath("input");
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

}
