/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.util;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.core.action.Action;

import java.util.Set;

public abstract class AbstractStateService {

    /**
     * This method updates the list of actions on an abstract state.
     * @param abstractState
     * @param actions
     */
    public static void updateAbstractStateActions(AbstractState abstractState, Set<Action> actions) {
        // we only add actions to the abstract state. We do not delete.
        for (AbstractAction action : ActionHelper.convertActionsToAbstractActions(actions)) {
            abstractState.addNewAction(action);
        }
    }

}
