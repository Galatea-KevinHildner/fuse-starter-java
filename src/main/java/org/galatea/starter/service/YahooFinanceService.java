package org.galatea.starter.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.AlphaVantage.AlphaVantageHandler;
import org.galatea.starter.utils.ServiceCode;
import org.galatea.starter.utils.yahoofinancedata.DailyStockInfo;
import org.galatea.starter.utils.yahoofinancedata.PriceData;
import org.galatea.starter.utils.yahoofinancedata.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YahooFinanceService {

  @Value("spring.datasource.url")
  private String url;
  @Value("spring.datasource.database-profile")
  private String databaseProfile;
  @Value("spring.datasource.password")
  private String databasePassword;

  /**
   * Attempts to collect the data specified in the Request object.
   *
   * @param request The object representing the requested stock information
   * @return LinkedList of PriceInfo objects that contain the requested information
   */
  public List<DailyStockInfo> handleRequest(final Request request) throws DataFormatException, SQLException {
    // Extract request info
    int days = request.getDays();
    String date;
    String ticker = request.getTicker();

    List<DailyStockInfo> resultList = new ArrayList<DailyStockInfo>();

    for (int i = 0; i <= days; i++) {
      date = dateMath(request.getDate(), (-1 * i));
      DailyStockInfo current = getInfoFromDatabase(ticker, date);

      if (current == null) { // Ticker and date combination not found in database
        ServiceCode retrievalResult = gatherInfoFromWeb(ticker);
        switch (retrievalResult) {
          case SUCCESS:
            current = getInfoFromDatabase(ticker, date);
            break;
          case INVALID_TICKER:
            return null;
          case INVALID_DATA_RECIEVED:
            log.error("api for retrieving stock information returned an incorrect format");
            throw new DataFormatException("data from the web was incorrectly formatted");
          default:
            log.error("gatherInfoFromWeb returned an unexpected service code");
            throw new DataFormatException("unexpected service code entered into switch case");
        }
      }
      resultList.add(current);
    }
    return resultList;
  }

  /**
   * Retrieves price information from local MySQL database for a single ticker on a specific day.
   *
   * @param ticker The ticker for the desired stock
   * @param date The desired date for the stock
   * @return PriceInfo object representing the specified stock on the specified date. Returns null
   *     if there is no matching entry in the database.
   */
  private DailyStockInfo getInfoFromDatabase(final String ticker, final String date) throws SQLException {
    try {
      Connection mySqlConnection = DriverManager.getConnection(url, databaseProfile, databasePassword);

      String requestString = "SELECT * FROM stocks WHERE name =? AND day =?";
      PreparedStatement requestStatement = mySqlConnection.prepareStatement(requestString);
      requestStatement.setString(1, ticker);
      requestStatement.setString(2, date);

      ResultSet rs = requestStatement.executeQuery();
      DailyStockInfo result = null;
      if (rs.next()) {
        double open = rs.getDouble("open");
        double high = rs.getDouble("high");
        double low = rs.getDouble("low");
        double close = rs.getDouble("close");
        int volume = rs.getInt("volume");
        result =  new DailyStockInfo(ticker, date, open, high, low, close, volume);
      }
      closeConnectionObjects(rs, requestStatement, mySqlConnection);
      return result;
    } catch (SQLException e) {
      log.error("unable to complete request to stocks database:\n" + e.toString());
      throw e;
    }
  }

  /**
   * Retrieves price data for the last 100 days from AlphaVantage for the indicated ticker.
   *
   * @param ticker The ticker representing the requested stock
   * @return a ServiceCode Enum indicating the outcome of the attempt to retrieve the data from
   *     AlphaVantage and add it to the MySQL database.
   */
  private ServiceCode gatherInfoFromWeb(final String ticker) {
    List<DailyStockInfo> newStockInfo;
    try {
      newStockInfo = new AlphaVantageHandler().retrieve(ticker);
    } catch (IOException e) {
      return ServiceCode.INVALID_DATA_RECIEVED;
    }

    if (newStockInfo == null) {
      return ServiceCode.INVALID_TICKER;
    }

    Connection mySqlConnection;
    PreparedStatement insertStatement;
    try {
      mySqlConnection = DriverManager.getConnection(url, databaseProfile, databasePassword);
      String insertCommand = "INSERT INTO stocks(name,day,open,high,low,close,volume) "
          + "VALUES(?,?,?,?,?,?,?)";
      insertStatement = mySqlConnection.prepareStatement(insertCommand);
      for(DailyStockInfo newDay : newStockInfo) {
        addStockToBatch(insertStatement, newDay);
      }
      insertStatement.executeBatch();
      return ServiceCode.SUCCESS;
    } catch (SQLException e) {
      log.error("failed to insert stocks into database");

    } finally {
      closeConnectionObjects(null, insertStatement, mySqlConnection);
    }

  }

  /**
   * Sets the parameters of the prepared statement for the specified stock and then adds it to the
   * batch.
   * @param statement The PreparedStatement that will be executed
   * @param stock The stock that is being added to the batch of PreparedStatements
   */
  private void addStockToBatch(PreparedStatement statement, DailyStockInfo stock) throws SQLException {
    PriceData prices = stock.getPriceData();
    statement.setString(1, stock.getTicker());
    statement.setString(2, stock.getDate());
    statement.setDouble(3, prices.getOpen());
    statement.setDouble(4, prices.getHigh());
    statement.setDouble(5, prices.getLow());
    statement.setDouble(6, prices.getClose());
    statement.setInt(7, prices.getVolume());
    statement.addBatch();
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

  /**
   * Closes the JDBC objects to prevent memory leaks.
   * @param resultSet Result set being closed
   * @param statement Statement being closed
   * @param connection Connection being closed
   */
  private void closeConnectionObjects(ResultSet resultSet, PreparedStatement statement,
      Connection connection) {
    if(resultSet != null) {
      resultSet.close();
    }
    if(statement != null) {
      statement.close();
    }
    if(connection != null) {
      connection.close();
    }
  }

}
