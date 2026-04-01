/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */
package org.testar.engine.action.selection.prioritize;

import org.testar.core.action.Action;

import java.util.Set;

public class DerivedActions {

    private Set<Action> filteredActions;
    private Set<Action> availableActions;

    public DerivedActions(Set<Action> availableActions, Set<Action> filteredActions) {
        this.filteredActions = filteredActions;
        this.availableActions = availableActions;
    }

    public Set<Action> getFilteredActions() {
        return filteredActions;
    }

    public Set<Action> getAvailableActions() {
        return availableActions;
    }

    public void addAvailableAction(Action action){
        availableActions.add(action);
    }

    public void addFilteredAction(Action action){
        filteredActions.add(action);
    }
}
