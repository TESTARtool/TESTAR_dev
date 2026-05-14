/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.util;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

public interface TriggerActionRuntime {

    State refreshState(SUT system);

    Set<Action> refreshActions(State state, Set<Action> actions);

    boolean executeTriggerAction(SUT system, State state, Action action);
}
