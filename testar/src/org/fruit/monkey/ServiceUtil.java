package org.fruit.monkey;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;

public class ServiceUtil {
  public static void waitForConnection(String url) throws TestarServiceException {
    System.out.println("Waiting for connection...");
    final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    final HttpGet testRequest = new HttpGet(url);
    HttpResponse testResponse;
    int status = 400;
    while (status >= 400 && status < 600) {
      boolean isError = false;
      try {
        testResponse = httpClient.execute(testRequest);
        status = testResponse.getCode();
      } catch (Exception e) {
        System.out.println("Not yet ready: " + e);
        isError = true;
      }
      finally {
        testRequest.reset();
      }

      System.out.println("...done");
      if (!isError && status != HttpStatus.SC_OK) {
        System.out.println("Status: " + status);
      }

      try {
        Thread.sleep(5000);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
