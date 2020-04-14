package org.galatea.starter.utils.yahoofinancedata;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PriceInfo {

  private String ticker; // Ticker of the stock that the info is for
  private String date; // Date that the PriceInfo applies to

  // Daily stock information
  private double open;
  private double high;
  private double low;
  private double close;
  private int volume;

  public PriceInfo(@NonNull String ticker, @NonNull String date, @NonNull double open,
      @NonNull double high, @NonNull double low, @NonNull double close, @NonNull int volume){

    // Check ticker format
    if(!ticker.matches("\\D{1,4}"))
      throw new IllegalArgumentException("Improper ticker format");

    // Check date format
    Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    if(!p.matcher(date).matches())
      throw new IllegalArgumentException("Improper date format");

    // Check for valid number inputs
    if(open < 0.0 || high < 0.0 || low < 0.0 || close < 0.0 || volume < 0)
      throw new IllegalArgumentException("Negative stock value");

    this.ticker = ticker;
    this.date = date;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }
}

