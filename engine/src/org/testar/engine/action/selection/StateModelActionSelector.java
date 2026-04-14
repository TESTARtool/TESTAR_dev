/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.state.State;
import org.testar.statemodel.StateModelManager;

import java.util.Set;

/**
 * Action selector backed by the TESTAR state model manager.
 */
public final class StateModelActionSelector implements ActionSelectorService {

    private final StateModelManager stateModelManager;

    public StateModelActionSelector(StateModelManager stateModelManager) {
        this.stateModelManager = Assert.notNull(stateModelManager);
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        return stateModelManager.getAbstractActionToExecute(actions);
    }
}
