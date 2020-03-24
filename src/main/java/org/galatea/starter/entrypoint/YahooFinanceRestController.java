package org.galatea.starter.entrypoint;

import java.util.LinkedList;
import java.util.Date;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.service.YahooFinanceService;
import org.galatea.starter.utils.YahooFinanceData.PriceInfo;
import org.galatea.starter.utils.YahooFinanceData.Request;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST Controller that listens to http endpoints and allows the caller to send text to be
 * processed.
 */
@RequiredArgsConstructor
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class YahooFinanceRestController extends BaseRestController {
  @NonNull
  YahooFinanceService yahooFinanceService;
  @Builder.Default
  private static int idTracker = 0;

  @GetMapping("/prices")
  public LinkedList<PriceInfo> getPriceInfo(
      @RequestParam(value = "stock") String ticker,
      @RequestParam(value = "days") int days) {

    idTracker++;
    // TODO: Handle exceptions for invalid Request data input
    Request req = new Request(ticker, days, idTracker, new Date());

    return yahooFinanceService.handleRequest(req);
  }

}
