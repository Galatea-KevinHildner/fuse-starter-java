package org.galatea.starter.utils.AlphaVantage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.galatea.starter.utils.JavaUrlConnectionReader;
import org.galatea.starter.utils.yahooFinanceInterfaces.StockInfoFromWeb;
import org.galatea.starter.utils.yahoofinancedata.DailyStockInfo;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class AlphaVantageHandler implements StockInfoFromWeb {

  @Value("spring.datasource.alphavantage-url")
  private String urlFrame;

  @Override
  public List<DailyStockInfo> retrieve(String ticker) throws IOException {
    String url = String.format(urlFrame, ticker);
    String responseData = JavaUrlConnectionReader.getUrlContents(url);
    if(responseData.contains("Error Message")) {
      log.info("request to alphavantage returned no results for " + ticker.toLowerCase());
      return null;
    }
    AVResponse avResponse = new ObjectMapper().readValue(responseData, AVResponse.class);
    return avResponse.dailyStockInfoList();
  }
}
