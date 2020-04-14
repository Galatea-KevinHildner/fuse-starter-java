package org.galatea.starter.utils.yahoofinancedata;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;


@Getter
public class Request {

  @NonNull
  private String ticker; // Ticker of the stock being requested
  @NonNull
  private int days; // # of days of data being requested
  @NonNull
  private int id; // ID # for the request
  @NonNull
  private String date; // Date the request was made

  public Request(String ticker, int days, int id, String date){
    // Check ticker format
    if(!ticker.matches("\\D{1,4}"))
      throw new IllegalArgumentException("Improper ticker format");

    // Check date format
    Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    if(!p.matcher(date).matches())
      throw new IllegalArgumentException("Improper date format");

    // Check integer inputs
    if(days < 0)
      throw new IllegalArgumentException("Negative days input");
    if(id < 0)
      throw new IllegalArgumentException("Invalid ID");

    this.ticker = ticker;
    this. days = days;
    this.id = id;
    this.date = date;
  }
}

