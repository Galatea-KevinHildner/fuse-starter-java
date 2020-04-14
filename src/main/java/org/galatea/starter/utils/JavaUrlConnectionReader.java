package org.galatea.starter.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class copied from https://alvinalexander.com/blog/post/java/java-how-read-from-url-string-text/.
 */
@Slf4j
@NoArgsConstructor
public class JavaUrlConnectionReader {

  /**
   * Gets the contents of the specified URL.
   * @param theUrl URL being requested
   * @return the URL's contents
   */
  public static String getUrlContents(final String theUrl) {
    StringBuilder content = new StringBuilder();

    // many of these calls can throw exceptions, so i've just
    // wrapped them all in one try/catch statement.
    try {
      // create a url object
      URL url = new URL(theUrl);

      log.info("Attempting to make URLConnection with " + theUrl);
      // create a urlconnection object
      URLConnection urlConnection = url.openConnection();
      log.info("Attempt successful");
      // wrap the urlconnection in a bufferedreader
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      String line;

      // read from the urlconnection via the bufferedreader
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line).append("\n");
      }
      bufferedReader.close();
    } catch (Exception e) {
      log.error("URL request failed for " + theUrl + "\nError stack trace:\n" + e.toString());
      e.printStackTrace();
    }
    log.trace("URL request returned:\n" + content.toString());
    return content.toString();
  }
}