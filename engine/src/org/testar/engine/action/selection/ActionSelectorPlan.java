/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection;

import org.testar.core.Assert;
import org.testar.core.service.ActionSelectorService;
import org.testar.engine.action.selection.random.RandomActionSelector;

/**
 * Minimal composition contract for action selection orchestration.
 */
public final class ActionSelectorPlan {

    private final ActionSelectorService actionSelectorService;
    private final ActionSelectorService fallbackActionSelectorService;

    public ActionSelectorPlan(ActionSelectorService actionSelectorService,
                              ActionSelectorService fallbackActionSelectorService) {
        this.actionSelectorService = Assert.notNull(actionSelectorService);
        this.fallbackActionSelectorService = Assert.notNull(fallbackActionSelectorService);
    }

    public static ActionSelectorPlan basic(ActionSelectorService actionSelectorService) {
        return new ActionSelectorPlan(actionSelectorService, new RandomActionSelector());
    }

    public static ActionSelectorPlan withFallback(ActionSelectorService actionSelectorService,
                                                  ActionSelectorService fallbackActionSelectorService) {
        return new ActionSelectorPlan(actionSelectorService, fallbackActionSelectorService);
    }

    public ActionSelectorService actionSelectorService() {
        return actionSelectorService;
    }

    public ActionSelectorService fallbackActionSelectorService() {
        return fallbackActionSelectorService;
    }
}
