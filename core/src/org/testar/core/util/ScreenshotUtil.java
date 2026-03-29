/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
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
 *******************************************************************************************************/

package org.testar.core.util;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.action.Action;
import org.testar.core.alayer.Finder;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.state.Widget;

public class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    /**
     * Method returns a binary representation of a state's screenshot.
     * 
     * @param state
     * @return
     */
    public static AWTCanvas getStateshotBinary(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        Shape viewPort = null;
        if (state.childCount() > 0) {
            viewPort = state.child(0).get(Tags.Shape, null);
            if (viewPort != null && (viewPort.width() * viewPort.height() < 1)) {
                viewPort = null;
            }
        }

        // If the state Shape is not properly obtained, or the State has an error, use
        // full monitor screen
        List<Verdict> verdicts = state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
        if (viewPort == null || !Verdict.helperAreAllVerdictsOK(verdicts)) {
            viewPort = state.get(Tags.Shape, null); // get the SUT process canvas (usually, full monitor screen)
        }

        // Validate viewport dimensions before taking the screenshot
        if (viewPort == null || viewPort.width() <= 0 || viewPort.height() <= 0) {
            // If viewport is still null, get a screenshot of all the screen
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return AWTCanvas.fromScreenshot(
                    Rect.from(screenRect.x, screenRect.y, screenRect.width, screenRect.height),
                    getRootWindowHandle(state),
                    AWTCanvas.StorageFormat.PNG,
                    1);
        } else {
            // Capture and return the viewport screenshot
            return AWTCanvas.fromScreenshot(
                    Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()),
                    getRootWindowHandle(state),
                    AWTCanvas.StorageFormat.PNG,
                    1);
        }
    }

    public static String getActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        List<Finder> targets = action.get(Tags.Targets, null);
        if (targets != null) {
            Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

            for (Finder f : targets) {
                Widget w = f.apply(state);
                Shape s = w.get(Tags.Shape, null);
                if (s == null) {
                    continue;
                }

                Rectangle r = new Rectangle((int) s.x(), (int) s.y(), (int) s.width(), (int) s.height());
                actionArea = actionArea.union(r);
            }

            if (actionArea.isEmpty()) {
                return "";
            }

            AWTCanvas scrshot = AWTCanvas.fromScreenshot(
                    Rect.from(actionArea.x, actionArea.y, actionArea.width, actionArea.height),
                    getRootWindowHandle(state),
                    AWTCanvas.StorageFormat.PNG,
                    1);

            return ScreenshotSerialiser.saveActionshot(
                    state.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                    action.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                    scrshot);
        }
        return "";
    }

    private static long getRootWindowHandle(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        long windowHandle = 0;
        if (state.childCount() > 0) {
            windowHandle = state.child(0).get(Tags.HWND, 0L);
        }
        return windowHandle;
    }

}
