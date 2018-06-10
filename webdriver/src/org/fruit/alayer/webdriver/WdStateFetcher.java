/*
 * Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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

/*
 *  @author Sebastian Bauersfeld
 *  @author Urko Rueda (refactor from UIAStateBuilder)
 */
package org.fruit.alayer.webdriver;

import org.fruit.Util;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WdStateFetcher implements Callable<WdState> {
  private static Map<String, String> labelmap;

  private final RemoteWebDriver driver;
  private final SUT system;

  public WdStateFetcher(SUT system) {
    this.driver = ((WdDriver) system).getRemoteWebDriver();
    this.system = system;
  }

  @SuppressWarnings("unchecked")
  public static WdRootElement buildRoot(SUT system) {
    Map<String, Object> packedBody = (Map<String, Object>) WdDriver.executeScript(
        "return getStateTreeTestar(arguments[0])", Constants.ignoredTags);

    WdRootElement wdRoot = new WdRootElement(packedBody);
    wdRoot.isRunning = system.isRunning();
    wdRoot.timeStamp = System.currentTimeMillis();
    wdRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
    wdRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;
    wdRoot.pid = system.get(Tags.PID);
    wdRoot.windowHandles = WdDriver.getWindowHandles();

    return wdRoot;
  }

  public WdState call() {
    WdRootElement webRoot = buildSkeletton();

    WdState root = createWidgetTree(webRoot);
    root.set(Tags.Role, Roles.Process);
    root.set(Tags.NotResponding, false);

    for (Widget w : root) {
      w.set(Tags.Path, Util.indexString(w));
    }

    return root;
  }

  /**
   * @return WdRootElement the root element of the page
   */
  private WdRootElement buildSkeletton() {
    // TODO Incorparate in building of the tree?
    labelmap = new HashMap<>();
    findAllLabels(labelmap);

    WdRootElement rootElement = buildRoot(system);
    system.set(Tags.Desc, rootElement.documentTitle);

    return rootElement;
  }

  private void findAllLabels(Map<String, String> labelmap) {
    // TODO Add to Chrome extension?

    List<WebElement> labelElements = driver.findElementsByTagName("label");
    for (WebElement labelElement : labelElements) {
      String target = labelElement.getAttribute("for");
      String label = labelElement.getText();

      if (target != null && !"null".equals(target) &&
          !target.isEmpty() && !label.isEmpty()) {
        labelmap.put(target, label);
      }
    }

    List<WebElement> iframeElements = driver.findElementsByTagName("iframe");
    for (WebElement iframeElement : iframeElements) {
      driver.switchTo().frame(iframeElement);
      findAllLabels(labelmap);
      driver.switchTo().parentFrame();
    }
  }

  public static Map<String, String> getLabelmap() {
    return labelmap;
  }

  private WdState createWidgetTree(WdRootElement root) {
    WdState state = new WdState(root);
    root.backRef = state;
    for (WdElement childElement : root.children) {
      if (!childElement.ignore) {
        createWidgetTree(state, childElement);
      }
    }
    return state;
  }

  private void createWidgetTree(WdWidget parent, WdElement element) {
    // TODO Needed?
    if (!element.enabled) {
      return;
    }

    WdWidget w = parent.root().addChild(parent, element);
    element.backRef = w;
    for (WdElement child : element.children) {
      createWidgetTree(w, child);
    }
  }
}
