/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.statemodel.util.ActionHelper;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;

import java.util.Set;

abstract class AbstractStateFactory {

    /**
     * This builder method will create a new abstract state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @param actions a set of testar actions
     * @return the new abstract state
     */
    static AbstractState createAbstractState(State newState, Set<Action> actions) {
        String abstractStateId = newState.get(Tags.AbstractID);
        return new AbstractState(abstractStateId, ActionHelper.convertActionsToAbstractActions(actions));
    }

}
