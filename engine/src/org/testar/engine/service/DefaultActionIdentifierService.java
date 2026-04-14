/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.service.ActionIdentifierService;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class DefaultActionIdentifierService implements ActionIdentifierService {

    @Override
    public Set<Action> identifyActions(State state, Set<Action> actions) {
        Assert.notNull(state, actions);

        if (actions.isEmpty()) {
            return actions;
        }

        Set<Action> widgetActions = new LinkedHashSet<>();
        for (Action action : actions) {
            if (hasOriginWidgetPath(action)) {
                widgetActions.add(action);
            } else {
                identifyEnvironmentAction(state, action);
            }
        }

        if (!widgetActions.isEmpty()) {
            CodingManager.buildIDs(state, widgetActions);
        }

        return actions;
    }

    @Override
    public Action identifyEnvironmentAction(State state, Action action) {
        CodingManager.buildEnvironmentActionIDs(Assert.notNull(state), Assert.notNull(action));
        return action;
    }

    private boolean hasOriginWidgetPath(Action action) {
        Widget originWidget = action.get(Tags.OriginWidget, null);
        return originWidget != null && originWidget.get(Tags.Path, null) != null;
    }
}
