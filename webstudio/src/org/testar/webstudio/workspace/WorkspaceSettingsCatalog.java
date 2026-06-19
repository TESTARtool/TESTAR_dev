/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.workspace;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.core.tag.Tag;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

public final class WorkspaceSettingsCatalog {

    private WorkspaceSettingsCatalog() { }

    public static List<WorkspaceSettingsGroupDto> buildSettingsGroups(Properties settingsProperties) {
        return List.of(
            group(
                "sut-connection",
                "SUT Connection",
                "How TESTAR connects to and identifies the target System Under Test.",
                settingsProperties,
                ConfigTags.SUTConnector,
                ConfigTags.SUTConnectorValue,
                ConfigTags.ApplicationName,
                ConfigTags.ApplicationVersion
                //,
                //ConfigTags.SUTProcesses,
                //ConfigTags.AccessBridgeEnabled,
                //ConfigTags.ForceForeground
            ),
            group(
                "execution",
                "SUT Execution",
                "Sequence and action execution count and timing.",
                settingsProperties,
                ConfigTags.Sequences,
                ConfigTags.SequenceLength,
                ConfigTags.ActionDuration,
                ConfigTags.TimeToWaitAfterAction,
                ConfigTags.StartupTime,
                ConfigTags.MaxTime,
                ConfigTags.StopGenerationOnFault,
                ConfigTags.KeyBoardListener
                //,
                //ConfigTags.ShowVisualSettingsDialogOnStartup
            ),
            /*group(
                "scriptless",
                "Scriptless Composition",
                "Workspace and resource selection for scriptless composition and policies.",
                settingsProperties,
                ConfigTags.CompositionProfile,
                ConfigTags.CustomCompositionResource,
                ConfigTags.CustomPoliciesResource
            ),*/
            group(
                "reporting",
                "Reporting",
                "How TESTAR stores reports, logs, widget information, and generated outputs.",
                settingsProperties,
                //ConfigTags.OutputDir,
                //ConfigTags.TempDir,
                ConfigTags.ReportInHTML,
                ConfigTags.ReportInPlainText,
                ConfigTags.OnlySaveFaultySequences,
                ConfigTags.IgnoreDuplicatedVerdicts
                //,
                //ConfigTags.LogLevel,
                //ConfigTags.CreateWidgetInfoJsonFile
            ),
            group(
                "filters",
                "Filters",
                "Filtering rules applied during execution.",
                settingsProperties,
                ConfigTags.ClickFilter,
                ConfigTags.TagsToFilter,
                ConfigTags.ProcessesToKillDuringTest
            ),
            group(
                "oracles",
                "Oracles",
                "Suspicious patterns and oracles rules applied during execution.",
                settingsProperties,
                ConfigTags.SuspiciousTags,
                ConfigTags.TagsForSuspiciousOracle,
                ConfigTags.TimeToFreeze,
                ConfigTags.ProcessListenerEnabled,
                ConfigTags.SuspiciousProcessOutput,
                ConfigTags.ProcessLogs,
                ConfigTags.LogOracleEnabled,
                ConfigTags.LogOracleRegex,
                ConfigTags.LogOracleCommands,
                ConfigTags.LogOracleFiles,
                ConfigTags.ExtendedOracles
            ),
            group(
                "state-model",
                "State Model",
                "State model persistence configuration.",
                settingsProperties,
                StateModelTags.StateModelEnabled,
                StateModelTags.DataStore,
                StateModelTags.DataStoreType,
                StateModelTags.DataStoreServer,
                StateModelTags.DataStoreDirectory,
                StateModelTags.DataStoreDB,
                StateModelTags.DataStoreUser,
                StateModelTags.DataStorePassword,
                StateModelTags.DataStoreMode,
                StateModelTags.ActionSelectionAlgorithm,
                StateModelTags.StateModelStoreWidgets,
                StateModelTags.ResetDataStore
            ),
            group(
                "state-identification",
                "Abstract Identification",
                "State abstract identifier attributes.",
                settingsProperties,
                ConfigTags.AbstractStateAttributes
            ),
            /*group(
                "spy",
                "Spy mode configuration",
                "Spy mode visualization settings.",
                settingsProperties,
                ConfigTags.SpyTagAttributes,
                ConfigTags.VisualizeActions,
                ConfigTags.RefreshSpyCanvas
            ),*/
            group(
                "webdriver",
                "WebDriver",
                "Primary WebDriver exploration and browser-console settings.",
                settingsProperties,
                ConfigTags.WebClickableClasses,
                ConfigTags.WebTypeableClasses,
                ConfigTags.WebDeniedExtensions,
                ConfigTags.WebDomainsAllowed,
                ConfigTags.WebPathsAllowed,
                ConfigTags.FollowLinks,
                ConfigTags.BrowserFullScreen,
                ConfigTags.SwitchNewTabs,
                ConfigTags.WebIgnoredTags,
                ConfigTags.WebIgnoredAttributes,
                ConfigTags.WebConsoleErrorOracle,
                ConfigTags.WebConsoleErrorPattern,
                ConfigTags.WebConsoleWarningOracle,
                ConfigTags.WebConsoleWarningPattern,
                ConfigTags.OverrideWebDriverDisplayScale,
                ConfigTags.FormFillingAction
            ),
            group(
                "appium",
                "Android Appium",
                "Android and Appium-specific device, APK, and automation settings.",
                settingsProperties,
                ConfigTags.AppiumPlatformName,
                ConfigTags.AppiumIsApkInstalled,
                ConfigTags.AppiumApp,
                ConfigTags.AppiumAppPackage,
                ConfigTags.AppiumAppActivity,
                ConfigTags.AppiumIsEmulatorDocker,
                ConfigTags.AppiumIpAddress,
                ConfigTags.AppiumDeviceName,
                ConfigTags.AppiumAutomationName,
                ConfigTags.AppiumNewCommandTimeout,
                ConfigTags.AppiumAutoGrantPermissions,
                ConfigTags.AppiumAllowInvisibleElements,
                ConfigTags.AppiumIgnoreHiddenApiPolicyError,
                ConfigTags.AppiumAdbExecTimeout,
                ConfigTags.AppiumUiautomator2ServerInstallTimeout,
                ConfigTags.AppiumUiautomator2ServerLaunchTimeout,
                ConfigTags.AndroidClickableClasses,
                ConfigTags.AndroidTypeableClasses,
                ConfigTags.UseSystemActions
            ),
            group(
                "llm",
                "LLM Agent",
                "Prompting, model, endpoint, and history settings for LLM-based testing.",
                settingsProperties,
                ConfigTags.LlmPlatform,
                ConfigTags.LlmModel,
                ConfigTags.LlmReasoning,
                ConfigTags.LlmHostUrl,
                ConfigTags.LlmAuthorizationHeader,
                ConfigTags.LlmActionFewshotFile,
                ConfigTags.LlmOracleFewshotFile,
                ConfigTags.LlmTemperature,
                ConfigTags.LlmHistorySize,
                ConfigTags.LlmStateless,
                ConfigTags.LlmTestGoals
            ),
            group(
                "coverage",
                "Coverage",
                "Jacoco-based code coverage collection settings.",
                settingsProperties,
                ConfigTags.JacocoCoverage,
                ConfigTags.JacocoCoverageIpAddress,
                ConfigTags.JacocoCoveragePort,
                ConfigTags.JacocoCoverageClasses,
                ConfigTags.JacocoCoverageAccumulate
            )
            /*,
            group(
                "advanced",
                "Advanced",
                "Low-level or protocol-specific values that are useful but less frequently changed.",
                settingsProperties,
                ConfigTags.MaxReward,
                ConfigTags.Discount,
                ConfigTags.ProtocolSpecificSetting_1,
                ConfigTags.ProtocolSpecificSetting_2,
                ConfigTags.ProtocolSpecificSetting_3,
                ConfigTags.ProtocolSpecificSetting_4,
                ConfigTags.ProtocolSpecificSetting_5
            )*/
        );
    }

    private static WorkspaceSettingsGroupDto group(
        String id,
        String title,
        String description,
        Properties settingsProperties,
        Tag<?>... tags
    ) {
        List<WorkspaceSettingDto> settings = List.of(tags)
            .stream()
            .map(tag -> setting(tag, settingsProperties))
            .collect(Collectors.toList());

        return new WorkspaceSettingsGroupDto(id, title, description, settings);
    }

    private static WorkspaceSettingDto setting(Tag<?> tag, Properties settingsProperties) {
        String value = settingsProperties.getProperty(tag.name(), "").trim();
        return new WorkspaceSettingDto(
            tag.name(),
            value,
            inferType(tag),
            tag.getDescription(),
            optionsFor(tag)
        );
    }

    private static String inferType(Tag<?> tag) {
        Class<?> type = tag.type();

        if (Boolean.class.equals(type)) {
            return "boolean";
        }
        if (Integer.class.equals(type)) {
            return "integer";
        }
        if (Double.class.equals(type) || Float.class.equals(type)) {
            return "number";
        }
        if (List.class.equals(type)) {
            return "list";
        }
        if (type.isEnum()) {
            return "enum";
        }

        return "string";
    }

    private static List<String> optionsFor(Tag<?> tag) {
        Map<String, List<String>> configuredOptions = new LinkedHashMap<>();
        configuredOptions.put(ConfigTags.SUTConnector.name(), List.of("COMMAND_LINE", "SUT_WINDOW_TITLE", "SUT_PROCESS_NAME", "WEB_DRIVER", "ANDROID_APPIUM"));
        configuredOptions.put(ConfigTags.LlmReasoning.name(), List.of("low", "medium", "high"));
        configuredOptions.put(StateModelTags.DataStoreType.name(), List.of("remote", "plocal"));
        configuredOptions.put(StateModelTags.DataStoreMode.name(), List.of("instant", "delayed", "hybrid", "none"));
        configuredOptions.put(StateModelTags.ActionSelectionAlgorithm.name(), List.of("random", "unvisited"));

        if (configuredOptions.containsKey(tag.name())) {
            return configuredOptions.get(tag.name());
        }
        if (tag.type().isEnum()) {
            Object[] constants = tag.type().getEnumConstants();
            return Arrays.stream(constants)
                .map(Object::toString)
                .collect(Collectors.toList());
        }

        return List.of();
    }
}
