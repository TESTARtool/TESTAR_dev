/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.execution;

import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.engine.action.execution.BasicActionExecutionService;

/**
 * Reusable default action execution plan for Windows-driven desktop testing.
 */
public final class WindowsActionExecutionPlan {

    private WindowsActionExecutionPlan() {
    }

    public static ActionExecutionPlan basic() {
        return ActionExecutionPlan.basic(new BasicActionExecutionService());
    }
}
