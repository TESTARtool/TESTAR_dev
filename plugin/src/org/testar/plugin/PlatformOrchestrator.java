/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.engine.manager.InputDataManager;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.action.selection.random.RandomActionSelector;
import org.testar.engine.service.ComposedActionDerivationService;
import org.testar.engine.service.ComposedActionExecutionService;
import org.testar.engine.service.ComposedActionResolver;
import org.testar.engine.service.ComposedActionSelectorService;
import org.testar.engine.service.ComposedStateService;
import org.testar.engine.service.ComposedSystemService;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.resolver.DescriptionActionResolver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.plugin.policy.PlatformPolicyContexts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.core.CodingManager;
import org.testar.core.state.SUT;
import org.testar.plugin.exceptions.UnsupportedPlatformException;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.DummyModelManager;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelManagerFactory;
import org.testar.statemodel.StateModelStorageBootstrap;
import org.testar.webdriver.action.execution.WebdriverActionExecutionPlan;
import org.testar.webdriver.action.derivation.WebdriverActionDerivationPlan;
import org.testar.webdriver.system.WebdriverSystemCompositionPlan;
import org.testar.webdriver.state.WebdriverStateCompositionPlan;
import org.testar.windows.action.execution.WindowsActionExecutionPlan;
import org.testar.windows.action.derivation.WindowsActionDerivationPlan;
import org.testar.windows.action.policy.WindowsClickablePolicy;
import org.testar.windows.action.policy.WindowsScrollablePolicy;
import org.testar.windows.action.policy.WindowsTypeablePolicy;
import org.testar.windows.system.WindowsSystemCompositionPlan;
import org.testar.windows.tag.WindowsStateCompositionPlan;

/**
 * TESTAR platform/plugin orchestration
 */
public final class PlatformOrchestrator {

    private PlatformOrchestrator() {
    }

    public static String moduleName() {
        return "plugin";
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec) {
        switch (sessionSpec.getOperatingSystem()) {
            case WINDOWS:
            case WINDOWS_7:
            case WINDOWS_10:
                return windows(sessionSpec);
            case WEBDRIVER:
                return webdriver(sessionSpec);
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported operating system: " + sessionSpec.getOperatingSystem()
                );
        }
    }

    public static PlatformSession openSession(PlatformSessionSpec sessionSpec) {
        configureNativePlatform(sessionSpec.getOperatingSystem());
        PlatformServices services = resolve(sessionSpec);
        return openSession(sessionSpec, services);
    }

    private static void configureNativePlatform(OperatingSystems operatingSystem) {
        NativeLinker.cleanWdDriverOS();
        NativeLinker.cleanAndroidOS();

        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            NativeLinker.addWdDriverOS();
        } else if (operatingSystem == OperatingSystems.ANDROID) {
            NativeLinker.addAndroidOS();
        }
    }

    private static PlatformSession openSession(PlatformSessionSpec sessionSpec, PlatformServices services) {
        SUT system = services.systemService().startSystem();
        try {
            SessionReportingManager sessionReportingManager = SessionReportingManager.start(
                    sessionSpec.getSettings(),
                    sessionSpec.getTarget()
            );
            return new DefaultPlatformSession(services, system, sessionReportingManager);
        } catch (RuntimeException exception) {
            services.systemService().stopSystem(system);
            throw exception;
        }
    }

    private static PlatformServices windows(PlatformSessionSpec sessionSpec) {
        ComposedSystemService systemService;
        switch (sessionSpec.getTargetType()) {
            case EXECUTABLE:
                systemService = ComposedSystemService.compose(
                        WindowsSystemCompositionPlan.fromExecutable(
                                sessionSpec.getTarget(),
                                sessionSpec.getSettings().get(ConfigTags.ProcessListenerEnabled, false),
                                sessionSpec.getSettings().get(ConfigTags.SUTProcesses, "")
                        )
                );
                break;
            case PROCESS_NAME:
                systemService = ComposedSystemService.compose(
                        WindowsSystemCompositionPlan.fromProcessName(sessionSpec.getTarget())
                );
                break;
            case PROCESS_ID:
                systemService = ComposedSystemService.compose(
                        WindowsSystemCompositionPlan.fromProcessId(Long.parseLong(sessionSpec.getTarget()))
                );
                break;
            case UWP:
                systemService = ComposedSystemService.compose(
                        WindowsSystemCompositionPlan.fromExecutableUwp(sessionSpec.getTarget())
                );
                break;
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported Windows target type: " + sessionSpec.getTargetType()
                );
        }
        SessionPolicyContext sessionPolicyContext = PlatformPolicyContexts.desktopDefaults(
                new WindowsClickablePolicy(),
                new WindowsTypeablePolicy(),
                new WindowsScrollablePolicy()
        );

        return new PlatformServices(
                systemService,
                ComposedStateService.compose(
                        sessionPolicyContext,
                        WindowsStateCompositionPlan.uiAutomation(
                                sessionSpec.getSettings().get(ConfigTags.TimeToFreeze, 30.0),
                                sessionSpec.getSettings().get(ConfigTags.AccessBridgeEnabled, false),
                                sessionSpec.getSettings().get(ConfigTags.SUTProcesses, "")
                        )
                ),
                createStateModelService(sessionSpec),
                ComposedActionDerivationService.compose(
                        sessionPolicyContext,
                        WindowsActionDerivationPlan.create(
                                sessionSpec.getSettings().get(ConfigTags.ProcessesToKillDuringTest, ""),
                                widget -> InputDataManager.getRandomTextInputData()
                        )
                ),
                ComposedActionSelectorService.compose(
                        ActionSelectorPlan.basic(new RandomActionSelector())
                ),
                ComposedActionResolver.compose(
                        ActionResolverPlan.basic(new DescriptionActionResolver())
                ),
                ComposedActionExecutionService.compose(
                    WindowsActionExecutionPlan.basic()
                )
        );
    }

    private static PlatformServices webdriver(PlatformSessionSpec sessionSpec) {
        if (sessionSpec.getTargetType() != PlatformSessionSpec.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported WebDriver target type: " + sessionSpec.getTargetType()
            );
        }
        SessionPolicyContext sessionPolicyContext = PlatformPolicyContexts.webdriverDefaults(
                sessionSpec.getSettings().get(ConfigTags.ClickableClasses, Collections.emptyList()),
                sessionSpec.getSettings().get(ConfigTags.TypeableClasses, Collections.emptyList())
        );

        return new PlatformServices(
                ComposedSystemService.compose(
                        WebdriverSystemCompositionPlan.fromSutConnector(sessionSpec.getTarget())
                ),
                ComposedStateService.compose(
                        sessionPolicyContext,
                        WebdriverStateCompositionPlan.browser(
                                sessionSpec.getSettings().get(ConfigTags.TimeToFreeze, 30.0)
                        )
                ),
                createStateModelService(sessionSpec),
                ComposedActionDerivationService.compose(
                        sessionPolicyContext,
                        WebdriverActionDerivationPlan.create(
                                widget -> InputDataManager.getRandomTextInputData()
                        )
                ),
                ComposedActionSelectorService.compose(
                        ActionSelectorPlan.basic(new RandomActionSelector())
                ),
                ComposedActionResolver.compose(
                        ActionResolverPlan.basic(new DescriptionActionResolver())
                ),
                ComposedActionExecutionService.compose(
                    WebdriverActionExecutionPlan.basic()
                )
        );
    }

    private static StateModelManager createStateModelService(PlatformSessionSpec sessionSpec) {
        Settings settings = sessionSpec.getSettings();

        if (!settings.get(StateModelTags.StateModelEnabled , false)) {
            return new DummyModelManager();
        }

        CodingManager.initCodingManager(settings.get(ConfigTags.AbstractStateAttributes));

        bootstrapStateModelStorage(settings);

        String modelName = settings.get(ConfigTags.ApplicationName, "");
        if(modelName.isEmpty()) {
            modelName = sessionSpec.getTarget().replaceAll("[^A-Za-z0-9]", "_");
        }
        String modelVersion = settings.get(ConfigTags.ApplicationVersion, "");
        if(modelVersion.isEmpty()) {
            modelVersion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        }

        return StateModelManagerFactory.getStateModelManager(modelName, modelVersion, settings);
    }

    private static void bootstrapStateModelStorage(Settings settings) {
        boolean stateModelEnabled = settings.get(StateModelTags.StateModelEnabled, false);
        String dataStore = settings.get(StateModelTags.DataStore, "");
        String dataStoreType = settings.get(StateModelTags.DataStoreType, "");

        if (!stateModelEnabled) {
            return;
        }

        if (!"OrientDB".equalsIgnoreCase(dataStore)) {
            return;
        }

        if (!"plocal".equalsIgnoreCase(dataStoreType)) {
            return;
        }

        StateModelStorageBootstrap.setupOrientDB(
                settings.get(StateModelTags.DataStoreDirectory, ""),
                settings.get(StateModelTags.DataStoreDB, ""),
                settings.get(StateModelTags.DataStoreUser, ""),
                settings.get(StateModelTags.DataStorePassword, "")
        );
    }
}
