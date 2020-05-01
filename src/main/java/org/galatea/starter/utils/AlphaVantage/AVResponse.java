package org.galatea.starter.utils.AlphaVantage;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.yahoofinancedata.DailyStockInfo;
import org.galatea.starter.utils.yahoofinancedata.PriceData;

@Slf4j
@Getter
public class AVResponse {
  private AVMetaData responseMetaData;
  private Map<String, PriceData> responsePrices;

  @JsonCreator
  public AVResponse(@JsonProperty("Meta Data") AVMetaData responseMetaData,
      @JsonProperty("Time Series (Daily)") Map<String, PriceData> responsePriceInfo) {
    this.responseMetaData = responseMetaData;
    this.responsePrices = responsePriceInfo;
  }

  @JsonAnySetter
  public void add(String key, PriceData value) {
    responsePrices.put(key, value);
  }

  /**
   * Converts the responsePrices data into a list of daily stock information
   * @return a LinkedList of daily stock information for the ticker that the response represents
   */
  public List<DailyStockInfo> dailyStockInfoList(){
    // Creates an iterator to parse through all map entries
    Iterator<Map.Entry<String, PriceData>> responseIterator = responsePrices.entrySet().iterator();
    LinkedList<DailyStockInfo> returnList = new LinkedList<DailyStockInfo>();

    while(responseIterator.hasNext()) {
      Map.Entry<String, PriceData> responseEntry = responseIterator.next();

      String ticker = responseMetaData.getSymbol();
      String date = responseEntry.getKey();
      PriceData prices = responseEntry.getValue();
      DailyStockInfo currentDay = new DailyStockInfo(ticker, date, prices);

      returnList.add(currentDay);
    }
    return returnList;
  }

}
