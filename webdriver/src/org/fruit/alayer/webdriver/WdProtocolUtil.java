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

import es.upv.staq.testar.ProtocolUtil;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import org.fruit.alayer.Shape;
import org.fruit.alayer.*;
import org.fruit.alayer.webdriver.enums.WdTags;

import java.awt.*;
import java.util.List;

import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class WdProtocolUtil extends ProtocolUtil {
  public WdProtocolUtil() {
  }

  @Override
  public String getStateshot(State state) {
    double width = CanvasDimensions.getCanvasWidth() + (
        state.get(WdTags.WebVerticallyScrollable) ? scrollThick : 0);
    double height = CanvasDimensions.getCanvasHeight() + (
        state.get(WdTags.WebHorizontallyScrollable) ? scrollThick : 0);
    Rect rect = Rect.from(0, 0, width, height);
    AWTCanvas screenshot = WdScreenshot.fromScreenshot(rect);
    return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID), screenshot);
  }

  @Override
  public String getActionshot(State state, Action action) {
    List<Finder> targets = action.get(Tags.Targets, null);
    if (targets == null) {
      return null;
    }

    Rectangle actionArea = new Rectangle(
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    for (Finder f : targets) {
      Widget widget = f.apply(state);
      Shape shape = widget.get(Tags.Shape);
      Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
      actionArea = actionArea.union(r);
    }
    if (actionArea.isEmpty()) {
      return null;
    }

    // Actionarea is outside viewport
    if (actionArea.x < 0 || actionArea.y < 0 ||
        actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
        actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
      return null;
    }

    Rect rect = Rect.from(
        actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
    AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect);
    return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), action.get(Tags.ConcreteID, "NoConcreteIdAvailable"), scrshot);
  }
  
  @Override
  public AWTCanvas getStateshotBinary(State state) {
	  double width = CanvasDimensions.getCanvasWidth() + (
			  state.get(WdTags.WebVerticallyScrollable) ? scrollThick : 0);
	  double height = CanvasDimensions.getCanvasHeight() + (
			  state.get(WdTags.WebHorizontallyScrollable) ? scrollThick : 0);
	  Rect rect = Rect.from(0, 0, width, height);
	  AWTCanvas screenshot = WdScreenshot.fromScreenshot(rect);
	  return screenshot;
  }
}