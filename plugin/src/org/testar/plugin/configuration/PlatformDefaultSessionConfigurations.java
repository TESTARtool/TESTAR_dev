/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.engine.manager.InputDataManager;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.action.selection.random.RandomActionSelector;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.resolver.DescriptionActionResolver;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.webdriver.action.execution.WebdriverActionExecutionPlan;
import org.testar.webdriver.action.derivation.WebdriverActionDerivationPlan;
import org.testar.webdriver.state.WebdriverStateCompositionPlan;
import org.testar.webdriver.system.WebdriverSystemCompositionPlan;
import org.testar.windows.action.execution.WindowsActionExecutionPlan;
import org.testar.windows.action.derivation.WindowsActionDerivationPlan;
import org.testar.windows.system.WindowsSystemCompositionPlan;
import org.testar.windows.tag.WindowsStateCompositionPlan;

/**
 * Factory helpers for platform default session configurations.
 */
public final class PlatformDefaultSessionConfigurations {

    private PlatformDefaultSessionConfigurations() {
    }

    public static SessionPolicyConfiguration defaultPolicyConfiguration() {
        return SessionPolicyConfiguration.defaults();
    }

    public static SessionServiceConfiguration windowsServiceConfiguration(PlatformSessionSpec sessionSpec) {
        Settings settings = sessionSpec.getSettings();
        SessionServiceConfiguration.Builder builder = SessionServiceConfiguration.builder();

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
                .overrideActionExecutionPlan(WindowsActionExecutionPlan.basic())
                .build();
    }

    public static SessionServiceConfiguration webdriverServiceConfiguration(PlatformSessionSpec sessionSpec) {
        Settings settings = sessionSpec.getSettings();

        return SessionServiceConfiguration.builder()
                .overrideSystemCompositionPlan(
                        WebdriverSystemCompositionPlan.fromSutConnector(sessionSpec.getTarget())
                )
                .overrideStateCompositionPlan(
                        WebdriverStateCompositionPlan.browser(
                                settings.get(ConfigTags.TimeToFreeze, 30.0)
                        )
                )
                .overrideActionDerivationPlan(
                        WebdriverActionDerivationPlan.create(
                                widget -> InputDataManager.getRandomTextInputData()
                        )
                )
                .overrideActionSelectorPlan(ActionSelectorPlan.basic(new RandomActionSelector()))
                .overrideActionResolverPlan(ActionResolverPlan.basic(new DescriptionActionResolver()))
                .overrideActionExecutionPlan(WebdriverActionExecutionPlan.basic())
                .build();
    }
}
