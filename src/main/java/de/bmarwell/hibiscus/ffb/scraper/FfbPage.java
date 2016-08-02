package de.bmarwell.hibiscus.ffb.scraper;

public enum FfbPage {
  LOGIN("/login"), UEBERSICHT("/diverse/indexKunde.jsp"), LOGOFF("/action/logout;");

  private String pathfragmet;

  FfbPage(String path) {
    this.pathfragmet = path;
  }

  public String getPathStartingFragmet() {
    return pathfragmet;
  }
}
