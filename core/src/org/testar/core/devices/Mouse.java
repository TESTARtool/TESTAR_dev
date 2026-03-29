/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

import org.testar.core.alayer.Point;

public interface Mouse {
    void press(MouseButtons k);
    void release(MouseButtons k);
    void setCursor(double x, double y);
    Point cursor();
    void setCursorDisplayScale(double displayScale);
}
