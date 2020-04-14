package org.galatea.starter.entrypoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.zip.DataFormatException;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.service.YahooFinanceService;
import org.galatea.starter.utils.yahoofinancedata.PriceInfo;
import org.galatea.starter.utils.yahoofinancedata.Request;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that listens to http endpoints and allows the caller to send text to be
 * processed.
 */
@RequiredArgsConstructor
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class YahooFinanceRestController extends BaseRestController {

  @Builder.Default
  private static int idTracker = 0;
  @NonNull
  YahooFinanceService yahooFinanceService;

  /**
   * Sends the received text to the YahooFinanceService to be processed.
   * @param ticker The ticker for the requested stock
   * @param days The # of days of stock information being requested
   * @return List of past price information for that stock
   */
  @GetMapping("/prices")
  public LinkedList<PriceInfo> getPriceInfo(
      @RequestParam(value = "stock") final String ticker,
      @RequestParam(value = "days") final int days) {

    idTracker++;
    // TODO: Handle exceptions for invalid Request data input
    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
    Request req = new Request(ticker, days, idTracker, form.format(new Date()));
    LinkedList<PriceInfo> res;
    try{
      res = yahooFinanceService.handleRequest(req);
    } catch(DataFormatException e){
      res = null;
    }
    return res;
  }

}
