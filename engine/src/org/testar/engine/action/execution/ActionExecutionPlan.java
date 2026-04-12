/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.execution;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

/**
 * Minimal composition contract for action execution orchestration.
 */
public final class ActionExecutionPlan {

    @FunctionalInterface
    public interface BeforeExecutionHook {

        void beforeExecute(SUT system, State state, Action action);
    }

    @FunctionalInterface
    public interface AfterExecutionHook {

        void afterExecute(SUT system, State state, Action action, boolean executed);
    }

    private final ActionExecutionService actionExecutionService;
    private final List<BeforeExecutionHook> beforeExecutionHooks;
    private final List<AfterExecutionHook> afterExecutionHooks;

    public ActionExecutionPlan(ActionExecutionService actionExecutionService,
                               List<BeforeExecutionHook> beforeExecutionHooks,
                               List<AfterExecutionHook> afterExecutionHooks) {
        this.actionExecutionService = Assert.notNull(actionExecutionService);
        this.beforeExecutionHooks = Collections.unmodifiableList(Assert.notNull(beforeExecutionHooks));
        this.afterExecutionHooks = Collections.unmodifiableList(Assert.notNull(afterExecutionHooks));
    }

    public static ActionExecutionPlan basic(ActionExecutionService actionExecutionService) {
        return new ActionExecutionPlan(
                actionExecutionService,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public ActionExecutionService actionExecutionService() {
        return actionExecutionService;
    }

    public List<BeforeExecutionHook> beforeExecutionHooks() {
        return beforeExecutionHooks;
    }

    public List<AfterExecutionHook> afterExecutionHooks() {
        return afterExecutionHooks;
    }
}
