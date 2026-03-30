/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.util;

import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.alayer.Finder;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.action.WdActionRoles;
import org.testar.webdriver.alayer.WdCanvasDimensions;
import org.testar.webdriver.state.WdScreenshot;
import org.testar.webdriver.tag.WdTags;

import static org.testar.webdriver.util.WdConstants.scrollThick;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;
import java.util.Objects;

public class WdScreenshotUtil {

    public static String getActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        if (action.get(Tags.Role, ActionRoles.Action).isA(WdActionRoles.RemoteAction)) {
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
            if (shape == null) {
                continue;
            }

            Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
            actionArea = actionArea.union(r);
        }

        if (actionArea.isEmpty()) {
            return "";
        }

        if (actionArea.x < 0 || actionArea.y < 0
                || actionArea.x + actionArea.width > WdCanvasDimensions.getCanvasWidth()
                || actionArea.y + actionArea.height > WdCanvasDimensions.getCanvasHeight()) {
            return "";
        }

        Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
        AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, 0L));
        return ScreenshotSerialiser.saveActionshot(
                state.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                action.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                scrshot);
    }

    private static String getRemoteActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        if (action.get(Tags.OriginWidget) == null || action.get(Tags.OriginWidget).get(Tags.Shape) == null) {
            return "";
        }

        Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        Shape shape = action.get(Tags.OriginWidget).get(Tags.Shape);
        Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
        actionArea = actionArea.union(r);

        if (actionArea.isEmpty()) {
            return "";
        }

        if (actionArea.x < 0 || actionArea.y < 0
                || actionArea.x + actionArea.width > WdCanvasDimensions.getCanvasWidth()
                || actionArea.y + actionArea.height > WdCanvasDimensions.getCanvasHeight()) {
            return "";
        }

        Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
        AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, 0L));
        return ScreenshotSerialiser.saveActionshot(
                state.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                action.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                scrshot);
    }

    public static AWTCanvas getStateshotBinary(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        if (state.get(WdTags.WebVerticallyScrollable, null) == null
                && state.get(WdTags.WebHorizontallyScrollable, null) == null) {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return AWTCanvas.fromScreenshot(
                    Rect.from(screenRect.getX(), screenRect.getY(), screenRect.getWidth(), screenRect.getHeight()),
                    state.get(Tags.HWND, 0L),
                    AWTCanvas.StorageFormat.PNG,
                    1);
        }

        double width = WdCanvasDimensions.getCanvasWidth() + (state.get(WdTags.WebVerticallyScrollable, false) ? scrollThick : 0);
        double height = WdCanvasDimensions.getCanvasHeight() + (state.get(WdTags.WebHorizontallyScrollable, false) ? scrollThick : 0);
        Rect rect = Rect.from(0, 0, width, height);
        return WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, 0L));
    }
}
