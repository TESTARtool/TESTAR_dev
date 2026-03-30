/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.Assert;
import org.testar.core.execution.StateService;
import org.testar.core.execution.SystemService;

/**
 * Platform service bundle resolved by the orchestrator.
 */
public final class PlatformServices {

    private final SystemService systemService;
    private final StateService stateService;

    public PlatformServices(SystemService systemService, StateService stateService) {
        this.systemService = Assert.notNull(systemService);
        this.stateService = Assert.notNull(stateService);
    }

    public SystemService systemService() {
        return systemService;
    }

    public StateService stateService() {
        return stateService;
    }
}
