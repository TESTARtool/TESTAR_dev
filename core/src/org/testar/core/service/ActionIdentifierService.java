/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.service;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.State;

public interface ActionIdentifierService {

    Set<Action> identifyActions(State state, Set<Action> actions);

    Action identifyEnvironmentAction(State state, Action action);
}
