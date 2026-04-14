/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 */

package org.testar.config.settings;

import org.testar.config.StateModelTags;
import org.testar.config.TestarDirectories;
import org.testar.config.TestarMode;
import org.testar.core.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testar.config.ConfigTags.AbstractStateAttributes;
import static org.testar.config.ConfigTags.AccessBridgeEnabled;
import static org.testar.config.ConfigTags.ActionDuration;
import static org.testar.config.ConfigTags.AlwaysCompile;
import static org.testar.config.ConfigTags.ApplicationName;
import static org.testar.config.ConfigTags.ApplicationVersion;
import static org.testar.config.ConfigTags.BrowserFullScreen;
import static org.testar.config.ConfigTags.ClickFilter;
import static org.testar.config.ConfigTags.ClickableClasses;
import static org.testar.config.ConfigTags.CopyFromTo;
import static org.testar.config.ConfigTags.CreateWidgetInfoJsonFile;
import static org.testar.config.ConfigTags.Delete;
import static org.testar.config.ConfigTags.DeniedExtensions;
import static org.testar.config.ConfigTags.Discount;
import static org.testar.config.ConfigTags.ExtendedOracles;
import static org.testar.config.ConfigTags.FollowLinks;
import static org.testar.config.ConfigTags.ForceForeground;
import static org.testar.config.ConfigTags.FormFillingAction;
import static org.testar.config.ConfigTags.IgnoreDuplicatedVerdicts;
import static org.testar.config.ConfigTags.JacocoCoverage;
import static org.testar.config.ConfigTags.JacocoCoverageAccumulate;
import static org.testar.config.ConfigTags.JacocoCoverageClasses;
import static org.testar.config.ConfigTags.JacocoCoverageIpAddress;
import static org.testar.config.ConfigTags.JacocoCoveragePort;
import static org.testar.config.ConfigTags.KeyBoardListener;
import static org.testar.config.ConfigTags.ClickFilter;
import static org.testar.config.ConfigTags.LlmActionFewshotFile;
import static org.testar.config.ConfigTags.LlmAuthorizationHeader;
import static org.testar.config.ConfigTags.LlmHistorySize;
import static org.testar.config.ConfigTags.LlmHostUrl;
import static org.testar.config.ConfigTags.LlmModel;
import static org.testar.config.ConfigTags.LlmOracleFewshotFile;
import static org.testar.config.ConfigTags.LlmPlatform;
import static org.testar.config.ConfigTags.LlmReasoning;
import static org.testar.config.ConfigTags.LlmStateless;
import static org.testar.config.ConfigTags.LlmTemperature;
import static org.testar.config.ConfigTags.LlmTestGoals;
import static org.testar.config.ConfigTags.LogLevel;
import static org.testar.config.ConfigTags.LogOracleCommands;
import static org.testar.config.ConfigTags.LogOracleEnabled;
import static org.testar.config.ConfigTags.LogOracleFiles;
import static org.testar.config.ConfigTags.LogOracleRegex;
import static org.testar.config.ConfigTags.MaxReward;
import static org.testar.config.ConfigTags.MaxTime;
import static org.testar.config.ConfigTags.Mode;
import static org.testar.config.ConfigTags.MyClassPath;
import static org.testar.config.ConfigTags.OnlySaveFaultySequences;
import static org.testar.config.ConfigTags.OutputDir;
import static org.testar.config.ConfigTags.OverrideWebDriverDisplayScale;
import static org.testar.config.ConfigTags.ProcessListenerEnabled;
import static org.testar.config.ConfigTags.ProcessLogs;
import static org.testar.config.ConfigTags.ProcessesToKillDuringTest;
import static org.testar.config.ConfigTags.ProtocolClass;
import static org.testar.config.ConfigTags.ProtocolCompileDirectory;
import static org.testar.config.ConfigTags.ProtocolSpecificSetting_1;
import static org.testar.config.ConfigTags.ProtocolSpecificSetting_2;
import static org.testar.config.ConfigTags.ProtocolSpecificSetting_3;
import static org.testar.config.ConfigTags.ProtocolSpecificSetting_4;
import static org.testar.config.ConfigTags.ProtocolSpecificSetting_5;
import static org.testar.config.ConfigTags.RefreshSpyCanvas;
import static org.testar.config.ConfigTags.ReportInHTML;
import static org.testar.config.ConfigTags.ReportInPlainText;
import static org.testar.config.ConfigTags.SequenceLength;
import static org.testar.config.ConfigTags.Sequences;
import static org.testar.config.ConfigTags.ShowVisualSettingsDialogOnStartup;
import static org.testar.config.ConfigTags.SpyTagAttributes;
import static org.testar.config.ConfigTags.StartupTime;
import static org.testar.config.ConfigTags.StopGenerationOnFault;
import static org.testar.config.ConfigTags.SuspiciousProcessOutput;
import static org.testar.config.ConfigTags.SuspiciousTags;
import static org.testar.config.ConfigTags.SUTConnector;
import static org.testar.config.ConfigTags.SUTConnectorValue;
import static org.testar.config.ConfigTags.SUTProcesses;
import static org.testar.config.ConfigTags.SwitchNewTabs;
import static org.testar.config.ConfigTags.TagsForSuspiciousOracle;
import static org.testar.config.ConfigTags.TagsToFilter;
import static org.testar.config.ConfigTags.TempDir;
import static org.testar.config.ConfigTags.TimeToFreeze;
import static org.testar.config.ConfigTags.TimeToWaitAfterAction;
import static org.testar.config.ConfigTags.TypeableClasses;
import static org.testar.config.ConfigTags.VisualizeActions;
import static org.testar.config.ConfigTags.WebConsoleErrorOracle;
import static org.testar.config.ConfigTags.WebConsoleErrorPattern;
import static org.testar.config.ConfigTags.WebConsoleWarningOracle;
import static org.testar.config.ConfigTags.WebConsoleWarningPattern;
import static org.testar.config.ConfigTags.WebDomainsAllowed;
import static org.testar.config.ConfigTags.WebIgnoredAttributes;
import static org.testar.config.ConfigTags.WebIgnoredTags;
import static org.testar.config.ConfigTags.WebPathsAllowed;

public class SettingsDefaults {

    private SettingsDefaults() { }

    public static List<Pair<?, ?>> getSettingsDefaults() {

        List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();

        defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
        defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
        defaults.add(Pair.from(LogLevel, 1));
        defaults.add(Pair.from(Mode, TestarMode.Spy));
        defaults.add(Pair.from(OutputDir, TestarDirectories.getOutputDir()));
        defaults.add(Pair.from(TempDir, TestarDirectories.getTempDir()));
        defaults.add(Pair.from(OnlySaveFaultySequences, false));
        defaults.add(Pair.from(ActionDuration, 0.1));
        defaults.add(Pair.from(TimeToWaitAfterAction, 0.1));
        defaults.add(Pair.from(VisualizeActions, false));
        defaults.add(Pair.from(KeyBoardListener, true));
        defaults.add(Pair.from(SequenceLength, 10));
        defaults.add(Pair.from(Sequences, 1));
        defaults.add(Pair.from(MaxTime, 31536000.0));
        defaults.add(Pair.from(StartupTime, 8.0));
        defaults.add(Pair.from(SUTConnectorValue, ""));
        defaults.add(Pair.from(Delete, new ArrayList<String>()));
        defaults.add(Pair.from(CopyFromTo, new ArrayList<Pair<String, String>>()));
        defaults.add(Pair.from(IgnoreDuplicatedVerdicts, false));
        defaults.add(Pair.from(SuspiciousTags, "(?!x)x"));
        defaults.add(Pair.from(ClickFilter, "(?!x)x"));
        defaults.add(Pair.from(MyClassPath, Arrays.asList(TestarDirectories.getSettingsDir())));
        defaults.add(Pair.from(ProtocolClass, "org.testar.monkey.DefaultProtocol"));
        defaults.add(Pair.from(ForceForeground, true));
        defaults.add(Pair.from(StopGenerationOnFault, true));
        defaults.add(Pair.from(TimeToFreeze, 10.0));
        defaults.add(Pair.from(RefreshSpyCanvas, 0.5));
        defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
        defaults.add(Pair.from(MaxReward, 9999999.0));
        defaults.add(Pair.from(Discount, .95));
        defaults.add(Pair.from(AccessBridgeEnabled, false));
        defaults.add(Pair.from(SUTProcesses, ""));
        defaults.add(Pair.from(ApplicationName, ""));
        defaults.add(Pair.from(ApplicationVersion, ""));
        defaults.add(Pair.from(AlwaysCompile, true));
        defaults.add(Pair.from(ProcessListenerEnabled, false));
        defaults.add(Pair.from(SuspiciousProcessOutput, "(?!x)x"));
        defaults.add(Pair.from(ProcessLogs, ".*.*"));
        defaults.add(Pair.from(OverrideWebDriverDisplayScale, ""));
        defaults.add(Pair.from(CreateWidgetInfoJsonFile, false));
        defaults.add(Pair.from(FormFillingAction, false));
        defaults.add(Pair.from(ReportInHTML, true));
        defaults.add(Pair.from(ReportInPlainText, false));
        defaults.add(Pair.from(ExtendedOracles, ""));

        // Oracles for webdriver browser console
        defaults.add(Pair.from(WebConsoleErrorOracle, false));
        defaults.add(Pair.from(WebConsoleErrorPattern, ".*.*"));
        defaults.add(Pair.from(WebConsoleWarningOracle, false));
        defaults.add(Pair.from(WebConsoleWarningPattern, ".*.*"));

        defaults.add(Pair.from(ProtocolSpecificSetting_1, ""));
        defaults.add(Pair.from(ProtocolSpecificSetting_2, ""));
        defaults.add(Pair.from(ProtocolSpecificSetting_3, ""));
        defaults.add(Pair.from(ProtocolSpecificSetting_4, ""));
        defaults.add(Pair.from(ProtocolSpecificSetting_5, ""));
        defaults.add(Pair.from(ProtocolCompileDirectory, "./settings"));

        defaults.add(Pair.from(AbstractStateAttributes, new ArrayList<String>() {
            {
                add("WidgetControlType");
            }
        }));

        defaults.add(Pair.from(ClickableClasses, new ArrayList<String>() {
            {
                add("v-menubar-menuitem");
                add("v-menubar-menuitem-caption");
            }
        }));

        defaults.add(Pair.from(TypeableClasses, new ArrayList<String>()));

        defaults.add(Pair.from(DeniedExtensions, new ArrayList<String>() {
            {
                add("pdf");
                add("jpg");
                add("png");
            }
        }));

        defaults.add(Pair.from(WebDomainsAllowed, new ArrayList<String>() {
            {
                add("www.ou.nl");
            }
        }));

        defaults.add(Pair.from(WebPathsAllowed, ""));

        defaults.add(Pair.from(TagsToFilter, new ArrayList<String>() {
            {
                add("Title");
                add("WebName");
                add("WebTagName");
            }
        }));

        defaults.add(Pair.from(TagsForSuspiciousOracle, new ArrayList<String>() {
            {
                add("Title");
                add("WebName");
                add("WebTagName");
            }
        }));

        defaults.add(Pair.from(FollowLinks, true));
        defaults.add(Pair.from(BrowserFullScreen, true));
        defaults.add(Pair.from(SwitchNewTabs, true));

        defaults.add(Pair.from(WebIgnoredTags, new ArrayList<String>() {
            {
                add("script");
                add("noscript");
                add("head");
                add("meta");
                add("style");
                add("link");
                add("svg");
                add("canvas");
            }
        }));

        defaults.add(Pair.from(WebIgnoredAttributes, new ArrayList<String>() {
            {
                add("xpath");
            }
        }));

        /*
        //TODO web driver settings for login feature
        defaults.add(Pair.from(Login, null)); // null = feature not enabled
        // login = Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
        defaults.add(Pair.from(Username, ""));
        defaults.add(Pair.from(Password, ""));
         */

        // Settings for LogOracle
        defaults.add(Pair.from(LogOracleEnabled, false));
        defaults.add(Pair.from(LogOracleRegex, ".*([Ee]xception|[Ee]rror).*"));
        defaults.add(Pair.from(LogOracleCommands, new ArrayList<String>()));
        defaults.add(Pair.from(LogOracleFiles, new ArrayList<String>()));

        // Settings for Coverage
        defaults.add(Pair.from(JacocoCoverage, false));
        defaults.add(Pair.from(JacocoCoverageIpAddress, "localhost"));
        defaults.add(Pair.from(JacocoCoveragePort, 5000));
        defaults.add(Pair.from(JacocoCoverageClasses, "path/to/SUT/classes"));
        defaults.add(Pair.from(JacocoCoverageAccumulate, false));

        // State Model settings defaults
        defaults.add(Pair.from(StateModelTags.StateModelEnabled, false));
        defaults.add(Pair.from(StateModelTags.DataStore, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreType, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreServer, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreDirectory, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreDB, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreUser, ""));
        defaults.add(Pair.from(StateModelTags.DataStorePassword, ""));
        defaults.add(Pair.from(StateModelTags.DataStoreMode, ""));
        defaults.add(Pair.from(StateModelTags.ResetDataStore, false));
        defaults.add(Pair.from(StateModelTags.ActionSelectionAlgorithm, "random"));
        defaults.add(Pair.from(StateModelTags.StateModelStoreWidgets, false));

        // Settings for LLM agents
        defaults.add(Pair.from(LlmPlatform, "OpenAI"));
        defaults.add(Pair.from(LlmModel, ""));
        defaults.add(Pair.from(LlmReasoning, "default"));
        defaults.add(Pair.from(LlmHostUrl, "http://192.168.108.242:1234/v1/chat/completions"));
        defaults.add(Pair.from(LlmAuthorizationHeader, ""));
        defaults.add(Pair.from(LlmTestGoals, Arrays.asList("Log in with the username john and the password demo\\nThen the message Welcome John Smith is shown")));
        defaults.add(Pair.from(LlmActionFewshotFile, "prompts/fewshot_openai_action.json"));
        defaults.add(Pair.from(LlmOracleFewshotFile, "prompts/fewshot_openai_oracle.json"));
        defaults.add(Pair.from(LlmTemperature, 0.2f));
        defaults.add(Pair.from(LlmHistorySize, 5));
        defaults.add(Pair.from(LlmStateless, true));

        // Settings for Spy Mode Tag Attributes configuration
        defaults.add(Pair.from(SpyTagAttributes,  new ArrayList<String>() {
            {
                add("AbstractID");
                add("ConcreteID");
                add("Desc");
                add("Title");
                add("Role");
                add("Enabled");
                add("Blocked");
                add("Shape");
                add("Path");
            }
        }));

        return defaults;
    }

}
