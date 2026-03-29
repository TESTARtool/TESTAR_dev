/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.testar.core.exceptions.FruitException;

public final class AWTKeyboard implements Keyboard {

    public static AWTKeyboard build() throws FruitException {
        return new AWTKeyboard();
    }

    private final Robot robot;
    
    private AWTKeyboard() {
        try {
            robot = new Robot();
        } catch (AWTException awte) {
            throw new FruitException(awte);
        }
    }
    
    public String toString() {
        return "AWT Keyboard";
    }

    public void press(KBKeys k) {
        robot.keyPress(k.code());
    }

    public void release(KBKeys k) {
        robot.keyRelease(k.code());
    }

    public void paste() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
