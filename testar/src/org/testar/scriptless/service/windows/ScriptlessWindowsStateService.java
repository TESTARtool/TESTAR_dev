/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.windows;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.Assert;
import org.testar.core.alayer.Rect;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.screenshot.json.JsonUtils;
import org.testar.scriptless.RuntimeContext;
import org.testar.windows.tag.UIATags;

public class ScriptlessWindowsStateService implements StateService {

    private final StateService delegate;
    private final RuntimeContext runtimeContext;

    public ScriptlessWindowsStateService(StateService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);
        State state = delegate.getState(system);

        // For Qt applications, block elements outside the modal window
        if(state.childCount() > 0 && state.child(0).get(UIATags.UIAFrameworkId, "").equals("Qt")) {
            // Obtain the modal element. By default the main window
            Widget modalWindow = state.child(0);
            for(Widget w : state) {
                if (w.get(UIATags.UIAIsWindowModal, false)) {
                    modalWindow = w;
                    break; // exit loop once modal is found
                }
            }

            // If the modal element exists, mark elements outside modal as blocked
            if(modalWindow != null) {
                for(Widget w : state) {
                    if (!isQtElementVisibleOnModalScreen(w, modalWindow)) {
                        w.set(Tags.Blocked, true);
                    }
                }
            }
        }

        // Creating a JSON file with information about widgets and their location on the screenshot:
        if(runtimeContext.settings().get(ConfigTags.Mode) == TestarMode.Generate && runtimeContext.settings().get(ConfigTags.CreateWidgetInfoJsonFile)) {
            JsonUtils.createWidgetInfoJsonFile(state);
        }

        return state;
    }

    private boolean isQtElementVisibleOnModalScreen(Widget w, Widget modalWidget) {
        Rect elementRect = (Rect) w.get(Tags.Shape, Rect.from(0,0,0,0));
        Rect modalRect = (Rect) modalWidget.get(Tags.Shape, Rect.from(0,0,0,0));

        double elementRight = elementRect.x() + elementRect.width();
        double elementBottom = elementRect.y() + elementRect.height();
        double modalRight = modalRect.x() + modalRect.width();
        double modalBottom = modalRect.y() + modalRect.height();

        // Check if element is completely visible inside the modal
        return elementRect.x() >= modalRect.x() && 
                elementRight <= modalRight &&
                elementRect.y() >= modalRect.y() && 
                elementBottom <= modalBottom;
    }
}
