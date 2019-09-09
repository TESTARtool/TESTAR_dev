/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

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

  public static WdScreenshot fromScreenshot(Rect r)
      throws StateBuildException {
    WdScreenshot wdScreenshot = new WdScreenshot();
    RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();

    try {
      File screenshot = webDriver.getScreenshotAs(OutputType.FILE);
      BufferedImage fullImg = ImageIO.read(screenshot);
      int x = (int) Math.max(0, r.x());
      int y = (int) Math.max(0, r.y());
      int width = (int) Math.min(fullImg.getWidth(), r.width());
      int height = (int) Math.min(fullImg.getHeight(), r.height());
      wdScreenshot.img = fullImg.getSubimage(x, y, width, height);
    }
    catch (Exception ignored) {

    }
    return wdScreenshot;
  }
}
