/***************************************************************************************************
*
* Copyright (c) 2013 - 2021 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
*
* Redistribution an d use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package org.testar.monkey;

import org.sikuli.script.Do;
import org.testar.monkey.alayer.Tag;

import java.util.List;

public final class ConfigTags {
  public static final Tag<String> ProcessesToKillDuringTest = Tag.from("ProcessesToKillDuringTest", String.class);
  public static final Tag<Boolean> ShowVisualSettingsDialogOnStartup = Tag.from("ShowVisualSettingsDialogOnStartup", Boolean.class);
  public static final Tag<Integer> LogLevel = Tag.from("LogLevel", Integer.class);
  public static final Tag<String> SuspiciousTitles = Tag.from("SuspiciousTitles", String.class);
  public static final Tag<String> ClickFilter = Tag.from("ClickFilter", String.class);
  public static final Tag<String> OutputDir = Tag.from("OutputDir", String.class);
  public static final Tag<String> TempDir = Tag.from("TempDir", String.class);
  public static final Tag<Boolean> OnlySaveFaultySequences = Tag.from("OnlySaveFaultySequences", Boolean.class);
  public static final Tag<Boolean> ForceForeground = Tag.from("ForceForeground", Boolean.class);
  public static final Tag<Double> ActionDuration = Tag.from("ActionDuration", Double.class);
  public static final Tag<Double> FaultThreshold = Tag.from("FaultThreshold", Double.class);
  public static final Tag<Double> TimeToWaitAfterAction = Tag.from("TimeToWaitAfterAction", Double.class);
  public static final Tag<Boolean> VisualizeActions = Tag.from("VisualizeActions", Boolean.class);
  public static final Tag<Boolean> VisualizeSelectedAction = Tag.from("VisualizeSelectedAction", Boolean.class);
  public static final Tag<Boolean> DrawWidgetUnderCursor = Tag.from("DrawWidgetUnderCursor", Boolean.class);
  public static final Tag<Boolean> DrawWidgetInfo = Tag.from("DrawWidgetInfo", Boolean.class);
  public static final Tag<Boolean> ExecuteActions = Tag.from("ExecuteActions", Boolean.class);
  public static final Tag<String> PathToReplaySequence = Tag.from("PathToReplaySequence", String.class);
  public static final Tag<RuntimeControlsProtocol.Modes> Mode = Tag.from("Mode", RuntimeControlsProtocol.Modes.class);
  public static final Tag<String> SUTConnectorValue = Tag.from("SUTConnectorValue", String.class);
  public static final Tag<Integer> SequenceLength = Tag.from("SequenceLength", Integer.class);
  public static final Tag<Integer> Sequences = Tag.from("Sequences", Integer.class);
  public static final Tag<Double> ReplayRetryTime = Tag.from("ReplayRetryTime", Double.class);
  public static final Tag<Double> MaxTime = Tag.from("MaxTime", Double.class);
  public static final Tag<Double> StartupTime = Tag.from("StartupTime", Double.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> Delete = Tag.from("Delete", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<Pair<String, String>>> CopyFromTo = Tag.from("CopyFromTo", (Class<List<Pair<String, String>>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> MyClassPath = Tag.from("MyClassPath", (Class<List<String>>) (Class<?>) List.class);
  public static final Tag<String> ProtocolClass = Tag.from("ProtocolClass", String.class);
  public static final Tag<Boolean> UseRecordedActionDurationAndWaitTimeDuringReplay = Tag.from("UseRecordedActionDurationAndWaitTimeDuringReplay", Boolean.class);
  public static final Tag<Boolean> StopGenerationOnFault = Tag.from("StopGenerationOnFault", Boolean.class);
  public static final Tag<Double> TimeToFreeze = Tag.from("TimeToFreeze", Double.class);
  public static final Tag<Boolean> ShowSettingsAfterTest = Tag.from("ShowSettingsAfterTest", Boolean.class);
  public static final Tag<Double> RefreshSpyCanvas = Tag.from("RefreshSpyCanvas", Double.class);

  public static final Tag<String> SUTConnector = Tag.from("SUTConnector", String.class);
  public static final Tag<String> TestGenerator = Tag.from("TestGenerator", String.class);
  public static final Tag<Double> MaxReward = Tag.from("MaxReward", Double.class);
  public static final Tag<Double> Discount = Tag.from("Discount", Double.class);
  public static final Tag<Boolean> AlgorithmFormsFilling = Tag.from("AlgorithmFormsFilling", Boolean.class);
  public static final Tag<Integer> TypingTextsForExecutedAction = Tag.from("TypingTextsForExecutedAction", Integer.class);
  public static final Tag<Boolean> DrawWidgetTree = Tag.from("DrawWidgetTree", Boolean.class);
  public static final Tag<Integer> ExplorationSampleInterval = Tag.from("ExplorationSampleInterval", Integer.class);
  public static final Tag<Boolean> ForceToSequenceLength = Tag.from("ForceToSequenceLength", Boolean.class);
  public static final Tag<Integer> NonReactingUIThreshold = Tag.from("NonReactingUIThreshold", Integer.class);
  public static final Tag<Boolean> OfflineGraphConversion = Tag.from("OfflineGraphConversion", Boolean.class);
  public static final Tag<Float> StateScreenshotSimilarityThreshold = Tag.from("StateScreenshotSimilarityThreshold", Float.class);
  public static final Tag<Boolean> UnattendedTests = Tag.from("UnattendedTests", Boolean.class);
  public static final Tag<Boolean> AccessBridgeEnabled = Tag.from("AccessBridgeEnabled", Boolean.class);
  public static final Tag<String> SUTProcesses = Tag.from("SUTProcesses", String.class); // Shift+0 shortcut to debug (STDOUT) windows' process names


  // MySQL reporting databse configuration
  public static final Tag<String> SQLReporting = Tag.from("SQLReporting", String.class);
  public static final Tag<String> SQLReportingType = Tag.from("SQLReportingType", String.class);
  public static final Tag<String> SQLReportingServer = Tag.from("SQLReportingServer", String.class);
  public static final Tag<String> SQLReportingUser = Tag.from("SQLReportingUser", String.class);
  public static final Tag<String> SQLReportingDB = Tag.from("SQLReportingDB", String.class);
  public static final Tag<String> SQLReportingDirectory = Tag.from("SQLReportingDirectory", String.class);
  public static final Tag<String> SQLReportingPassword = Tag.from("SQLReportingPassword", String.class);

  // state model config tags
  public static final Tag<Boolean> StateModelEnabled = Tag.from("StateModelEnabled", Boolean.class);
  public static final Tag<String> StateModelReinforcementLearningEnabled = Tag.from("StateModelReinforcementLearningEnabled", String.class);
  public static final Tag<String> DataStore = Tag.from("DataStore", String.class);
  public static final Tag<String> DataStoreType = Tag.from("DataStoreType", String.class);
  public static final Tag<String> DataStoreServer = Tag.from("DataStoreServer", String.class);
  public static final Tag<String> DataStoreDB = Tag.from("DataStoreDB", String.class);
  public static final Tag<String> DataStoreUser = Tag.from("DataStoreUser", String.class);
  public static final Tag<String> DataStorePassword = Tag.from("DataStorePassword", String.class);
  public static final Tag<String> DataStoreMode = Tag.from("DataStoreMode", String.class);
  public static final Tag<String> DataStoreDirectory = Tag.from("DataStoreDirectory", String.class);
  public static final Tag<Boolean> ResetDataStore = Tag.from("ResetDataStore", Boolean.class);
  public static final Tag<String> ApplicationName = Tag.from("ApplicationName", String.class);
  public static final Tag<String> ApplicationVersion = Tag.from("ApplicationVersion", String.class);
  public static final Tag<String> ActionSelectionAlgorithm = Tag.from("ActionSelectionAlgorithm", String.class);
  public static final Tag<Boolean> StateModelStoreWidgets = Tag.from("StateModelStoreWidgets", Boolean.class);
  public static final Tag<String> ReportType = Tag.from("ReportType", String.class);
  public static final Tag<Integer> ReportServicePort = Tag.from("ReportServicePort", Integer.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> ConcreteStateAttributes = Tag.from("ConcreteStateAttributes", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> AbstractStateAttributes = Tag.from("AbstractStateAttributes", (Class<List<String>>) (Class<?>) List.class);
  // Replay state model
  public static final Tag<String> ReplayApplicationName = Tag.from("ReplayApplicationName", String.class);
  public static final Tag<String> ReplayApplicationVersion = Tag.from("ReplayApplicationVersion", String.class);
  public static final Tag<String> ReplayModelSequenceId = Tag.from("ReplayModelSequenceId", String.class);
  public static final Tag<String> ReplayModelSequenceTime = Tag.from("ReplayModelSequenceTime", String.class);

  public static final Tag<Boolean> AlwaysCompile = Tag.from("AlwaysCompile", Boolean.class);

  public static final Tag<Boolean> ProcessListenerEnabled = Tag.from("ProcessListenerEnabled", Boolean.class);
  public static final Tag<String> SuspiciousProcessOutput = Tag.from("SuspiciousProcessOutput", String.class);
  public static final Tag<String> ProcessLogs = Tag.from("ProcessLogs", String.class);

  // Note: Defined the tag as string on purpose so we can leave the default value empty in the pre defined settings.
  public static final Tag<String> OverrideWebDriverDisplayScale = Tag.from("OverrideWebDriverDisplayScale", String.class);

  public static final Tag<String> ExtendedSettingsFile = Tag.from("ExtendedSettingsFile", String.class);

  // 5 settings that can be used in user specified TESTAR protocols for anything:
  public static final Tag<String> ProtocolSpecificSetting_1 = Tag.from("ProtocolSpecificSetting_1", String.class);
  public static final Tag<String> ProtocolSpecificSetting_2 = Tag.from("ProtocolSpecificSetting_2", String.class);
  public static final Tag<String> ProtocolSpecificSetting_3 = Tag.from("ProtocolSpecificSetting_3", String.class);
  public static final Tag<String> ProtocolSpecificSetting_4 = Tag.from("ProtocolSpecificSetting_4", String.class);
  public static final Tag<String> ProtocolSpecificSetting_5 = Tag.from("ProtocolSpecificSetting_5", String.class);

  // WebDriver specific settings:
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> ClickableClasses = Tag.from("ClickableClasses", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> DeniedExtensions = Tag.from("DeniedExtensions", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> DomainsAllowed = Tag.from("DomainsAllowed", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> TagsToFilter = Tag.from("TagsToFilter", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> TagsForSuspiciousOracle = Tag.from("TagsForSuspiciousOracle", (Class<List<String>>) (Class<?>) List.class);
  public static final Tag<Boolean> FollowLinks = Tag.from("FollowLinks", Boolean.class);
  public static final Tag<Boolean> BrowserFullScreen = Tag.from("BrowserFullScreen", Boolean.class);
  public static final Tag<Boolean> SwitchNewTabs = Tag.from("SwitchNewTabs", Boolean.class);

  // Oracles for webdriver browser console
  public static final Tag<Boolean> WebConsoleErrorOracle = Tag.from("WebConsoleErrorOracle", Boolean.class);
  public static final Tag<String> WebConsoleErrorPattern = Tag.from("WebConsoleErrorPattern", String.class);
  public static final Tag<Boolean> WebConsoleWarningOracle = Tag.from("WebConsoleWarningOracle", Boolean.class);
  public static final Tag<String> WebConsoleWarningPattern = Tag.from("WebConsoleWarningPattern", String.class);
  public static final Tag<String> CustomOracle = Tag.from("CustomOracle", String.class);

  public static final Tag<Boolean> FlashFeedback = Tag.from("FlashFeedback", Boolean.class);
  public static final Tag<String> ProtocolCompileDirectory = Tag.from("ProtocolCompileDirectory", String.class);
  public static final Tag<String> ReportingClass = Tag.from("ReportingClass", String.class);
  public static final Tag<String> GitUrl = Tag.from("GitUrl", String.class);
  public static final Tag<String> ReportApiUrl = Tag.from("ReportApiUrl", String.class);
  public static final Tag<String> GitUsername = Tag.from("GitUsername", String.class);
  public static final Tag<String> GitToken = Tag.from("GitToken", String.class);
  public static final Tag<String> GitBranch = Tag.from("GitBranch", String.class);
  public static final Tag<Boolean> GitAuthRequired = Tag.from("GitAuthRequired", Boolean.class);

  public static final Tag<String> SonarUrl = Tag.from("SonarUrl", String.class);
  public static final Tag<String> SonarUsername = Tag.from("SonarUsername", String.class);
  public static final Tag<String> SonarPassword = Tag.from("SonarPassword", String.class);
  public static final Tag<String> SonarSubdir = Tag.from("SonarSubdir", String.class);
  public static final Tag<Boolean> SonarDockerize = Tag.from("SonarDockerize", Boolean.class);
  public static final Tag<Boolean> SonarSaveResult = Tag.from("SonarSaveResult", Boolean.class);

  public static final Tag<String> SonarProjectProperties = Tag.from("SonarProjectProperties", String.class);
  public static final Tag<String> SonarProjectName = Tag.from("SonarProjectName", String.class);
  public static final Tag<String> SonarProjectKey = Tag.from("SonarProjectKey", String.class);

  public static final Tag<String> ForcedLoginUrl = Tag.from("ForcedLoginUrl", String.class);
  public static final Tag<String> ForcedLoginButtonName = Tag.from("ForcedLoginButtonName", String.class);
  public static final Tag<String> ForcedLoginFormId = Tag.from("ForcedLoginFormId", String.class);
  public static final Tag<String> ForcedLoginInputKeyAttribute = Tag.from("ForcedLoginInputKeyAttribute", String.class);
  public static final Tag<String> ForcedLoginUsernameInputName = Tag.from("ForcedLoginUsernameInputName", String.class);
  public static final Tag<String> ForcedLoginUsername = Tag.from("ForcedLoginUsername", String.class);
  public static final Tag<String> ForcedLoginPasswordInputName = Tag.from("ForcedLoginPasswordInputName", String.class);
  public static final Tag<String> ForcedLoginPassword = Tag.from("ForcedLoginPassword", String.class);

  public static final Tag<String> BtraceServiceHost = Tag.from("BtraceServiceHost", String.class);

  public static final Tag<Boolean> QLearningEnabled = Tag.from("QLearningEnabled", Boolean.class);
  public static final Tag<Double> QLearningAlpha = Tag.from("QLearningAlpha", Double.class);
  public static final Tag<Double> QLearningGamma = Tag.from("QLearningGamma", Double.class);

  // Reinforcement learning specific
  public static final Tag<Float> Alpha = Tag.from("Alpha", Float.class);
  public static final Tag<Float> Gamma = Tag.from("Gamma", Float.class);
  public static final Tag<Float> DefaultValue = Tag.from("DefaultValue", Float.class);
  public static final Tag<Float> Epsilon = Tag.from("Epsilon", Float.class);
  public static final Tag<Float> MinEpsilon = Tag.from("MinEpsilon", Float.class);
  public static final Tag<Float> MaxEpsilon = Tag.from("MaxEpsilon", Float.class);
  public static final Tag<Integer> TotalActions = Tag.from("TotalActions", Integer.class);
  public static final Tag<Float> DefaultReward = Tag.from("DefaultReward", Float.class);
  public static final Tag<String> RewardFunction = Tag.from("RewardFunction", String.class);
  public static final Tag<String> Policy = Tag.from("Policy", String.class);
  public static final Tag<Float> DecayRate = Tag.from("DecayRate", Float.class);
  public static final Tag<Float> Temperature = Tag.from("Temperature", Float.class);
  public static final Tag<Float> MaxQValue = Tag.from("MaxQValue", Float.class);
  public static final Tag<String> TagName = Tag.from("TagName", String.class);
  public static final Tag<String> QFunction = Tag.from("QFunction", String.class);

  /*
  //TODO web driver settings for login feature
  public static final Tag<Pair<String, String>> Login = Tag.from("Login", (Class<Pair<String, String>>) (Class<?>) Pair.class);
  // Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
  public static final Tag<Pair<String, String>> Username = Tag.from("Username", (Class<Pair<String, String>>) (Class<?>) Pair.class);
  public static final Tag<Pair<String, String>> Password = Tag.from("Password", (Class<Pair<String, String>>) (Class<?>) Pair.class);
  */
}
