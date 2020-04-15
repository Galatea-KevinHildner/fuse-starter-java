package org.galatea.starter.utils.yahoofinancedata;

import static org.junit.Assert.*;

import org.junit.Test;

public class RequestTest {
  String valTicker = "MSFT";
  String valDate = "2020-04-15";
  int valInt = 10;

  @Test
  public void invalidTicker() {
    String[] badTickers = {"M2FT", "MSFTT",""};
    for (String badTicker : badTickers) {
      try {
        new Request(badTicker, valInt, valInt, valDate);
        fail();
      } catch (IllegalArgumentException e) {
        assertEquals("Improper ticker format", e.getMessage());
      }
    }
  }

  @Test
  public void invalidDates() {
    String[] badDates = {"202-04-14","2020-0-14","2020-04-2","","--","20201-04-14","2020-021-14",
        "2020-04-123","yyyy-04-14","2020-mm-14","2020-04-dd","2020/04/14"};
    for (String badDate : badDates) {
      try {
        new Request(valTicker, valInt, valInt, badDate);
        fail();
      } catch (IllegalArgumentException e) {
        assertEquals("Improper date format", e.getMessage());
      }
    }
  }

  @Test
  public void invalidId() {
    try {
      new Request(valTicker, valInt, -1, valDate);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid ID", e.getMessage());
    }
  }

  @Test
  public void invalidDays() {
    try {
      new Request(valTicker, -1, valInt, valDate);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Negative days input", e.getMessage());
    }
  }
}