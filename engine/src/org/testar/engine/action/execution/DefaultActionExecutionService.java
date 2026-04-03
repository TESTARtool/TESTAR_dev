/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.execution;

import org.testar.core.action.Action;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

/**
 * Default reusable action execution service.
 */
public final class DefaultActionExecutionService implements ActionExecutionService {

    private static final double DEFAULT_ACTION_DURATION = 0.0d;

    @Override
    public boolean executeAction(SUT system, State state, Action action) {
        try {
            action.run(system, state, DEFAULT_ACTION_DURATION);
            return true;
        } catch (ActionFailedException exception) {
            return false;
        }
    }
}
