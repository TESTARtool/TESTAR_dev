/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.engine.action.selection;

import org.testar.core.action.Action;
import org.testar.core.execution.ActionSelectorService;
import org.testar.core.state.State;
import org.testar.engine.action.selection.random.RandomActionSelector;

import java.util.Set;

public class ActionSelectorProxy implements ActionSelectorService {

    private final Object selector;

    public ActionSelectorProxy(Object selector) {
        this.selector = selector;
        System.out.println("ActionSelector: " + this.selector.getClass().getSimpleName());
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        if (selector instanceof ActionSelectorService){
            return ((ActionSelectorService) selector).selectAction(state, actions);
        }
        System.out.println("ActionSelectorService not found, returning RandomActionSelector");
        return new RandomActionSelector().selectAction(state, actions);
    }
}
