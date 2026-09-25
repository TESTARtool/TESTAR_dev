/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdDriver;
import org.testar.core.action.Action;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;

public class WdHistoryBackAction extends TaggableBase implements Action {
    private static final long serialVersionUID = -4564430451637399460L;

    public WdHistoryBackAction() {
        this.set(Tags.Role, WdActionRoles.HistoryBackScript);
        this.set(Tags.Desc, "Execute Webdriver script to load the previous URL in the history list");
    }

    public WdHistoryBackAction(State state) {
        this();
        this.set(Tags.OriginWidget, state);
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        WdDriver.executeScript("window.history.back();");
    }

    @Override
    public String toShortString() {
        return "History back";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}
