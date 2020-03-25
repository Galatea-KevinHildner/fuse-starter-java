package org.galatea.starter.service;

import java.util.Date;
import java.util.LinkedList;
import org.galatea.starter.utils.YahooFinanceData.PriceInfo;
import org.galatea.starter.utils.YahooFinanceData.Request;
import org.galatea.starter.utils.JavaUrlConnectionReader;

public class YahooFinanceService {
  private final String alphaVantageKey = "KBOP9W9HW83OI5DP";
  private JavaUrlConnectionReader urlConnectionReader;


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
  private boolean getFromWeb(String ticker) {
    // Build the URL
    StringBuilder url = new StringBuilder();
    url.append("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=");
    url.append(ticker);
    url.append("&apikey=");
    url.append(alphaVantageKey);

    // Get the data from alphavantage
    String rawData = urlConnectionReader.getUrlContents(url);
    String avErrMsg = "{\n"
        + "    \"Error Message\": \"Invalid API call. Please retry or visit the documentation (https://www.alphavantage.co/documentation/) for TIME_SERIES_DAILY.\"\n"
        + "}\n";

    if(rawData.equals(avErrMsg)){
      return false;
    }

    // Add data to database
    //TODO: Extract data and add it to MySQL server
  }
}
