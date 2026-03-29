/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.actionselector;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.ActionNotFoundException;

public interface ActionSelector {

    void notifyNewSequence();

    /**
     * This method returns an action to execute
     * @param currentState
     * @param abstractStateModel
     * @return
     */
    AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel)
            throws ActionNotFoundException;

}
