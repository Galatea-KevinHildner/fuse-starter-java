package org.galatea.starter.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.JavaUrlConnectionReader;
import org.galatea.starter.utils.ServiceCodes;
import org.galatea.starter.utils.yahoofinancedata.PriceInfo;
import org.galatea.starter.utils.yahoofinancedata.Request;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YahooFinanceService {

  private Connection mySqlConn;

  /**
   * Establishes a connection to the results database upon creation.
   */
  public YahooFinanceService() {
    String dataBaseUrl = "jdbc:mysql://localhost:3306/result_database";
    String dataBasePW = "Galatea123";
    String dataBaseUser = "root";

    log.info("Attempting to connect to result database");
    try {
      this.mySqlConn = DriverManager.getConnection(dataBaseUrl, dataBaseUser, dataBasePW);
    } catch (SQLException e) {
      log.error("Unable to establish connection to result database");
    }
    log.info("Successfully connected to result database");
  }

  /**
   * Attempts to collect the data specified in the Request object.
   *
   * @param req The object representing the requested stock information
   * @return LinkedList of PriceInfo objects that contain the requested information
   */
  public LinkedList<PriceInfo> handleRequest(final Request req) throws DataFormatException {
    // Extract request info
    int days = req.getDays();
    String date;
    String ticker = req.getTicker();

    LinkedList<PriceInfo> resList = new LinkedList<>();
    PriceInfo current;

    for (int i = 0; i <= days; i++) {
      date = dateMath(req.getDate(), (-1 * i));
      current = getFromDatabase(ticker, date);

      if (current == null) { // Ticker and date combination not found in database
        switch (getFromWeb(ticker)) { // Get updated stock information from AlphaVantage
          case SUCCESS:
            current = getFromDatabase(ticker, date);
            break;
          case INVALID_TICKER:
            return null;
          default:
            // TODO: Unsure of what exception to throw here, but this should only happen if the AV data is formatted wrong
            throw new DataFormatException();
        }
      }
      resList.add(current);
    }
    return resList;
  }

  /**
   * Retrieves price information from local MySQL database for a single ticker on a specific day.
   *
   * @param ticker The ticker for the desired stock
   * @param date The desired date for the stock
   * @return PriceInfo object representing the specified stock on the specified date. Returns null
   *     if there is no matching entry in the database.
   */
  private PriceInfo getFromDatabase(final String ticker, final String date) {
    double open = -1.0;
    double high = -1.0;
    double low = -1.0;
    double close = -1.0;
    int volume = -1;

    try {
      Statement myStmt = mySqlConn.createStatement();
      String sql =
          "SELECT * FROM stocks WHERE name = \"" + ticker + "\" AND day = \"" + date + "\"";
      ResultSet rs = myStmt.executeQuery(sql);

      if (!rs.next()) {
        return null; // No results found in the database
      } else {
        // Extract data from the result set
        rs.next();
        open = rs.getDouble("open");
        high = rs.getDouble("high");
        low = rs.getDouble("low");
        close = rs.getDouble("close");
        volume = rs.getInt("volume");
      }
    } catch (SQLException e) {
      log.error("Unable to create Statement for MySQL database:\n" + e.toString());
    }

    return new PriceInfo(ticker, date, open, high, low, close, volume);
  }

  /**
   * Retrieves price data for the last 100 days from AlphaVantage for the indicated ticker
   *
   * @param ticker The ticker representing the requested stock
   * @return a ServiceCode Enum indicating the outcome of the attempt to retrieve the data from
   * AlphaVantage and add it to the MySQL database.
   */
  private ServiceCodes getFromWeb(final String ticker) {
    String alphaVantageKey = "KBOP9W9HW83OI5DP";

    // Build the URL
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
        + ticker
        + "&outputsize=compact&apikey=" // "compact" gets last 100 days, "full" gets all 20+ years of info
        + alphaVantageKey;

    // Get the data from AlphaVantage
    String rawData = JavaUrlConnectionReader.getUrlContents(url);
    String avErrMsg = "{\n"
        + "    \"Error Message\": \"Invalid API call. Please retry or visit the documentation "
        + "(https://www.alphavantage.co/documentation/) for TIME_SERIES_DAILY.\"\n}\n";
    if (rawData.equals(avErrMsg)) {
      log.info("No data was found for the ticker: " + ticker);
      return ServiceCodes.INVALID_TICKER;
    }

    // Convert raw data into PriceInfo
    Scanner scan = new Scanner(rawData);
    for (int i = 0; i <= 8; i++) { // Get rid of the metadata from AlphaVantage
      if (!scan.hasNextLine()) {
        log.warn("AlphaVantage MetaData is missing or incorrectly formatted");
        return ServiceCodes.INVALID_DATA_RECEIVED;
      }
      scan.nextLine();
      // Could add checks for format but it's not too important
    }

    Pattern doubPat = Pattern.compile("[\\d]+.[\\d]+");
    Matcher m;
    String sqlStmt;
    String currentLine;
    String day;
    double open = -1.0;
    double high = -1.0;
    double low = -1.0;
    double close = -1.0;
    int volume = -1;

    log.info("Processing AlphaVantage data and sending it to the Results database");
    while (scan.hasNextLine()) {
      //TODO: Optimize for pre-existing entries

      // Day
      day = scan.nextLine();
      day = day.substring(day.indexOf("\"") + 1);
      day = day.substring(0, day.indexOf("\""));

      // Open
      if (scan.hasNextLine()) {
        currentLine = scan.nextLine();
        m = doubPat.matcher(currentLine);
        if (m.find()) {
          open = Double.parseDouble(m.group(0));
        } else {
          log.warn("Unable to retrieve Open value from AlphaVantage Data");
        }
      }

      // High
      if (scan.hasNextLine()) {
        currentLine = scan.nextLine();
        m = doubPat.matcher(currentLine);
        if (m.find()) {
          high = Double.parseDouble(m.group(0));
        } else {
          log.warn("Unable to retrieve High value from AlphaVantage Data");
        }
      }

      // Low
      if (scan.hasNextLine()) {
        currentLine = scan.nextLine();
        m = doubPat.matcher(currentLine);
        if (m.find()) {
          low = Double.parseDouble(m.group(0));
        } else {
          log.warn("Unable to retrieve Low value from AlphaVantage Data");
        }
      }

      // Close
      if (scan.hasNextLine()) {
        currentLine = scan.nextLine();
        m = doubPat.matcher(currentLine);
        if (m.find()) {
          close = Double.parseDouble(m.group(0));
        } else {
          log.warn("Unable to retrieve Close value from AlphaVantage Data");
        }
      }

      // Volume
      if (scan.hasNextLine()) {
        currentLine = scan.nextLine();
        m = doubPat.matcher(currentLine);
        if (m.find()) {
          volume = Integer.parseInt(m.group(0));
        } else {
          log.warn("Unable to retrieve Volume value from AlphaVantage Data");
        }
      }

      // Inserts data into MySQL database ignoring duplicate ticker/date combinations
      sqlStmt = String.format("INSERT IGNORE INTO stocks(name,day,open,high,low,close,volume) "
          + "VALUES(\"%s\",'%s',%f,%f,%f,%f,%d)", ticker, day, open, high, low, close, volume);

      try {
        Statement myStmt = mySqlConn.createStatement();
        myStmt.execute(sqlStmt);
      } catch (SQLException e) {
        log.warn("Unable to create Statement for MySQL database:\n" + e.toString());
      }
    }
    scan.close();
    log.info("AlphaVantage data processing complete");
    return ServiceCodes.SUCCESS;
  }

  /**
   * Adds/subtracts the indicated number of days from the given date formatted (YYYY-MM-DD).
   *
   * @param date String representing start date in form (YYYY-MM-DD)
   * @param days number of days to add or subtract from the start date
   * @return Returns String representing the end date
   */
  private String dateMath(final String date, final int days) {
    // Split the date into day, month and year
    String[] arrOfStr = date.split("-", 3);
    int year = Integer.parseInt(arrOfStr[0]);
    int month = Integer.parseInt(arrOfStr[1]);
    int day = Integer.parseInt(arrOfStr[2]);

    // Create new Gregorian Calendar
    GregorianCalendar cal = new GregorianCalendar();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, day);

    // Add the indicated number of days and convert back to a String
    cal.add(GregorianCalendar.DAY_OF_MONTH, days);
    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
    return form.format(cal.getTime());
  }

}
