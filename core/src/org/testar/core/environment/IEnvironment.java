/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.environment;

public interface IEnvironment {

    /**
     * Get the display scale based on the windows handle.
     * @param windowHandle The handle of the window.
     * @return The scale of the display which shows the window, when the display could not be resolved 1.0 is returned.
     */
    double getDisplayScale(long windowHandle);
}
