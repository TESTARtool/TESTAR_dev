package org.fruit.alayer.webdriver;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Rect;
import org.fruit.alayer.exceptions.StateBuildException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Extend AWTCanvas to get the screenshot from WebDriver
 */
public class WdScreenshot extends AWTCanvas {
  private WdScreenshot() {
    // Dimensions are irrelevant
    super(1, 1, 1, 1);
  }

  public static WdScreenshot fromScreenshot(RemoteWebDriver webDriver, Rect r)
      throws StateBuildException {
    WdScreenshot wdScreenshot = new WdScreenshot();

    try {
      File screenshot = webDriver.getScreenshotAs(OutputType.FILE);
      BufferedImage fullImg = ImageIO.read(screenshot);
      int x = (int) Math.max(0, r.x());
      int y = (int) Math.max(0, r.y());
      int width = (int) Math.min(fullImg.getWidth(), r.width());
      int height = (int) Math.min(fullImg.getHeight(),r.height());
      wdScreenshot.img = fullImg.getSubimage(x, y, width, height);
    }
    catch (WebDriverException | IOException e) {
      throw new StateBuildException("WebDriver not reachable");
    }
    return wdScreenshot;
  }
}
