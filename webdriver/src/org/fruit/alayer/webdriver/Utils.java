package org.fruit.alayer.webdriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO GB Remove, just for development
public class Utils {

  public static void logAndEnd() {
    System.out.println();
    System.out.println("We're done !");
    killWebDriver();
    System.out.println();

    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    for (int i = 2; i < stElements.length; i++) {
      StackTraceElement ste = stElements[i];
      System.out.format("%2s : %-60s || %-40s || %5s || %s \n", i,
          ste.getClassName(), ste.getMethodName(),
          ste.getLineNumber(), ste.getFileName());
    }

    System.out.println();
    System.exit(1);
  }

  private static void killWebDriver() {
    // TODO Just for development
    try {
      Runtime rt = Runtime.getRuntime();
      Process pr = rt.exec("ps ax");
      BufferedReader input = new BufferedReader(
          new InputStreamReader(pr.getInputStream()));

      String line;
      while ((line = input.readLine()) != null) {
        if (line.toLowerCase().contains("enable-automation")) {
          rt.exec("kill -9 " + line.trim().split(" ")[0]);
        }
      }
      System.out.println();
      System.out.println("Exited with error code " + pr.waitFor());
      System.out.println();
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
