package org.galatea.starter.utils.yahoofinancedata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PriceData {
  private double open;
  private double high;
  private double low;
  private double close;
  private int volume;

  /**
   * Stores only the price information for a single day of
   * @param open Opening stock price for the day
   * @param high Highest stock price for the day
   * @param low Lowest stock price for the day
   * @param close Closing stock price for the day
   * @param volume Volume of stocks traded during the day
   */
  @JsonCreator
  public PriceData(@JsonProperty("1. open") double open, @JsonProperty("2. high") double high,
      @JsonProperty("3. low") double low, @JsonProperty("4. close") double close,
      @JsonProperty("5. volume") int volume) {
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }
}
