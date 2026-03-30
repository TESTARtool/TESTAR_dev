/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.Assert;
import org.testar.core.execution.ActionDerivationService;
import org.testar.core.execution.ActionExecutionService;
import org.testar.core.execution.StateService;
import org.testar.core.execution.SystemService;

/**
 * Platform service bundle resolved by the orchestrator.
 */
public final class PlatformServices {

    private final SystemService systemService;
    private final StateService stateService;
    private final ActionDerivationService actionDerivationService;
    private final ActionExecutionService actionExecutionService;

    public PlatformServices(SystemService systemService,
                            StateService stateService,
                            ActionDerivationService actionDerivationService,
                            ActionExecutionService actionExecutionService) {
        this.systemService = Assert.notNull(systemService);
        this.stateService = Assert.notNull(stateService);
        this.actionDerivationService = Assert.notNull(actionDerivationService);
        this.actionExecutionService = Assert.notNull(actionExecutionService);
    }

    public SystemService systemService() {
        return systemService;
    }

    public StateService stateService() {
        return stateService;
    }

    public ActionDerivationService actionDerivationService() {
        return actionDerivationService;
    }

    public ActionExecutionService actionExecutionService() {
        return actionExecutionService;
    }
}
