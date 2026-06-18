/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.engine.policy.SessionPolicyContext;
import org.testar.plugin.configuration.PlatformDefaultSessionConfigurations;
import org.testar.plugin.configuration.PlatformDefaultServicePlans;
import org.testar.plugin.configuration.PlatformSessionAssembly;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.configuration.PolicySessionConfiguration;
import org.testar.plugin.configuration.SessionPolicyContextComposer;
import org.testar.plugin.configuration.SessionServiceComposer;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.plugin.policy.PlatformPolicyContexts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.core.CodingManager;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.plugin.exceptions.UnsupportedPlatformException;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.DummyModelManager;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelManagerFactory;
import org.testar.statemodel.StateModelStorageBootstrap;

/**
 * TESTAR platform/plugin orchestration
 */
public final class PlatformOrchestrator {

    private PlatformOrchestrator() {
    }

    public static PlatformServices resolve(PlatformSessionSpecification sessionSpec) {
        return resolve(
                sessionSpec,
                PolicySessionConfiguration.defaults(),
                ServiceSessionConfiguration.defaults()
        );
    }

    public static PlatformServices resolve(PlatformSessionSpecification sessionSpec,
                                           PolicySessionConfiguration policyConfiguration,
                                           ServiceSessionConfiguration serviceConfiguration) {
        return resolve(
                sessionSpec,
                policyConfiguration,
                serviceConfiguration,
                createStateModelManager(sessionSpec)
        );
    }

    public static PlatformServices resolve(PlatformSessionSpecification sessionSpec,
                                           PolicySessionConfiguration policyConfiguration,
                                           ServiceSessionConfiguration serviceConfiguration,
                                           StateModelManager stateModelManager) {
        configureNativePlatform(sessionSpec.getOperatingSystem());
        initializeCodingManager(sessionSpec.getSettings());

        // Resolve the platform-specific defaults first, then compose the final services.
        switch (sessionSpec.getOperatingSystem()) {
            case ANDROID:
                return android(sessionSpec, policyConfiguration, serviceConfiguration, stateModelManager);
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

    public static PlatformSession openCliSession(PlatformSessionSpecification sessionSpec) {
        // CLI opens a full platform session after resolving the shared services.
        return openSession(sessionSpec, resolve(sessionSpec));
    }

    public static PlatformSession openCliSession(PlatformSessionSpecification sessionSpec,
                                                 PolicySessionConfiguration policyConfiguration,
                                                 ServiceSessionConfiguration serviceConfiguration) {
        return openSession(sessionSpec, resolve(sessionSpec, policyConfiguration, serviceConfiguration));
    }

    public static PlatformSession openCliSession(PlatformSessionSpecification sessionSpec,
                                                 PlatformServices services) {
        return openSession(sessionSpec, services);
    }

    public static PlatformSession openSpySession(PlatformSessionSpecification sessionSpec) {
        // Spy Mode needs a live platform session without generating TESTAR reports.
        return openSession(sessionSpec, resolve(sessionSpec), false);
    }

    private static void configureNativePlatform(OperatingSystems operatingSystem) {
        // Reset platform flags before enabling the one required by this session.
        NativeLinker.cleanWdDriverOS();
        NativeLinker.cleanAndroidOS();

        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            NativeLinker.addWdDriverOS();
        } else if (operatingSystem == OperatingSystems.ANDROID) {
            NativeLinker.addAndroidOS();
        }
    }

    private static PlatformSession openSession(PlatformSessionSpecification sessionSpec, PlatformServices services) {
        return openSession(sessionSpec, services, true);
    }

    private static PlatformSession openSession(PlatformSessionSpecification sessionSpec,
                                               PlatformServices services,
                                               boolean reportingEnabled) {
        // Start the SUT first so reporting only begins for a live session.
        SUT system = services.systemService().startSystem();
        try {
            SessionReportingManager sessionReportingManager = reportingEnabled
                    ? SessionReportingManager.start(sessionSpec.getSettings(), sessionSpec.getTarget())
                    : SessionReportingManager.deferred(sessionSpec.getTarget());
            return new DefaultPlatformSession(services, system, sessionReportingManager, reportingEnabled);
        } catch (RuntimeException exception) {
            services.systemService().stopSystem(system);
            throw exception;
        }
    }

    public static State projectCliState(PlatformSessionSpecification sessionSpec, State state) {
        return projectCliState(sessionSpec, state, PolicySessionConfiguration.defaults());
    }

    public static State projectCliState(PlatformSessionSpecification sessionSpec,
                                        State state,
                                        PolicySessionConfiguration policyConfiguration) {
        // CLI state projection uses the same platform defaults, but only applies
        // the semantic state shaping step to an already captured state.
        initializeCodingManager(sessionSpec.getSettings());
        SessionPolicyContext sessionPolicyContext = buildSessionPolicyContext(sessionSpec, policyConfiguration);
        switch (sessionSpec.getOperatingSystem()) {
            case ANDROID:
                return PlatformDefaultSessionConfigurations
                        .androidSemanticStateCompositionPlan(sessionSpec)
                        .query(state, sessionPolicyContext);
            case WINDOWS:
            case WINDOWS_10:
                return PlatformDefaultSessionConfigurations
                        .windowsSemanticStateCompositionPlan(sessionSpec)
                        .query(state, sessionPolicyContext);
            case WEBDRIVER:
                return PlatformDefaultSessionConfigurations
                        .webdriverSemanticStateCompositionPlan(sessionSpec)
                        .query(state, sessionPolicyContext);
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported operating system for CLI projection: " + sessionSpec.getOperatingSystem()
                );
        }
    }

    private static PlatformServices windows(PlatformSessionSpecification sessionSpec,
                                            PolicySessionConfiguration policyConfiguration,
                                            ServiceSessionConfiguration serviceConfiguration,
                                            StateModelManager stateModelManager) {
        return composePlatformServices(
                sessionSpec,
                serviceConfiguration,
                stateModelManager,
                buildWindowsAssembly(sessionSpec, policyConfiguration)
        );
    }

    private static PlatformServices android(PlatformSessionSpecification sessionSpec,
                                            PolicySessionConfiguration policyConfiguration,
                                            ServiceSessionConfiguration serviceConfiguration,
                                            StateModelManager stateModelManager) {
        if (sessionSpec.getTargetType() != PlatformSessionSpecification.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported Android target type: " + sessionSpec.getTargetType()
            );
        }
        return composePlatformServices(
                sessionSpec,
                serviceConfiguration,
                stateModelManager,
                buildAndroidAssembly(sessionSpec, policyConfiguration)
        );
    }

    private static PlatformServices webdriver(PlatformSessionSpecification sessionSpec,
                                              PolicySessionConfiguration policyConfiguration,
                                              ServiceSessionConfiguration serviceConfiguration,
                                              StateModelManager stateModelManager) {
        if (sessionSpec.getTargetType() != PlatformSessionSpecification.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported WebDriver target type: " + sessionSpec.getTargetType()
            );
        }
        return composePlatformServices(
                sessionSpec,
                serviceConfiguration,
                stateModelManager,
                buildWebdriverAssembly(sessionSpec, policyConfiguration)
        );
    }

    private static SessionPolicyContext buildSessionPolicyContext(PlatformSessionSpecification sessionSpec) {
        return buildSessionPolicyContext(sessionSpec, PolicySessionConfiguration.defaults());
    }

    private static SessionPolicyContext buildSessionPolicyContext(PlatformSessionSpecification sessionSpec,
                                                                  PolicySessionConfiguration policyConfiguration) {
        // Build only the default policy context for cases that need projection without
        // resolving a full platform session.
        switch (sessionSpec.getOperatingSystem()) {
            case ANDROID:
                return buildAndroidPolicyContext(sessionSpec, policyConfiguration);
            case WINDOWS:
            case WINDOWS_10:
                return buildWindowsPolicyContext(sessionSpec, policyConfiguration);
            case WEBDRIVER:
                return buildWebdriverPolicyContext(sessionSpec, policyConfiguration);
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported operating system for policy context: " + sessionSpec.getOperatingSystem()
                );
        }
    }

    private static PlatformServices composePlatformServices(PlatformSessionSpecification sessionSpec,
                                                            ServiceSessionConfiguration serviceConfiguration,
                                                            StateModelManager stateModelManager,
                                                            PlatformSessionAssembly assembly) {
        // Compose the final session services from one platform assembly plus optional overrides.
        return SessionServiceComposer.compose(
                sessionSpec.getSettings(),
                assembly.sessionPolicyContext(),
                stateModelManager,
                serviceConfiguration,
                assembly.defaultServicePlans()
        );
    }

    private static PlatformSessionAssembly buildWindowsAssembly(PlatformSessionSpecification sessionSpec,
                                                                PolicySessionConfiguration policyConfiguration) {
        // Windows combines desktop policy defaults with the default Windows plan set.
        return new PlatformSessionAssembly(
                buildWindowsPolicyContext(sessionSpec, policyConfiguration),
                PlatformDefaultServicePlans.fromConfiguration(
                        PlatformDefaultSessionConfigurations.windowsServiceConfiguration(sessionSpec)
                )
        );
    }

    private static PlatformSessionAssembly buildAndroidAssembly(PlatformSessionSpecification sessionSpec,
                                                                PolicySessionConfiguration policyConfiguration) {
        // Android combines Android-specific policy defaults with the default Android plan set.
        return new PlatformSessionAssembly(
                buildAndroidPolicyContext(sessionSpec, policyConfiguration),
                PlatformDefaultServicePlans.fromConfiguration(
                        PlatformDefaultSessionConfigurations.androidServiceConfiguration(sessionSpec)
                )
        );
    }

    private static PlatformSessionAssembly buildWebdriverAssembly(PlatformSessionSpecification sessionSpec,
                                                                  PolicySessionConfiguration policyConfiguration) {
        // WebDriver combines browser policy defaults with the default WebDriver plan set.
        return new PlatformSessionAssembly(
                buildWebdriverPolicyContext(sessionSpec, policyConfiguration),
                PlatformDefaultServicePlans.fromConfiguration(
                        PlatformDefaultSessionConfigurations.webdriverServiceConfiguration(sessionSpec)
                )
        );
    }

    private static SessionPolicyContext buildWindowsPolicyContext(PlatformSessionSpecification sessionSpec,
                                                                  PolicySessionConfiguration policyConfiguration) {
        return SessionPolicyContextComposer.compose(
                PlatformPolicyContexts.desktopDefaults(sessionSpec.getSettings()),
                policyConfiguration
        );
    }

    private static SessionPolicyContext buildAndroidPolicyContext(PlatformSessionSpecification sessionSpec,
                                                                  PolicySessionConfiguration policyConfiguration) {
        return SessionPolicyContextComposer.compose(
                PlatformPolicyContexts.androidDefaults(sessionSpec.getSettings()),
                policyConfiguration
        );
    }

    private static SessionPolicyContext buildWebdriverPolicyContext(PlatformSessionSpecification sessionSpec,
                                                                    PolicySessionConfiguration policyConfiguration) {
        return SessionPolicyContextComposer.compose(
                PlatformPolicyContexts.webdriverDefaults(sessionSpec.getSettings()),
                policyConfiguration
        );
    }

    private static StateModelManager createStateModelManager(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();

        // Skip model initialization entirely when the state model is disabled.
        if (!settings.get(StateModelTags.StateModelEnabled , false)) {
            return new DummyModelManager();
        }

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
        // Bootstrap the local OrientDB storage only for the matching configuration.
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

    private static void initializeCodingManager(Settings settings) {
        CodingManager.initCodingManager(settings.get(ConfigTags.AbstractStateAttributes));
    }
}
