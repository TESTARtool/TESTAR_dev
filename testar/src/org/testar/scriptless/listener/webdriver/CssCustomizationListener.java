/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.listener.webdriver;

import java.util.List;
import java.util.Set;

import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.TestarMode;
import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.Mouse;
import org.testar.core.devices.MouseButtons;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.util.Util;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.scriptless.RuntimeContext;
import org.testar.webdriver.state.WdElement;
import org.testar.webdriver.state.WdWidget;

public class CssCustomizationListener implements IEventListener {

    private final RuntimeContext runtimeContext;

    public CssCustomizationListener(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * Add additional TESTAR keyboard shortcuts in SPY mode to enable the CSS clickable customization of widgets.
     * @param key
     */
    @Override
    public void keyDown(KBKeys key) {
        Set<OperatingSystems> platform = NativeLinker.getPLATFORM_OS();
        if (platform.contains(OperatingSystems.WEBDRIVER) && runtimeContext.mode() == TestarMode.Spy) {
            State latestState = runtimeContext.latestState();
            Mouse mouse = runtimeContext.mouse();
            List<String> clickableClasses = runtimeContext.settings().get(ConfigTags.WebClickableClasses);

            if (key == KBKeys.VK_RIGHT) {
                try {
                    // Obtain the widget aimed with the mouse cursor
                    Widget w = Util.widgetFromPoint(latestState, mouse.cursor().x(), mouse.cursor().y());
                    // Add the widget web CSS class property as clickable
                    WdElement element = ((WdWidget) w).element;
                    for(String s : element.cssClasses) {
                        if(s != null && !s.isEmpty()) {
                            clickableClasses.add(s);
                        }
                    }
                    // And save the new CSS class property in the test.setting file
                    Util.saveToFile(runtimeContext.settings().toFileString(), TestarDirectories.getTestSettingsFile());
                } catch(Exception e) {
                    System.out.println("ERROR adding the widget from point: " + "x(" + mouse.cursor().x() + "), y("+ mouse.cursor().y() +")");
                }
            }

            if (key == KBKeys.VK_LEFT) {
                try {
                    // Obtain the widget aimed with the mouse cursor
                    Widget w = Util.widgetFromPoint(latestState, mouse.cursor().x(), mouse.cursor().y());
                    // Remove the widget web CSS class property from all clickables
                    WdElement element = ((WdWidget) w).element;
                    for(String s : element.cssClasses) {
                        if(s != null && !s.isEmpty()) {
                            clickableClasses.remove(s);
                        }
                    }
                    // And save the new CSS class property in the test.setting file
                    Util.saveToFile(runtimeContext.settings().toFileString(), TestarDirectories.getTestSettingsFile());
                } catch(Exception e) {
                    System.out.println("ERROR removing the widget from point: " + "x(" + mouse.cursor().x() + "), y("+ mouse.cursor().y() +")");
                }
            }
        }
    }

    @Override
    public void keyUp(KBKeys key) {
    }

    @Override
    public void mouseDown(MouseButtons btn, double x, double y) {
    }

    @Override
    public void mouseUp(MouseButtons btn, double x, double y) {
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}
