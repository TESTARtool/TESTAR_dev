/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.engine.policy.SessionPolicyContext;
import org.testar.plugin.configuration.PlatformDefaultSessionConfigurations;
import org.testar.plugin.configuration.SessionPolicyConfiguration;
import org.testar.plugin.configuration.SessionPolicyContextComposer;
import org.testar.plugin.configuration.SessionServiceComposer;
import org.testar.plugin.configuration.SessionServiceConfiguration;
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
import org.testar.windows.action.policy.WindowsClickablePolicy;
import org.testar.windows.action.policy.WindowsScrollablePolicy;
import org.testar.windows.action.policy.WindowsTypeablePolicy;

/**
 * TESTAR platform/plugin orchestration
 */
public final class PlatformOrchestrator {

    private PlatformOrchestrator() {
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec) {
        return resolve(
                sessionSpec,
                SessionPolicyConfiguration.defaults(),
                SessionServiceConfiguration.defaults()
        );
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec,
                                           SessionPolicyConfiguration policyConfiguration,
                                           SessionServiceConfiguration serviceConfiguration) {
        return resolve(
                sessionSpec,
                policyConfiguration,
                serviceConfiguration,
                createStateModelService(sessionSpec)
        );
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec,
                                           SessionPolicyConfiguration policyConfiguration,
                                           SessionServiceConfiguration serviceConfiguration,
                                           StateModelManager stateModelManager) {
        configureNativePlatform(sessionSpec.getOperatingSystem());

        switch (sessionSpec.getOperatingSystem()) {
            case WINDOWS:
            case WINDOWS_10:
                return windows(sessionSpec, policyConfiguration, serviceConfiguration, stateModelManager);
            case WEBDRIVER:
                return webdriver(sessionSpec, policyConfiguration, serviceConfiguration, stateModelManager);
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

    private static PlatformServices windows(PlatformSessionSpec sessionSpec,
                                            SessionPolicyConfiguration policyConfiguration,
                                            SessionServiceConfiguration serviceConfiguration,
                                            StateModelManager stateModelManager) {
        SessionServiceConfiguration defaultServiceConfiguration =
                PlatformDefaultSessionConfigurations.windowsServiceConfiguration(sessionSpec);
        SessionPolicyContext sessionPolicyContext = SessionPolicyContextComposer.compose(
                PlatformPolicyContexts.desktopDefaults(
                        new WindowsClickablePolicy(),
                        new WindowsTypeablePolicy(),
                        new WindowsScrollablePolicy()
                ),
                policyConfiguration
        );

        return SessionServiceComposer.compose(
                sessionPolicyContext,
                stateModelManager,
                serviceConfiguration,
                defaultServiceConfiguration.systemCompositionPlanOverride().orElseThrow(),
                defaultServiceConfiguration.stateCompositionPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionDerivationPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionSelectorPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionResolverPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionExecutionPlanOverride().orElseThrow()
        );
    }

    private static PlatformServices webdriver(PlatformSessionSpec sessionSpec,
                                              SessionPolicyConfiguration policyConfiguration,
                                              SessionServiceConfiguration serviceConfiguration,
                                              StateModelManager stateModelManager) {
        SessionServiceConfiguration defaultServiceConfiguration =
                PlatformDefaultSessionConfigurations.webdriverServiceConfiguration(sessionSpec);
        if (sessionSpec.getTargetType() != PlatformSessionSpec.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported WebDriver target type: " + sessionSpec.getTargetType()
            );
        }
        SessionPolicyContext sessionPolicyContext = SessionPolicyContextComposer.compose(
                PlatformPolicyContexts.webdriverDefaults(
                        sessionSpec.getSettings().get(ConfigTags.ClickableClasses, Collections.emptyList()),
                        sessionSpec.getSettings().get(ConfigTags.TypeableClasses, Collections.emptyList())
                ),
                policyConfiguration
        );

        return SessionServiceComposer.compose(
                sessionPolicyContext,
                stateModelManager,
                serviceConfiguration,
                defaultServiceConfiguration.systemCompositionPlanOverride().orElseThrow(),
                defaultServiceConfiguration.stateCompositionPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionDerivationPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionSelectorPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionResolverPlanOverride().orElseThrow(),
                defaultServiceConfiguration.actionExecutionPlanOverride().orElseThrow()
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
