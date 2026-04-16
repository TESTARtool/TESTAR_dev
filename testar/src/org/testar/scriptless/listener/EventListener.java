/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.listener;

import org.testar.core.Assert;
import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.MouseButtons;

public final class EventListener implements IEventListener {

    private final ModeListener modeListener;
    private final VisualizationListener visualizationListener;

    public EventListener(ModeListener modeListener, VisualizationListener visualizationListener) {
        this.modeListener = Assert.notNull(modeListener);
        this.visualizationListener = Assert.notNull(visualizationListener);
    }

    @Override
    public void keyDown(KBKeys key) {
        modeListener.keyDown(key);
        visualizationListener.keyDown(key);
    }

    @Override
    public void keyUp(KBKeys key) {
        modeListener.keyUp(key);
        visualizationListener.keyUp(key);
    }

    @Override
    public void mouseDown(MouseButtons btn, double x, double y) {
        modeListener.mouseDown(btn, x, y);
        visualizationListener.mouseDown(btn, x, y);
    }

    @Override
    public void mouseUp(MouseButtons btn, double x, double y) {
        modeListener.mouseUp(btn, x, y);
        visualizationListener.mouseUp(btn, x, y);
    }

    @Override
    public void mouseMoved(double x, double y) {
        modeListener.mouseMoved(x, y);
        visualizationListener.mouseMoved(x, y);
    }
}
