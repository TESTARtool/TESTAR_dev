package org.fruit.alayer.webdriver;

import org.fruit.alayer.*;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class WdDriver extends SUTBase {
  private static RemoteWebDriver webDriver = null;
  private static List<String> windowHandles = new ArrayList<>();
  // TODO Make settable via settings or protocol
  public static boolean followLinks = true;

  private final Keyboard kbd = AWTKeyboard.build();
  private final Mouse mouse = WdMouse.build();

  private WdDriver(String sutConnector) {
    String[] parts = sutConnector.split(" ");
    String driverPath = parts[0].replace("\"", "");
    String url = parts[parts.length - 1].replace("\"", "");
    Dimension screenDimensions = null;
    Point screenPosition = null;
    if (parts.length == 3) {
      String tmp = parts[1].replace("\"", "").toLowerCase();
      String[] dims = tmp.split("\\+")[0].split("x");
      screenDimensions =
          new Dimension(Integer.valueOf(dims[0]), Integer.valueOf(dims[1]));
      try {
        screenPosition = new Point(Integer.valueOf(tmp.split("\\+")[1]),
            Integer.valueOf(tmp.split("\\+")[2]));
      }
      catch (ArrayIndexOutOfBoundsException aioobe) {

      }
    }

    ChromeDriverService service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(driverPath))
        .usingAnyFreePort()
        .build();
    ChromeOptions options = new ChromeOptions();
    String path = System.getProperty("user.dir");
    path = path.substring(0, path.length() - 3) + "chrome-extension";
    options.addArguments("load-extension=" + path);
    options.addArguments("disable-infobars");
    webDriver = new ChromeDriver(service, options);

    if (screenDimensions != null) {
      webDriver.manage().window().setSize(screenDimensions);
    }
    if (screenPosition != null) {
      webDriver.manage().window().setPosition(screenPosition);
    }

    webDriver.get(url);

    CanvasDimensions.startThread();
  }

  @Override
  public void stop() throws SystemStopException {
    if (webDriver != null) {
      webDriver.quit();
      webDriver = null;
    }

    CanvasDimensions.stopThread();
  }

  @Override
  public boolean isRunning() {
    return webDriver != null;
  }

  @Override
  public String getStatus() {
    return "WebDriver : " + webDriver.getCurrentUrl();
  }

  @Override
  public AutomationCache getNativeAutomationCache() {
    return null;
  }

  @Override
  public void setNativeAutomationCache() {
  }

  public static List<SUT> fromAll() {
    List<SUT> suts = new ArrayList<>();

    // TODO GB

    return suts;
  }

  public static WdDriver fromExecutable(String sutConnector)
      throws SystemStartException {
    if (webDriver != null) {
      webDriver.quit();
    }

    return new WdDriver(sutConnector);
  }

  @SuppressWarnings("unchecked")
  protected <T> T fetch(Tag<T> tag) {
    if (tag.equals(Tags.StandardKeyboard)) {
      return (T) kbd;
    }
    else if (tag.equals(Tags.StandardMouse)) {
      return (T) mouse;
    }
    else if (tag.equals(Tags.PID)) {
      long pid = -1;
      return (T) (Long) pid;
    }
    else if (tag.equals(Tags.HANDLE)) {
      Utils.logAndEnd();
      return null;
      // return (T) (Long) hProcess;
    }
    else if (tag.equals(Tags.ProcessHandles)) {
      Utils.logAndEnd();
      return null;
      // return (T) runningProcesses().iterator();
    }
    else if (tag.equals(Tags.SystemActivator)) {
      return (T) new WdProcessActivator();
    }
    return null;
  }

  public RemoteWebDriver getRemoteWebDriver() {
    return webDriver;
  }


  /*
   * Update the list of handles with added handles (new tabs) 
   * Remove handles from closed tabs
   */
  private static void updateHandlesList ()
  {
    Set<String> currentHandles = webDriver.getWindowHandles();

    // Remove handles not present anymore (closed tabs)
    for (String handle : new ArrayList<>(windowHandles)) {
      if (!currentHandles.contains(handle)) {
        windowHandles.remove(handle);
      }
    }
    // Add new handles (new tabs), shouldn't be more than one
    for (String handle : currentHandles) {
      if (!windowHandles.contains(handle)) {
        windowHandles.add(handle);
      }
    }
  }

  /*
   * Make sure the last tab has focus
   */
  public static void activate() {
    // Nothing to activate
    if (windowHandles.size() < 1) {
      return;
    }

    String handle = windowHandles.get(followLinks ? windowHandles.size() - 1 : 0);
    if (!webDriver.getWindowHandle().equals(handle)) {
      webDriver.switchTo().window(handle);
    }
  }

  public static Set<String> getWindowHandles() {
    return webDriver.getWindowHandles();
  }

  public static String getCurrentUrl() {
    return webDriver.getCurrentUrl();
  }

  public static Object executeScript(String script, Object... args) {
    if (webDriver == null) {
      return null;
    }

    // Update the list with window handles
    updateHandlesList();
    // Choose first or last tab, depending on user prefs
    activate();

    try {
      return webDriver.executeScript(script, args);
    }
    catch (NoSuchWindowException nswe) {
      return null;
    }
  }
}
