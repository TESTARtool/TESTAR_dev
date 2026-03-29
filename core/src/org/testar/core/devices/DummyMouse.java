/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

import org.testar.core.alayer.Point;

/**
 *  Dummy Mouse for headless environments.
 */
public class DummyMouse implements Mouse {

    public static DummyMouse build() {
        return new DummyMouse();
    }

    private DummyMouse() {

    }

    @Override
    public void press(MouseButtons k) {

    }

    @Override
    public void release(MouseButtons k) {

    }

    @Override
    public void setCursor(double x, double y) {

    }

    @Override
    public Point cursor() {
        return Point.from(0, 0);
    }

    @Override
    public void setCursorDisplayScale(double displayScale) {

    }

    public String toString() {
        return "Dummy Mouse";
    }

}
