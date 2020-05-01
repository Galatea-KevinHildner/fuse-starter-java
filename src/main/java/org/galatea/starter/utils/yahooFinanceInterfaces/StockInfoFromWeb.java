package org.galatea.starter.utils.yahooFinanceInterfaces;

import java.io.IOException;
import java.util.List;
import org.galatea.starter.utils.yahoofinancedata.DailyStockInfo;

public interface StockInfoFromWeb {

  /**
   * Retrieves stock information from the web for the indicated stock ticker
   * @param ticker 3 or 4 letter code that represents a stock
   * @return A list representing that stock's historical price data
   */
  List<DailyStockInfo> retrieve(String ticker) throws IOException;
}
