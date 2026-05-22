/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import org.testar.android.action.derivation.AndroidActionDerivationPlan;
import org.testar.android.action.execution.AndroidActionExecutionPlan;
import org.testar.android.state.AndroidStateCompositionPlan;
import org.testar.android.system.AndroidSystemCompositionPlan;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.engine.manager.InputDataManager;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.action.selection.random.RandomActionSelector;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.resolver.DescriptionActionResolver;
import org.testar.engine.oracle.OracleEvaluationPlan;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.engine.service.DefaultOracleEvaluationService;
import org.testar.webdriver.action.execution.WebdriverActionExecutionPlan;
import org.testar.webdriver.action.derivation.WebdriverActionDerivationPlan;
import org.testar.webdriver.state.WebdriverStateCompositionPlan;
import org.testar.webdriver.system.WebdriverSystemCompositionPlan;
import org.testar.windows.action.execution.WindowsActionExecutionPlan;
import org.testar.windows.state.WindowsStateCompositionPlan;
import org.testar.windows.action.derivation.WindowsActionDerivationPlan;
import org.testar.windows.system.WindowsSystemCompositionPlan;

/**
 * Factory helpers for platform default session configurations.
 */
public final class PlatformDefaultSessionConfigurations {

    private PlatformDefaultSessionConfigurations() {
    }

    public static PolicySessionConfiguration defaultPolicyConfiguration() {
        return PolicySessionConfiguration.defaults();
    }

    // Build the full set of Windows default service plans for one session target.
    public static ServiceSessionConfiguration windowsServiceConfiguration(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();
        ServiceSessionConfiguration.Builder builder = ServiceSessionConfiguration.builder();

        switch (sessionSpec.getTargetType()) {
            case EXECUTABLE:
                builder.overrideSystemCompositionPlan(
                        WindowsSystemCompositionPlan.fromExecutable(
                                sessionSpec.getTarget(),
                                settings.get(ConfigTags.ProcessListenerEnabled, false),
                                settings.get(ConfigTags.SUTProcesses, ""),
                                settings.get(ConfigTags.StartupTime, 10.0),
                                settings.get(ConfigTags.TimeToFreeze, 30.0),
                                settings.get(ConfigTags.AccessBridgeEnabled, false)
                        )
                );
                break;
            case WINDOW_TITLE:
                builder.overrideSystemCompositionPlan(
                        WindowsSystemCompositionPlan.fromWindowTitle(
                                sessionSpec.getTarget(),
                                settings.get(ConfigTags.StartupTime, 10.0),
                                settings.get(ConfigTags.TimeToFreeze, 30.0),
                                settings.get(ConfigTags.AccessBridgeEnabled, false),
                                settings.get(ConfigTags.SUTProcesses, ""),
                                settings.get(ConfigTags.ForceForeground, false)
                        )
                );
                break;
            case PROCESS_NAME:
                builder.overrideSystemCompositionPlan(
                        WindowsSystemCompositionPlan.fromProcessName(sessionSpec.getTarget())
                );
                break;
            case PROCESS_ID:
                builder.overrideSystemCompositionPlan(
                        WindowsSystemCompositionPlan.fromProcessId(Long.parseLong(sessionSpec.getTarget()))
                );
                break;
            case UWP:
                builder.overrideSystemCompositionPlan(
                        WindowsSystemCompositionPlan.fromExecutableUwp(sessionSpec.getTarget())
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported Windows target type: " + sessionSpec.getTargetType());
        }

        return builder
                .overrideStateCompositionPlan(
                        WindowsStateCompositionPlan.uiAutomation(
                                settings.get(ConfigTags.TimeToFreeze, 30.0),
                                settings.get(ConfigTags.AccessBridgeEnabled, false),
                                settings.get(ConfigTags.SUTProcesses, "")
                        )
                )
                .overrideActionDerivationPlan(
                        WindowsActionDerivationPlan.create(
                                settings.get(ConfigTags.ProcessesToKillDuringTest, ""),
                                widget -> InputDataManager.getRandomTextInputData()
                        )
                )
                .overrideActionSelectorPlan(ActionSelectorPlan.basic(new RandomActionSelector()))
                .overrideActionResolverPlan(ActionResolverPlan.basic(new DescriptionActionResolver()))
                .overrideActionExecutionPlan(WindowsActionExecutionPlan.basic(
                        settings.get(ConfigTags.ActionDuration, 0.0d),
                        settings.get(ConfigTags.TimeToWaitAfterAction, 0.0d)
                ))
                .overrideOracleEvaluationPlan(OracleEvaluationPlan.basic(new DefaultOracleEvaluationService(settings)))
                .build();
    }

    // Build the full set of WebDriver default service plans for one session target.
    public static ServiceSessionConfiguration webdriverServiceConfiguration(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();

        return ServiceSessionConfiguration.builder()
                .overrideSystemCompositionPlan(
                        WebdriverSystemCompositionPlan.fromSettings(settings)
                )
                .overrideStateCompositionPlan(
                        WebdriverStateCompositionPlan.browser(
                                settings.get(ConfigTags.TimeToFreeze, 30.0)
                        )
                )
                .overrideActionDerivationPlan(
                        WebdriverActionDerivationPlan.create(
                                settings,
                                widget -> InputDataManager.getRandomTextInputData()
                        )
                )
                .overrideActionSelectorPlan(ActionSelectorPlan.basic(new RandomActionSelector()))
                .overrideActionResolverPlan(ActionResolverPlan.basic(new DescriptionActionResolver()))
                .overrideActionExecutionPlan(WebdriverActionExecutionPlan.basic(
                        settings.get(ConfigTags.ActionDuration, 0.0d),
                        settings.get(ConfigTags.TimeToWaitAfterAction, 0.0d)
                ))
                .overrideOracleEvaluationPlan(OracleEvaluationPlan.basic(new DefaultOracleEvaluationService(settings)))
                .build();
    }

    // Build the full set of Android default service plans for one session target.
    public static ServiceSessionConfiguration androidServiceConfiguration(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();

        return ServiceSessionConfiguration.builder()
                .overrideSystemCompositionPlan(
                        AndroidSystemCompositionPlan.fromSettings(settings)
                )
                .overrideStateCompositionPlan(
                        AndroidStateCompositionPlan.appium(
                                settings.get(ConfigTags.TimeToFreeze, 30.0)
                        )
                )
                .overrideActionDerivationPlan(
                        AndroidActionDerivationPlan.create(
                                widget -> InputDataManager.getRandomTextInputData(),
                                settings.get(ConfigTags.UseSystemActions, false)
                        )
                )
                .overrideActionSelectorPlan(ActionSelectorPlan.basic(new RandomActionSelector()))
                .overrideActionResolverPlan(ActionResolverPlan.basic(new DescriptionActionResolver()))
                .overrideActionExecutionPlan(AndroidActionExecutionPlan.basic(
                        settings.get(ConfigTags.ActionDuration, 0.0d),
                        settings.get(ConfigTags.TimeToWaitAfterAction, 0.0d)
                ))
                .overrideOracleEvaluationPlan(OracleEvaluationPlan.basic(new DefaultOracleEvaluationService(settings)))
                .build();
    }

    // Build the Windows semantic projection used when CLI wants a readable state view.
    public static StateCompositionPlan windowsSemanticStateCompositionPlan(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();
        return WindowsStateCompositionPlan.uiAutomationSemanticWidgets(
                settings.get(ConfigTags.TimeToFreeze, 30.0),
                settings.get(ConfigTags.AccessBridgeEnabled, false),
                settings.get(ConfigTags.SUTProcesses, "")
        );
    }

    // Build the WebDriver semantic projection used when CLI wants a readable state view.
    public static StateCompositionPlan webdriverSemanticStateCompositionPlan(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();
        return WebdriverStateCompositionPlan.browserSemanticWidgets(
                settings.get(ConfigTags.TimeToFreeze, 30.0)
        );
    }

    // Build the Android semantic projection used when CLI wants a readable state view.
    public static StateCompositionPlan androidSemanticStateCompositionPlan(PlatformSessionSpecification sessionSpec) {
        Settings settings = sessionSpec.getSettings();
        return AndroidStateCompositionPlan.appiumSemanticWidgets(
                settings.get(ConfigTags.TimeToFreeze, 30.0)
        );
    }
}
