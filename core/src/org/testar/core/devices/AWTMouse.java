/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;

import org.testar.core.alayer.Point;
import org.testar.core.exceptions.FruitException;

public final class AWTMouse implements Mouse {

    public static AWTMouse build() throws FruitException {
        return new AWTMouse();
    }

    private final Robot robot;
    
    private double displayScale;

    private AWTMouse() throws FruitException {
        try {
            robot = new Robot();
            this.displayScale = 1.0;
        } catch (AWTException awte) {
            throw new FruitException(awte);
        }
    }
    
    public void setCursorDisplayScale(double displayScale) {
        this.displayScale = displayScale;
    }

    public String toString() {
        return "AWT Mouse";
    }

    public void press(MouseButtons k) {
        robot.mousePress(k.code());
    }

    public void release(MouseButtons k) {
        robot.mouseRelease(k.code());
    }

    public void setCursor(double x, double y) {
        robot.mouseMove((int) (x * displayScale), (int) (y * displayScale));
    }

    public Point cursor() {
        PointerInfo info = MouseInfo.getPointerInfo();
        if (info == null) {
            throw new RuntimeException("MouseInfo.getPointerInfo() returned null! This seeems to be undocumented Java library behavior... " +
                    "Consider using a platform specific Mouse Implementation instead of AWTMouse!");
        }
        java.awt.Point p = info.getLocation();
        Point ret = Point.from(p.x / displayScale, p.y / displayScale);
        return ret;
    }
}
