/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.execution;

import org.testar.core.Assert;
import org.testar.core.service.ActionExecutionService;

/**
 * Orchestrator wrapper for the configured action execution service.
 */
public final class ActionExecutionPlan {

    private final ActionExecutionService actionExecutionService;

    public ActionExecutionPlan(ActionExecutionService actionExecutionService) {
        this.actionExecutionService = Assert.notNull(actionExecutionService);
    }

    public static ActionExecutionPlan basic(ActionExecutionService actionExecutionService) {
        return new ActionExecutionPlan(actionExecutionService);
    }

    public ActionExecutionService actionExecutionService() {
        return actionExecutionService;
    }
}
