package org.galatea.starter.utils.yahoofinancedata;

import static org.junit.Assert.*;

import org.junit.Test;


public class PriceInfoTest {
  String valTicker = "MSFT";
  String valDate = "2020-04-14";
  double valDouble = 15.0;
  int valInt = 300;

  @Test
  public void invalidTicker() {
    String[] badTickers = {"M2FT", "MSFTT",""};
    for (String badTicker : badTickers) {
      try {
        new PriceInfo(badTicker, valDate, valDouble, valDouble, valDouble, valDouble, valInt);
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
        new PriceInfo(valTicker, badDate, valDouble, valDouble, valDouble, valDouble, valInt);
        fail();
      } catch (IllegalArgumentException e) {
        assertEquals("Improper date format", e.getMessage());
      }
    }
  }

  @Test
  public void invalidStockValues() {
    try {
      new PriceInfo(valTicker, valDate, -1.0, valDouble, valDouble, valDouble, valInt);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Negative stock value", e.getMessage());
    }

    try {
      new PriceInfo(valTicker, valDate, valDouble, -1.0, valDouble, valDouble, valInt);
      fail();
    } catch(IllegalArgumentException e) {
      assertEquals("Negative stock value", e.getMessage());
    }

    try {
      new PriceInfo(valTicker, valDate, valDouble, valDouble, -1.0, valDouble, valInt);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Negative stock value", e.getMessage());
    }

    try {
      new PriceInfo(valTicker, valDate, valDouble, valDouble, valDouble, -1.0, valInt);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Negative stock value", e.getMessage());
    }

    try {
      new PriceInfo(valTicker, valDate, valDouble, valDouble, valDouble, valDouble, -1);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Negative stock value", e.getMessage());
    }
  }
}
