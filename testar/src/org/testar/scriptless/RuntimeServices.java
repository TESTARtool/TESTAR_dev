/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.Assert;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.StateModelManager;

/**
 * TESTAR-side scriptless runtime services used by the composed protocol
 * architecture.
 */
public final class RuntimeServices {

    private final SystemService systemService;
    private final StateService stateService;
    private final StateModelManager stateModelManager;
    private final ActionDerivationService actionDerivationService;
    private final ActionSelectorService actionSelectorService;
    private final ActionResolver actionResolver;
    private final ActionExecutionService actionExecutionService;
    private final SessionReportingManager sessionReportingManager;

    public RuntimeServices(SystemService systemService,
                                             StateService stateService,
                                             StateModelManager stateModelManager,
                                             ActionDerivationService actionDerivationService,
                                             ActionSelectorService actionSelectorService,
                                             ActionResolver actionResolver,
                                             ActionExecutionService actionExecutionService,
                                             SessionReportingManager sessionReportingManager) {
        this.systemService = Assert.notNull(systemService);
        this.stateService = Assert.notNull(stateService);
        this.stateModelManager = Assert.notNull(stateModelManager);
        this.actionDerivationService = Assert.notNull(actionDerivationService);
        this.actionSelectorService = Assert.notNull(actionSelectorService);
        this.actionResolver = Assert.notNull(actionResolver);
        this.actionExecutionService = Assert.notNull(actionExecutionService);
        this.sessionReportingManager = Assert.notNull(sessionReportingManager);
    }

    public static RuntimeServices fromPlatformServices(PlatformServices platformServices,
                                                               SessionReportingManager sessionReportingManager) {
        Assert.notNull(platformServices);

        return new RuntimeServices(
                platformServices.systemService(),
                platformServices.stateService(),
                platformServices.stateModelService(),
                platformServices.actionDerivationService(),
                platformServices.actionSelectorService(),
                platformServices.actionResolver(),
                platformServices.actionExecutionService(),
                sessionReportingManager
        );
    }

    public SystemService systemService() {
        return systemService;
    }

    public StateService stateService() {
        return stateService;
    }

    public StateModelManager stateModelManager() {
        return stateModelManager;
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

    public SessionReportingManager sessionReportingManager() {
        return sessionReportingManager;
    }
}
