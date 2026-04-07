/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios.util;

import java.io.IOException;
import java.util.Objects;

import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.ios.IOSAppiumFramework;
import org.testar.util.ScreenshotUtil;

public class IOSScreenshotUtil {

    private IOSScreenshotUtil() {
    }

    public static String getActionshot(State state, Action action) {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        try {
            return IOSAppiumFramework.getScreenshotAction(state, action);
        } catch(Exception e) {
            System.err.println("Exception when taking action screenshot: " + e);
        }

        return "";
    }

    public static String getStateshotSpyMode(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        try {
            return IOSAppiumFramework.getScreenshotSpyMode(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"));
        } catch(Exception e) {
            System.err.println("Exception occured when trying to take a screenshot of the iOS emulator: " + e);
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
            return IOSAppiumFramework.getScreenshotBinary(state);
        } catch (IOException e) {
            System.err.println("Exception occured when trying to take a binary screenshot of the iOS emulator: " + e);
        }

        return ScreenshotUtil.getStateshotBinary(state);
    }

}
