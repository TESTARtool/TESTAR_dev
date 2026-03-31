/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.util;

import java.io.IOException;
import java.util.Objects;

import org.testar.android.AndroidAppiumFramework;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.ScreenshotUtil;

public class AndroidScreenshotUtil {

    private AndroidScreenshotUtil() {
    }

    public static String getActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        try {
            return AndroidAppiumFramework.getScreenshotAction(state, action);
        } catch(Exception e) {
            System.err.println("Exception when taking action screenshot: " + e);
        }

        return "";
    }

    public static String getStateshotSpyMode(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        try {
            return AndroidAppiumFramework.getScreenshotSpyMode(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        } catch(Exception e) {
            System.err.println("Exception occured when trying to take a screenshot of the Android emulator: " + e);
        }

        return "";
    }

    /**
     * Method returns a binary representation of a state's screenshot.
     * @param state
     * @return
     */
    public static AWTCanvas getStateshotBinary(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        try {
            return AndroidAppiumFramework.getScreenshotBinary(state);
        } catch (IOException e) {
            System.err.println("Exception occured when trying to take a binary screenshot of the Android emulator: " + e);
        }

        return ScreenshotUtil.getStateshotBinary(state);
    }

}
