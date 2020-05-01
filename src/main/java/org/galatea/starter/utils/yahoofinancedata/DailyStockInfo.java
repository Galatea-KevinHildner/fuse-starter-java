package org.galatea.starter.utils.yahoofinancedata;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;

/**
 * Stores price information for a single stock on a particular day.
 */
@Getter
public class DailyStockInfo {

  private String ticker; // Ticker of the stock that the info is for
  private String date; // Date that the PriceInfo applies to
  private PriceData priceData;

  /**
   * Constructs a new PriceInfo object.
   * @param ticker The Ticker that represents the stock
   * @param date The date that the stored information applies to
   * @param priceData The stocks price data for the day
   */
  public DailyStockInfo(@NonNull final String ticker, @NonNull final String date,
      @NonNull final PriceData priceData) {

    // Check ticker format
    if (!ticker.matches("\\D{1,4}")) {
      throw new IllegalArgumentException("Improper ticker format");
    }

    // Check date format
    Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    if (!p.matcher(date).matches()) {
      throw new IllegalArgumentException("Improper date format");
    }

    this.ticker = ticker;
    this.date = date;
    this.priceData = priceData;
  }
}

