package org.galatea.starter.utils.AlphaVantage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AVMetaData {
  private String information;
  private String symbol;
  private String lastRefreshed;
  private String outputSize;
  private String timeZone;

  @JsonCreator
  public AVMetaData(@JsonProperty("1. Information") String information,
      @JsonProperty("2. Symbol") String symbol,
      @JsonProperty("3. Last Refreshed") String lastRefreshed,
      @JsonProperty("4. Output Size") String outputSize,
      @JsonProperty("5. Time Zone") String timeZone){
    this.information = information;
    this.symbol = symbol;
    this.lastRefreshed = lastRefreshed;
    this.outputSize = outputSize;
    this.timeZone = timeZone;
  }

}
