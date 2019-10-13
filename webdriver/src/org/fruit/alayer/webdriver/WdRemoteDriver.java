package org.fruit.alayer.webdriver;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.fruit.alayer.exceptions.SystemStartException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

class WdRemoteDriver {
  private static String path = System.getProperty("user.dir");
  private static String extensionPath = path.substring(0, path.length() - 3) + "web-extension";

  static RemoteWebDriver startChromeDriver(String chromeDriverPath) {
    ChromeDriverService service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(chromeDriverPath))
        .usingAnyFreePort()
        .build();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("load-extension=" + extensionPath);
    options.addArguments("disable-infobars");

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 1);
    options.setExperimentalOption("prefs", prefs);

    return new ChromeDriver(service, options);
  }

  static RemoteWebDriver startGeckoDriver(String geckoDriverPath) {
    String nullPath = "/dev/null";
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      nullPath = "NUL";
    }

    GeckoDriverService service = new GeckoDriverService.Builder()
        .usingDriverExecutable(new File(geckoDriverPath))
        .withLogFile(new File(nullPath))
        .usingAnyFreePort()
        .build();

    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("dom.webnotifications.enabled", false);
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(profile);

    RemoteWebDriver webDriver = new FirefoxDriver(service, options);
    loadGeckoExtension(webDriver, extensionPath);

    return webDriver;
  }

  private static void loadGeckoExtension(RemoteWebDriver webDriver,
                                         String extensionPath) {
    // Create payload
    String payload = "{" +
                     "\"path\": \"" + extensionPath.replace("\\", "\\\\") + "\", " +
                     "\"temporary\": true }";
    StringEntity entity = new StringEntity(payload,
        ContentType.APPLICATION_FORM_URLENCODED);

    // Determine the endpoint
    HttpCommandExecutor ce = (HttpCommandExecutor) webDriver.getCommandExecutor();
    SessionId sessionId = webDriver.getSessionId();
    String urlString = String.format(
        "http://localhost:%s/session/%s/moz/addon/install",
        ce.getAddressOfRemoteServer().getPort(), sessionId);

    // POST payload to the endpoint
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost request = new HttpPost(urlString);
    request.setEntity(entity);
    try {
      httpClient.execute(request);
    }
    catch (IOException ioe) {
      throw new SystemStartException(ioe);
    }
  }

  static RemoteWebDriver startEdgeDriver(String edgeDriverPath) {
    // Edge can only load extensions from a system path
    String edgeSideLoadPath = copyExtension(extensionPath);

    EdgeDriverService service = new EdgeDriverService.Builder()
        .usingDriverExecutable(new File(edgeDriverPath))
        .usingAnyFreePort()
        .build();

    EdgeOptions options = new EdgeOptions();
    options.setCapability("extensionPaths", new String[]{edgeSideLoadPath});
    RemoteWebDriver webDriver = new EdgeDriver(service, options);

    closeDialog(webDriver);

    return webDriver;
  }

  private static String copyExtension(String extensionPath) {
    String appDataDir = System.getenv("LOCALAPPDATA");
    String extensionDir = new File(extensionPath).getName();
    String subDirs = "Packages\\Microsoft.MicrosoftEdge_8wekyb3d8bbwe\\LocalState";
    String edgeSideLoadPath = String.format("%s\\%s\\%s", appDataDir, subDirs, extensionDir);

    File sourceFolder = new File(extensionPath);
    File targetFolder = new File(edgeSideLoadPath);
    try {
      copyFolder(sourceFolder, targetFolder);
    }
    catch (IOException ioe) {
      throw new SystemStartException(ioe);
    }

    return edgeSideLoadPath;
  }

  private static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
    if (sourceFolder.isDirectory()) {
      if (!targetFolder.exists()) {
        targetFolder.mkdir();
      }

      for (String file : sourceFolder.list()) {
        File sourceFile = new File(sourceFolder, file);
        File targetFile = new File(targetFolder, file);
        copyFolder(sourceFile, targetFile);
      }
    }
    else {
      Files.copy(sourceFolder.toPath(), targetFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Edge has an annoying bug during the loading of the extension
   * This method closes the dialog without user action
   */
  private static void closeDialog(RemoteWebDriver webDriver) {
    // Fix window size/position, so we don't have to look for the button
    Dimension screenDimensions = new Dimension(800, 400);
    webDriver.manage().window().setSize(screenDimensions);
    Point screenPosition = new Point(0, 0);
    webDriver.manage().window().setPosition(screenPosition);

    try {
      Robot robot = new Robot();
      robot.mouseMove(745, 365);
      robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
      robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    catch (AWTException awte) {
      awte.printStackTrace();
    }
  }
}
