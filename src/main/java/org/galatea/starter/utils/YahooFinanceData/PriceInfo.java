package org.galatea.starter.utils.YahooFinanceData;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class PriceInfo {

  @NonNull
  private String ticker; // Ticker of the stock that the info is for
  @NonNull
  private Date date; // Date that the PriceInfo applies to
  @NonNull
  private int id; // ID corresponding to the request the PriceInfo object was made for

  // Daily stock information
  private double open;
  private double high;
  private double low;
  private double close;
  private int volume;


}
