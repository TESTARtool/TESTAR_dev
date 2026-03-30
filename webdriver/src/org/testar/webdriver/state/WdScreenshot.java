/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.testar.core.environment.Environment;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.alayer.Rect;
import org.testar.core.exceptions.StateBuildException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Extend AWTCanvas to get the screenshot from WebDriver
 */
public class WdScreenshot extends AWTCanvas {

  private WdScreenshot() {
    // Dimensions are irrelevant
    super(1, 1, 1, 1);
  }

  public static WdScreenshot fromScreenshot(Rect r, long windowHandle)
      throws StateBuildException {
    WdScreenshot wdScreenshot = new WdScreenshot();
    RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();

    try {
      File screenshot = webDriver.getScreenshotAs(OutputType.FILE);
      BufferedImage fullImg = ImageIO.read(screenshot);
      double displayScale = Environment.getInstance().getDisplayScale(windowHandle);
      int x = (int) Math.max(0, r.x() * displayScale);
      int y = (int) Math.max(0, r.y() * displayScale);
      int width = (int) Math.min(fullImg.getWidth(), r.width() * displayScale);
      int height = (int) Math.min(fullImg.getHeight(), r.height() * displayScale);
      wdScreenshot.img = fullImg.getSubimage(x, y, width, height);
    }
    catch (Exception ignored) {

    }
    return wdScreenshot;
  }
}
