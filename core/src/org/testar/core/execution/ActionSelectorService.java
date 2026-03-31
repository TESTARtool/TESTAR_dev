/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.execution;

import org.testar.core.action.Action;
import org.testar.core.state.State;

import java.util.Set;

public interface ActionSelectorService {
    Action selectAction(State state, Set<Action> actions);
}
