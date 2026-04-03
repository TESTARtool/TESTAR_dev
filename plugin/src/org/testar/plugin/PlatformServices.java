/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.Assert;
import org.testar.core.action.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.statemodel.StateModelManager;

/**
 * Platform service bundle resolved by the orchestrator.
 */
public final class PlatformServices {

    private final SystemService systemService;
    private final StateService stateService;
    private final StateModelManager stateModelService;
    private final ActionDerivationService actionDerivationService;
    private final ActionExecutionService actionExecutionService;
    private final ActionResolver actionResolver;

    public PlatformServices(SystemService systemService,
                            StateService stateService,
                            StateModelManager stateModelService,
                            ActionDerivationService actionDerivationService,
                            ActionExecutionService actionExecutionService,
                            ActionResolver actionResolver) {
        this.systemService = Assert.notNull(systemService);
        this.stateService = Assert.notNull(stateService);
        this.stateModelService = Assert.notNull(stateModelService);
        this.actionDerivationService = Assert.notNull(actionDerivationService);
        this.actionExecutionService = Assert.notNull(actionExecutionService);
        this.actionResolver = Assert.notNull(actionResolver);
    }

    public SystemService systemService() {
        return systemService;
    }

    public StateService stateService() {
        return stateService;
    }

    public StateModelManager stateModelService() {
        return stateModelService;
    }

    public ActionDerivationService actionDerivationService() {
        return actionDerivationService;
    }

    public ActionExecutionService actionExecutionService() {
        return actionExecutionService;
    }

    public ActionResolver actionResolver() {
        return actionResolver;
    }
}
