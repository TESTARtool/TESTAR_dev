/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli.profile;

import java.util.List;

import org.testar.config.composition.CompositionDescriptor;
import org.testar.config.composition.CompositionLoader;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionIdentifierService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.StateIdentifierService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.service.DefaultActionIdentifierService;
import org.testar.engine.service.DefaultStateIdentifierService;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.configuration.PolicySessionConfiguration;
import org.testar.plugin.configuration.ServiceSessionConfiguration;

public final class CliProfileConfiguration {

    private final String profileName;
    private final Settings settings;
    private final PolicySessionConfiguration policyConfiguration;
    private final ServiceSessionConfiguration serviceConfiguration;
    private final CompositionDescriptor compositionDescriptor;
    private final List<String> compatibilityWarnings;

    public CliProfileConfiguration(String profileName,
                                   Settings settings,
                                   PolicySessionConfiguration policyConfiguration,
                                   ServiceSessionConfiguration serviceConfiguration,
                                   CompositionDescriptor compositionDescriptor,
                                   List<String> compatibilityWarnings) {
        this.profileName = Assert.notNull(profileName);
        this.settings = Assert.notNull(settings);
        this.policyConfiguration = Assert.notNull(policyConfiguration);
        this.serviceConfiguration = Assert.notNull(serviceConfiguration);
        this.compositionDescriptor = Assert.notNull(compositionDescriptor);
        this.compatibilityWarnings = List.copyOf(Assert.notNull(compatibilityWarnings));
    }

    public String profileName() {
        return profileName;
    }

    public Settings settings() {
        return settings;
    }

    public PolicySessionConfiguration policyConfiguration() {
        return policyConfiguration;
    }

    public ServiceSessionConfiguration serviceConfiguration() {
        return serviceConfiguration;
    }

    public List<String> compatibilityWarnings() {
        return compatibilityWarnings;
    }

    public PlatformServices applyServiceWrappers(PlatformServices services) {
        Assert.notNull(services);

        SystemService systemService = wrapSystemService(services.systemService());
        StateService stateService = wrapStateService(services.stateService());
        ActionDerivationService actionDerivationService = wrapActionDerivationService(services.actionDerivationService());
        ActionSelectorService actionSelectorService = wrapActionSelectorService(
                services.actionSelectorService(),
                services
        );
        ActionExecutionService actionExecutionService = wrapActionExecutionService(services.actionExecutionService());

        return new PlatformServices(
                systemService,
                stateService,
                services.oracleEvaluationService(),
                services.stateModelManager(),
                actionDerivationService,
                actionSelectorService,
                services.actionResolver(),
                actionExecutionService
        );
    }

    public StateIdentifierService stateIdentifierServiceOverride() {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stateIdentifierServiceClass(),
                compositionDescriptor.customCompositionResource(),
                StateIdentifierService.class,
                new DefaultStateIdentifierService(),
                new Object[]{settings},
                new Object[]{}
        );
    }

    public ActionIdentifierService actionIdentifierServiceOverride() {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionIdentifierServiceClass(),
                compositionDescriptor.customCompositionResource(),
                ActionIdentifierService.class,
                new DefaultActionIdentifierService(),
                new Object[]{settings},
                new Object[]{}
        );
    }

    private SystemService wrapSystemService(SystemService systemService) {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.systemServiceClass(),
                compositionDescriptor.customCompositionResource(),
                SystemService.class,
                systemService,
                new Object[]{settings},
                new Object[]{}
        );
    }

    private StateService wrapStateService(StateService stateService) {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stateServiceClass(),
                compositionDescriptor.customCompositionResource(),
                StateService.class,
                stateService,
                new Object[]{settings},
                new Object[]{}
        );
    }

    private ActionDerivationService wrapActionDerivationService(ActionDerivationService actionDerivationService) {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionDerivationServiceClass(),
                compositionDescriptor.customCompositionResource(),
                ActionDerivationService.class,
                actionDerivationService,
                new Object[]{settings},
                new Object[]{}
        );
    }

    private ActionSelectorService wrapActionSelectorService(ActionSelectorService actionSelectorService,
                                                            PlatformServices services) {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionSelectorServiceClass(),
                compositionDescriptor.customCompositionResource(),
                ActionSelectorService.class,
                actionSelectorService,
                new Object[]{services.stateModelManager(), settings},
                new Object[]{services.stateModelManager()},
                new Object[]{settings},
                new Object[]{}
        );
    }

    private ActionExecutionService wrapActionExecutionService(ActionExecutionService actionExecutionService) {
        return CompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionExecutionServiceClass(),
                compositionDescriptor.customCompositionResource(),
                ActionExecutionService.class,
                actionExecutionService,
                new Object[]{settings},
                new Object[]{}
        );
    }
}
