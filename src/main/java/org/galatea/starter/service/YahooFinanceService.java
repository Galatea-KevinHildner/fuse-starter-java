package org.galatea.starter.service;

import java.util.Date;
import java.util.LinkedList;
import org.galatea.starter.utils.YahooFinanceData.PriceInfo;
import org.galatea.starter.utils.YahooFinanceData.Request;

public class YahooFinanceService {

  /**
   * Attempts to collect the data specified in the Request object
   * @param req The object representing the requested stock information
   * @return LinkedList of PriceInfo objects that contain the requested information
   */
  public LinkedList<PriceInfo> handleRequest(Request req){
    // Extract request info
    int id = req.getId();
    int days = req.getDays();
    Date date = req.getDate();
    String ticker = req.getTicker();

    return NULL;

  }

  /**
   * Attempts to retrieve price information from local MySQL database
   */
  private PriceInfo getFromDatabase(String Ticker, Date date, int id) {
    //TODO: Implement
    return NULL;
  }

  /**
   * Attempts to retrieve price information from alphavantage
   */
  private PriceInfo getFromWeb(String Ticker, Date date, int id) {
    //TODO: Implement
    return NULL;
  }
}
