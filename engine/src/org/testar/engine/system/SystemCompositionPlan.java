/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.system;

import org.testar.core.Assert;
import org.testar.core.service.SystemService;

/**
 * Orchestrator wrapper for the configured system lifecycle service.
 */
public final class SystemCompositionPlan {

    private final SystemService systemService;

    public SystemCompositionPlan(SystemService systemService) {
        this.systemService = Assert.notNull(systemService);
    }

    public static SystemCompositionPlan basic(SystemService systemService) {
        return new SystemCompositionPlan(systemService);
    }

    public SystemService systemService() {
        return systemService;
    }
}
