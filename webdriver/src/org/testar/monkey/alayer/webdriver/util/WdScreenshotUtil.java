/**
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2026 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.webdriver.util;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.WdActionRoles;
import org.testar.monkey.alayer.webdriver.CanvasDimensions;
import org.testar.monkey.alayer.webdriver.WdScreenshot;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.serialisation.ScreenshotSerialiser;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class WdScreenshotUtil {

    public static String getActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        if(action.get(Tags.Role, ActionRoles.Action).isA(WdActionRoles.RemoteAction)) {
            return getRemoteActionshot(state, action);
        }

        List<Finder> targets = action.get(Tags.Targets, null);
        if (targets == null) {
            return "";
        }

        Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Finder f : targets) {
            Widget widget = f.apply(state);
            Shape shape = widget.get(Tags.Shape, null);
            if (shape == null)
                continue;

            Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
            actionArea = actionArea.union(r);
        }

        if (actionArea.isEmpty()) {
            return "";
        }

        // Actionarea is outside viewport
        if (actionArea.x < 0 || actionArea.y < 0 ||
                actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
                actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
            return "";
        }

        Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
        AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
        return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), action.get(Tags.ConcreteID, "NoConcreteIdAvailable"), scrshot);
    }

    private static String getRemoteActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        if(action.get(Tags.OriginWidget) == null || action.get(Tags.OriginWidget).get(Tags.Shape) == null) {
            return "";
        }

        Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        Shape shape = action.get(Tags.OriginWidget).get(Tags.Shape);
        Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
        actionArea = actionArea.union(r);

        if (actionArea.isEmpty()) {
            return "";
        }

        // Actionarea is outside viewport
        if (actionArea.x < 0 || actionArea.y < 0 ||
                actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
                actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
            return "";
        }

        Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
        AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
        return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), action.get(Tags.ConcreteID, "NoConcreteIdAvailable"), scrshot);
    }

    public static AWTCanvas getStateshotBinary(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        //If these State Tags are not obtained, the State has an error, use full monitor screen
        if(state.get(WdTags.WebVerticallyScrollable, null) == null 
                && state.get(WdTags.WebHorizontallyScrollable, null) == null) {
            //Get a screenshot of all the screen, because SUT ended and we can't obtain the size
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(screenRect.getX(), screenRect.getY(),
                    screenRect.getWidth(), screenRect.getHeight()), state.get(Tags.HWND, (long)0), AWTCanvas.StorageFormat.PNG, 1);
            return scrshot;
        }

        double width = CanvasDimensions.getCanvasWidth() + (state.get(WdTags.WebVerticallyScrollable, false) ? scrollThick : 0);
        double height = CanvasDimensions.getCanvasHeight() + (state.get(WdTags.WebHorizontallyScrollable, false) ? scrollThick : 0);
        Rect rect = Rect.from(0, 0, width, height);
        AWTCanvas screenshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
        return screenshot;
    }

}
