/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.core.action.Action;
import org.testar.core.state.State;

import java.util.Set;

public interface StateModelManager {

    void notifyNewStateReached(State newState, Set<Action> actions);

    void notifyActionExecution(Action action);

    void notifyTestingEnded();

    Action getAbstractActionToExecute(Set<Action> actions);

    void notifyTestSequencedStarted();

    void notifyTestSequenceStopped();

    void notifyTestSequenceInterruptedByUser();

    void notifyTestSequenceInterruptedBySystem(String message);

    String getModelIdentifier();

    String queryStateModel(String query, Object... params);
}
