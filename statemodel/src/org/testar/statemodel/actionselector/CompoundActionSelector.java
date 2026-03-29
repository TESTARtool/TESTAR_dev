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

import java.util.List;

public class CompoundActionSelector implements ActionSelector {

    private final List<ActionSelector> selectors;

    public CompoundActionSelector(List<ActionSelector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public void notifyNewSequence() {
        selectors.forEach(selector -> selector.notifyNewSequence());
    }

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        // Iterating through the available action selectors - if the current one throws exception,
        // then the next action selector will be tried
        // If all of them fail, then throw an exception
        for (ActionSelector selector : selectors) {
            try {
                return selector.selectAction(currentState, abstractStateModel);
            } catch (ActionNotFoundException ex) {
                //@todo maybe some logging here later?
            }
        }
        throw new ActionNotFoundException();
    }
}
