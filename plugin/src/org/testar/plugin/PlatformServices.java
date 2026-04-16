/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.Assert;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.statemodel.StateModelManager;

/**
 * Platform service bundle resolved by the orchestrator.
 */
public final class PlatformServices {

    private final SystemService systemService;
    private final StateService stateService;
    private final OracleEvaluationService oracleEvaluationService;
    private final StateModelManager stateModelService;
    private final ActionDerivationService actionDerivationService;
    private final ActionSelectorService actionSelectorService;
    private final ActionResolver actionResolver;
    private final ActionExecutionService actionExecutionService;

    public PlatformServices(SystemService systemService,
                            StateService stateService,
                            OracleEvaluationService oracleEvaluationService,
                            StateModelManager stateModelService,
                            ActionDerivationService actionDerivationService,
                            ActionSelectorService actionSelectorService,
                            ActionResolver actionResolver,
                            ActionExecutionService actionExecutionService) {
        this.systemService = Assert.notNull(systemService);
        this.stateService = Assert.notNull(stateService);
        this.oracleEvaluationService = Assert.notNull(oracleEvaluationService);
        this.stateModelService = Assert.notNull(stateModelService);
        this.actionDerivationService = Assert.notNull(actionDerivationService);
        this.actionSelectorService = Assert.notNull(actionSelectorService);
        this.actionResolver = Assert.notNull(actionResolver);
        this.actionExecutionService = Assert.notNull(actionExecutionService);
    }

    public SystemService systemService() {
        return systemService;
    }

    public StateService stateService() {
        return stateService;
    }

    public OracleEvaluationService oracleEvaluationService() {
        return oracleEvaluationService;
    }

    public StateModelManager stateModelService() {
        return stateModelService;
    }

    public ActionDerivationService actionDerivationService() {
        return actionDerivationService;
    }

    public ActionSelectorService actionSelectorService() {
        return actionSelectorService;
    }

    public ActionResolver actionResolver() {
        return actionResolver;
    }

    public ActionExecutionService actionExecutionService() {
        return actionExecutionService;
    }
}
