package org.galatea.starter.utils.YahooFinanceData;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


@AllArgsConstructor
@Getter
public class Request {

  @NonNull
  private String ticker; // Ticker of the stock being requested
  @NonNull
  private int days; // # of days of data being requested
  @NonNull
  private int id; // ID # for the request
  @NonNull
  private Date date; // Date the request was made

  //TODO: Check for invalid inputs
}
