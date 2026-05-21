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
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.StateModelManager;

public final class TestingServices {

    private final SystemService systemService;
    private final StateService stateService;
    private final OracleEvaluationService oracleEvaluationService;
    private final StateModelManager stateModelManager;
    private final ActionDerivationService actionDerivationService;
    private final ActionSelectorService actionSelectorService;
    private final ActionResolver actionResolver;
    private final ActionExecutionService actionExecutionService;
    private final SessionReportingManager sessionReportingManager;

    private TestingServices(Builder builder) {
        this.systemService = Assert.notNull(builder.systemService);
        this.stateService = Assert.notNull(builder.stateService);
        this.oracleEvaluationService = Assert.notNull(builder.oracleEvaluationService);
        this.stateModelManager = Assert.notNull(builder.stateModelManager);
        this.actionDerivationService = Assert.notNull(builder.actionDerivationService);
        this.actionSelectorService = Assert.notNull(builder.actionSelectorService);
        this.actionResolver = Assert.notNull(builder.actionResolver);
        this.actionExecutionService = Assert.notNull(builder.actionExecutionService);
        this.sessionReportingManager = Assert.notNull(builder.sessionReportingManager);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TestingServices fromPlatformServices(PlatformServices platformServices, SessionReportingManager sessionReportingManager) {
        Assert.notNull(platformServices);

        return builder()
                .withSystemService(platformServices.systemService())
                .withStateService(platformServices.stateService())
                .withOracleEvaluationService(platformServices.oracleEvaluationService())
                .withStateModelManager(platformServices.stateModelManager())
                .withActionDerivationService(platformServices.actionDerivationService())
                .withActionSelectorService(platformServices.actionSelectorService())
                .withActionResolver(platformServices.actionResolver())
                .withActionExecutionService(platformServices.actionExecutionService())
                .withSessionReportingManager(sessionReportingManager)
                .build();
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

    public static final class Builder {

        private SystemService systemService;
        private StateService stateService;
        private OracleEvaluationService oracleEvaluationService;
        private StateModelManager stateModelManager;
        private ActionDerivationService actionDerivationService;
        private ActionSelectorService actionSelectorService;
        private ActionResolver actionResolver;
        private ActionExecutionService actionExecutionService;
        private SessionReportingManager sessionReportingManager;

        public Builder withSystemService(SystemService systemService) {
            this.systemService = systemService;
            return this;
        }

        public Builder withStateService(StateService stateService) {
            this.stateService = stateService;
            return this;
        }

        public Builder withOracleEvaluationService(OracleEvaluationService oracleEvaluationService) {
            this.oracleEvaluationService = oracleEvaluationService;
            return this;
        }

        public Builder withStateModelManager(StateModelManager stateModelManager) {
            this.stateModelManager = stateModelManager;
            return this;
        }

        public Builder withActionDerivationService(ActionDerivationService actionDerivationService) {
            this.actionDerivationService = actionDerivationService;
            return this;
        }

        public Builder withActionSelectorService(ActionSelectorService actionSelectorService) {
            this.actionSelectorService = actionSelectorService;
            return this;
        }

        public Builder withActionResolver(ActionResolver actionResolver) {
            this.actionResolver = actionResolver;
            return this;
        }

        public Builder withActionExecutionService(ActionExecutionService actionExecutionService) {
            this.actionExecutionService = actionExecutionService;
            return this;
        }

        public Builder withSessionReportingManager(SessionReportingManager sessionReportingManager) {
            this.sessionReportingManager = sessionReportingManager;
            return this;
        }

        public TestingServices build() {
            return new TestingServices(this);
        }
    }
}
