/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.engine.action.execution.DefaultActionExecutionService;
import org.testar.engine.manager.InputDataManager;
import org.testar.engine.action.DescriptionActionResolver;
import org.testar.engine.action.derivation.DesktopActionDerivationFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.core.CodingManager;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.engine.state.DefaultStateService;
import org.testar.plugin.exceptions.UnsupportedPlatformException;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.DummyModelManager;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelManagerFactory;
import org.testar.statemodel.StateModelStorageBootstrap;
import org.testar.webdriver.action.WebdriverActionDerivationFactory;
import org.testar.webdriver.action.policy.WebdriverClickablePolicy;
import org.testar.webdriver.action.policy.WebdriverScrollablePolicy;
import org.testar.webdriver.action.policy.WebdriverTypeablePolicy;
import org.testar.webdriver.service.WebdriverStateService;
import org.testar.webdriver.service.WebdriverSystemService;
import org.testar.windows.service.WindowsStateService;
import org.testar.windows.service.WindowsSystemService;
import org.testar.windows.action.policy.WindowsClickablePolicy;
import org.testar.windows.action.policy.WindowsScrollablePolicy;
import org.testar.windows.action.policy.WindowsTypeablePolicy;

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
        PlatformServices services = resolve(sessionSpec);
        return openSession(sessionSpec, services);
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
        SystemService systemService;
        switch (sessionSpec.getTargetType()) {
            case EXECUTABLE:
                systemService = WindowsSystemService.fromExecutable(
                        sessionSpec.getTarget(),
                        sessionSpec.getSettings().get(ConfigTags.ProcessListenerEnabled, false),
                        sessionSpec.getSettings().get(ConfigTags.SUTProcesses, "")
                );
                break;
            case PROCESS_NAME:
                systemService = WindowsSystemService.fromProcessName(sessionSpec.getTarget());
                break;
            case PROCESS_ID:
                systemService = WindowsSystemService.fromProcessId(Long.parseLong(sessionSpec.getTarget()));
                break;
            case UWP:
                systemService = WindowsSystemService.fromExecutableUwp(sessionSpec.getTarget());
                break;
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported Windows target type: " + sessionSpec.getTargetType()
                );
        }

        return new PlatformServices(
                systemService,
                new DefaultStateService(
                        WindowsStateService.uiAutomation(
                                sessionSpec.getSettings().get(ConfigTags.TimeToFreeze, 30.0),
                                sessionSpec.getSettings().get(ConfigTags.AccessBridgeEnabled, false),
                                sessionSpec.getSettings().get(ConfigTags.SUTProcesses, "")
                        )
                ),
                createStateModelService(sessionSpec),
                DesktopActionDerivationFactory.create(
                        new WindowsClickablePolicy(),
                        new WindowsTypeablePolicy(),
                        new WindowsScrollablePolicy(),
                        sessionSpec.getSettings().get(ConfigTags.ProcessesToKillDuringTest, ""),
                        widget -> InputDataManager.getRandomTextInputData()
                ),
                new DefaultActionExecutionService(),
                new DescriptionActionResolver()
        );
    }

    private static PlatformServices webdriver(PlatformSessionSpec sessionSpec) {
        if (sessionSpec.getTargetType() != PlatformSessionSpec.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported WebDriver target type: " + sessionSpec.getTargetType()
            );
        }

        return new PlatformServices(
                WebdriverSystemService.fromSutConnector(sessionSpec.getTarget()),
                new DefaultStateService(
                        WebdriverStateService.browser(sessionSpec.getSettings().get(ConfigTags.TimeToFreeze, 30.0))
                ),
                createStateModelService(sessionSpec),
                WebdriverActionDerivationFactory.create(
                        new WebdriverClickablePolicy(),
                        new WebdriverTypeablePolicy(),
                        new WebdriverScrollablePolicy(),
                        widget -> InputDataManager.getRandomTextInputData()
                ),
                new DefaultActionExecutionService(),
                new DescriptionActionResolver()
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
