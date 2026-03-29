/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.screenshot;

import org.testar.core.alayer.AWTCanvas;
import org.testar.core.action.Action;
import org.testar.core.state.State;

public interface ScreenshotProvider {

    AWTCanvas getStateshotBinary(State state);

    String getActionshot(State state, Action action);

}
