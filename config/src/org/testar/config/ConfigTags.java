/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

import java.util.List;

import org.testar.core.tag.Tag;

public final class ConfigTags {

    private ConfigTags() { }

    public static final Tag<TestarMode> Mode = Tag.from("Mode", TestarMode.class, 
            "Set the mode you want TESTAR to start in: Spy, Generate");

    public static final Tag<String> SUTConnector = Tag.from("SUTConnector", String.class, 
            "Indicate how you want to connect to the SUT: COMMAND_LINE, SUT_WINDOW_TITLE, SUT_PROCESS_NAME, WEB_DRIVER, ANDROID_APPIUM");

    public static final Tag<String> SUTConnectorValue = Tag.from("SUTConnectorValue", String.class, 
            "The connector value: executable path, windows title, process name");

    public static final Tag<Boolean> AccessBridgeEnabled = Tag.from("AccessBridgeEnabled", Boolean.class, 
            "Enable Java Access Bridge to test Java Swing applications");

    public static final Tag<Integer> Sequences = Tag.from("Sequences", Integer.class, 
            "Number of times to repeat a test");

    public static final Tag<Integer> SequenceLength = Tag.from("SequenceLength", Integer.class, 
            "For each test sequence, the number of GUI actions to perform");

    public static final Tag<Boolean> IgnoreDuplicatedVerdicts = Tag.from("IgnoreDuplicatedVerdicts", Boolean.class,
            "Sets whether to ignore reporting duplicate verdicts across sequences for the same protocol");

    public static final Tag<String> SuspiciousTags = Tag.from("SuspiciousTags", String.class, 
            "Regular expressions ORACLE to find suspicious messages in the GUI Tags");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> TagsForSuspiciousOracle = Tag.from("TagsForSuspiciousOracle",
            (Class<List<String>>) (Class<?>) List.class,
            "The Tags to apply the SuspiciousTags regex expressions");

    public static final Tag<Boolean> ProcessListenerEnabled = Tag.from("ProcessListenerEnabled", Boolean.class, 
            "Enable  the feature to read the process buffer of the SUT (Only for desktop applications through COMMAND_LINE)");

    public static final Tag<String> SuspiciousProcessOutput = Tag.from("SuspiciousProcessOutput", String.class, 
            "Regular expressions ORACLE to find suspicious messages in the process buffer of the SUT");

    public static final Tag<String> ProcessLogs = Tag.from("ProcessLogs", String.class, 
            "Regular expressions to store execution logs coming from the processes");

    public static final Tag<Boolean> LogOracleEnabled = Tag.from("LogOracleEnabled", Boolean.class, 
            "Enable the Oracle for detecting suspicious messages in log files and standard output of commands");

    public static final Tag<String> LogOracleRegex = Tag.from("LogOracleRegex", String.class, 
            "Regular expressions ORACLE to find suspicious messages in the logs");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> LogOracleCommands = Tag.from("LogOracleCommands",
            (Class<List<String>>) (Class<?>) List.class,
            "A list of commands of which standard output should be monitored");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> LogOracleFiles = Tag.from("LogOracleFiles",
            (Class<List<String>>) (Class<?>) List.class,
            "A list of paths of log files to monitor");

    public static final Tag<String> ClickFilter = Tag.from("ClickFilter", String.class, 
            "Regular expressions to FILTER GUI widgets");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> TagsToFilter = Tag.from("TagsToFilter",
            (Class<List<String>>) (Class<?>) List.class,
            "The Tags to apply the ClickFilter regex expressions");

    public static final Tag<String> ProcessesToKillDuringTest = Tag.from("ProcessesToKillDuringTest", String.class, 
            "Regular expressions to kill processes that can start up and interfere when testing the SUT");

    public static final Tag<String> CompositionProfile = Tag.from("CompositionProfile", String.class,
            "Select the scriptless composition profile: windows_composition, webdriver_composition, android_composition");

    public static final Tag<String> CustomCompositionResource = Tag.from("CustomCompositionResource", String.class,
            "Optional properties resource that describes a custom scriptless composition");

    public static final Tag<String> CustomPoliciesResource = Tag.from("CustomPoliciesResource", String.class,
            "Optional properties resource that describes additive custom scriptless policies");

    public static final Tag<String> ApplicationName = Tag.from("ApplicationName", String.class, 
            "Name to identify the SUT.");

    public static final Tag<String> ApplicationVersion = Tag.from("ApplicationVersion", String.class, 
            "Version to identify the SUT.");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> AbstractStateAttributes = Tag.from("AbstractStateAttributes",
            (Class<List<String>>) (Class<?>) List.class,
            "Specify the widget attributes that you wish to use in constructing the widget and state hash strings. Use a comma separated list.");

    /**
     * Large Language Models settings 
     */

    public static final Tag<String> LlmPlatform = Tag.from("LlmPlatform", String.class,
            "The platform that hosts the LLM agent");

    public static final Tag<String> LlmModel = Tag.from("LlmModel", String.class,
            "The model used by the LLM agent");

    public static final Tag<String> LlmReasoning = Tag.from("LlmReasoning", String.class,
            "The reasoning effort of the LLM model");

    public static final Tag<String> LlmHostUrl = Tag.from("LlmHostUrl", String.class,
            "The Host URL on which the LLM agent is running");

    public static final Tag<String> LlmAuthorizationHeader = Tag.from("LlmAuthorizationHeader", String.class, 
            "The Authorization Header required by some LLM agents");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> LlmTestGoals = Tag.from("LlmTestGoals", (Class<List<String>>) (Class<?>) List.class,
            "The objective of the test for the LLM agent");

    public static final Tag<String> LlmActionFewshotFile = Tag.from("LlmActionFewshotFile", String.class,
            "The location of the action fewshot file that contains the prompt instructions");

    public static final Tag<String> LlmOracleFewshotFile = Tag.from("LlmOracleFewshotFile", String.class,
            "The location of the oracle fewshot file that contains the prompt instructions");

    public static final Tag<Float> LlmTemperature = Tag.from("LlmTemperature", Float.class,
            "Controls the randomness of the LLM output. Value between 0 and 2.");

    public static final Tag<Integer> LlmHistorySize = Tag.from("LlmHistorySize", Integer.class,
            "Controls how many historical actions are kept track of and sent to the LLM.");

    public static final Tag<Boolean> LlmStateless = Tag.from("LlmStateless", Boolean.class,
            "Generates individual prompts for each action and oracle LLM conversation.");

    /**
     * WebDriver settings 
     */

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebClickableClasses = Tag.from("WebClickableClasses",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which web CSS classes need to be considered clickable");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebTypeableClasses = Tag.from("WebTypeableClasses",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which web CSS classes need to be considered typeable");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebDeniedExtensions = Tag.from("WebDeniedExtensions",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which web URL extensions need to be ignored when testing");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebDomainsAllowed = Tag.from("WebDomainsAllowed",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which web URL domains are allowed to explore when testing");

    public static final Tag<String> WebPathsAllowed = Tag.from("WebPathsAllowed", String.class, 
            "Regular expressions to indicate which web URL paths are allowed to explore when testing");

    public static final Tag<Boolean> FollowLinks = Tag.from("FollowLinks", Boolean.class, 
            "Indicate if allowing to follow links opened in new tabs");

    public static final Tag<Boolean> BrowserFullScreen = Tag.from("BrowserFullScreen", Boolean.class, 
            "Indicate if perform the web testing with the browser in full screen");

    public static final Tag<Boolean> SwitchNewTabs = Tag.from("SwitchNewTabs", Boolean.class, 
            "Indicate if switch to a new web tab if opened");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebIgnoredTags = Tag.from("WebIgnoredTags",
            (Class<List<String>>) (Class<?>) List.class,
            "List of HTML tags that TESTAR should ignore when obtaining the web state");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> WebIgnoredAttributes = Tag.from("WebIgnoredAttributes",
            (Class<List<String>>) (Class<?>) List.class,
            "List of web attributes that TESTAR should ignore when obtaining the web state (e.g., to reduce state high-performance workloads)");

    public static final Tag<Boolean> WebConsoleErrorOracle = Tag.from("WebConsoleErrorOracle", Boolean.class, 
            "Enable or Disable applying ORACLES to the browser error console");

    public static final Tag<String> WebConsoleErrorPattern = Tag.from("WebConsoleErrorPattern", String.class, 
            "Regular expressions ORACLE to find suspicious messages in the browser error console");

    public static final Tag<Boolean> WebConsoleWarningOracle = Tag.from("WebConsoleWarningOracle", Boolean.class, 
            "Enable or Disable applying ORACLES to the browser warning console");

    public static final Tag<String> WebConsoleWarningPattern = Tag.from("WebConsoleWarningPattern", String.class, 
            "Regular expressions ORACLE to find suspicious messages in the browser warning console");

    // Note: Defined the tag as string on purpose so we can leave the default value empty in the pre defined settings.
    public static final Tag<String> OverrideWebDriverDisplayScale = Tag.from("OverrideWebDriverDisplayScale", String.class, 
            "Overrides the displayscale obtained from the system for web SUTs");

    /**
     * Android-Appium settings 
     */

    public static final Tag<String> AppiumPlatformName = Tag.from("AppiumPlatformName", String.class,
            "The mobile platform name used by Appium");

    public static final Tag<Boolean> AppiumIsApkInstalled = Tag.from("AppiumIsApkInstalled", Boolean.class,
            "Sets whether the SUT-APK is already installed in the device");

    public static final Tag<String> AppiumApp = Tag.from("AppiumApp", String.class,
            "The SUT-APK local or remote file");

    public static final Tag<String> AppiumAppPackage = Tag.from("AppiumAppPackage", String.class,
            "The Android application package name when the APK is already installed in the device");

    public static final Tag<String> AppiumAppActivity = Tag.from("AppiumAppActivity", String.class,
            "The Android launcher activity when the APK is already installed in the device");

    public static final Tag<Boolean> AppiumIsEmulatorDocker = Tag.from("AppiumIsEmulatorDocker", Boolean.class,
            "Sets whether the Android emulator is running inside a Docker container");

    public static final Tag<String> AppiumIpAddress = Tag.from("AppiumIpAddress", String.class,
            "The IP address of the remote Appium server used by a Docker-based Android emulator");

    public static final Tag<String> AppiumDeviceName = Tag.from("AppiumDeviceName", String.class,
            "The Android device name reported to Appium");

    public static final Tag<String> AppiumAutomationName = Tag.from("AppiumAutomationName", String.class,
            "The Appium automation engine name");

    public static final Tag<Integer> AppiumNewCommandTimeout = Tag.from("AppiumNewCommandTimeout", Integer.class,
            "The Appium new command timeout in seconds");

    public static final Tag<Boolean> AppiumAutoGrantPermissions = Tag.from("AppiumAutoGrantPermissions", Boolean.class,
            "Sets whether Appium automatically grants runtime permissions to the Android application");

    public static final Tag<Boolean> AppiumAllowInvisibleElements = Tag.from("AppiumAllowInvisibleElements", Boolean.class,
            "Sets whether Appium should expose invisible Android elements");

    public static final Tag<Boolean> AppiumIgnoreHiddenApiPolicyError = Tag.from("AppiumIgnoreHiddenApiPolicyError", Boolean.class,
            "Sets whether Appium should ignore hidden API policy errors on Android");

    public static final Tag<Integer> AppiumAdbExecTimeout = Tag.from("AppiumAdbExecTimeout", Integer.class,
            "The ADB execution timeout in milliseconds");

    public static final Tag<Integer> AppiumUiautomator2ServerInstallTimeout = Tag.from("AppiumUiautomator2ServerInstallTimeout", Integer.class,
            "The UiAutomator2 server installation timeout in milliseconds");

    public static final Tag<Integer> AppiumUiautomator2ServerLaunchTimeout = Tag.from("AppiumUiautomator2ServerLaunchTimeout", Integer.class,
            "The UiAutomator2 server launch timeout in milliseconds");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> AndroidClickableClasses = Tag.from("AndroidClickableClasses",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which Android className values need to be considered clickable");

    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> AndroidTypeableClasses = Tag.from("AndroidTypeableClasses",
            (Class<List<String>>) (Class<?>) List.class,
            "Indicate which Android className values need to be considered typeable");

    // 5 settings that can be used in user specified TESTAR protocols for anything:
    public static final Tag<String> ProtocolSpecificSetting_1 = Tag.from("ProtocolSpecificSetting_1", String.class, 
            "Settings (string) that can be used for user specified protocols");
    public static final Tag<String> ProtocolSpecificSetting_2 = Tag.from("ProtocolSpecificSetting_2", String.class, 
            "Settings (string) that can be used for user specified protocols");
    public static final Tag<String> ProtocolSpecificSetting_3 = Tag.from("ProtocolSpecificSetting_3", String.class, 
            "Settings (string) that can be used for user specified protocols");
    public static final Tag<String> ProtocolSpecificSetting_4 = Tag.from("ProtocolSpecificSetting_4", String.class, 
            "Settings (string) that can be used for user specified protocols");
    public static final Tag<String> ProtocolSpecificSetting_5 = Tag.from("ProtocolSpecificSetting_5", String.class, 
            "Settings (string) that can be used for user specified protocols");

    /**
     * Coverage settings
     */

    public static final Tag<Boolean> JacocoCoverage = Tag.from("JacocoCoverage", Boolean.class,
            "Sets whether to extract Jacoco Coverage");

    public static final Tag<String> JacocoCoverageIpAddress = Tag.from("JacocoCoverageIpAddress", String.class, 
            "The JMX IP Address on which the Jacoco Agent is running");

    public static final Tag<Integer> JacocoCoveragePort = Tag.from("JacocoCoveragePort", Integer.class, 
            "The JMX port on which the Jacoco Agent is running");

    public static final Tag<String> JacocoCoverageClasses = Tag.from("JacocoCoverageClasses", String.class, 
            "The SUT class files that Jacoco uses to create the CSV report");

    public static final Tag<Boolean> JacocoCoverageAccumulate = Tag.from("JacocoCoverageAccumulate", Boolean.class,
            "Sets whether Jacoco coverage will be accumulated across the run sequences");

    /**
     * Additional settings with descriptions
     */

    public static final Tag<Double> ActionDuration = Tag.from("ActionDuration", Double.class, 
            "Sets the speed, in seconds, at which a GUI action is performed");

    public static final Tag<Double> TimeToWaitAfterAction = Tag.from("TimeToWaitAfterAction", Double.class, 
            "Sets the delay, in seconds, between UI actions during a test");

    public static final Tag<Double> StartupTime = Tag.from("StartupTime", Double.class, 
            "Sets how many seconds to wait for the SUT to be ready for testing");

    public static final Tag<Double> MaxTime = Tag.from("MaxTime", Double.class, 
            "Sets a time, in seconds, after which the test run is finished (e.g. stop after an hour)");

    public static final Tag<Boolean> FormFillingAction = Tag.from("FormFillingAction", Boolean.class, 
            "Enables or disables a specific web action that populate data in web forms");

    public static final Tag<String> SUTProcesses = Tag.from("SUTProcesses", String.class, 
            "Regular expressions that indicates which processes conform the SUT");

    public static final Tag<Boolean> ShowVisualSettingsDialogOnStartup = Tag.from("ShowVisualSettingsDialogOnStartup", Boolean.class, 
            "Sets whether to display TESTAR dialog. If false is used, then TESTAR will run in the mode of the Mode property");

    public static final Tag<Boolean> ForceForeground = Tag.from("ForceForeground", Boolean.class, 
            "Sets whether to keep the SUTs GUI active in the screen (e.g. when its minimised or when a process is started and its UI is in front, etc.)");

    public static final Tag<Integer> LogLevel = Tag.from("LogLevel", Integer.class, 
            "Sets the logging level to critical messages (0), information messages (1) or debug messages (2)");

    public static final Tag<Boolean> OnlySaveFaultySequences = Tag.from("OnlySaveFaultySequences", Boolean.class, 
            "Sets whether to save test sequences without failures");

    public static final Tag<Boolean> StopGenerationOnFault = Tag.from("StopGenerationOnFault", Boolean.class, 
            "Sets whether to finish a test in the presence of a fail (e.g. Suspicious Tag detected)");

    public static final Tag<Double> TimeToFreeze = Tag.from("TimeToFreeze", Double.class, 
            "Sets the time window, in seconds, for which to wait for a not responding SUT. After that, the test will finish with a fail");

    public static final Tag<Boolean> VisualizeActions = Tag.from("VisualizeActions", Boolean.class, 
            "Sets whether to display overlay information, inside the SPY mode, for all the UI actions derived from the test set up");

    public static final Tag<Boolean> KeyBoardListener = Tag.from("KeyBoardListener", Boolean.class, 
            "Sets whether to listen to keyboard shortcuts during the exceution");

    public static final Tag<Boolean> UseSystemActions = Tag.from("UseSystemActions", Boolean.class, 
            "ANDROID: Indicate if add system calls");

    public static final Tag<Double> RefreshSpyCanvas = Tag.from("RefreshSpyCanvas", Double.class, 
            "Time in milliseconds that indicates the frequency of refreshing the screen in SPY mode");

    public static final Tag<Double> MaxReward = Tag.from("MaxReward", Double.class, 
            "MaxReward value for the QLearningActionSelector");

    public static final Tag<Double> Discount = Tag.from("Discount", Double.class, 
            "Discount value for the QLearningActionSelector");

    public static final Tag<Boolean> CreateWidgetInfoJsonFile = Tag.from("CreateWidgetInfoJsonFile", Boolean.class, 
            "Sets whether create a JSON file with information about widgets and their location on the screenshot");

    public static final Tag<String> OutputDir = Tag.from("OutputDir", String.class, 
            "The relative path to save TESTAR output results");

    public static final Tag<String> TempDir = Tag.from("TempDir", String.class, 
            "The relative path to temporarily  save TESTAR files");
    
    public static final Tag<Boolean> ReportInHTML = Tag.from("ReportInHTML", Boolean.class,
             "Sets whether to create a HTML report");
    
    public static final Tag<Boolean> ReportInPlainText = Tag.from("ReportInPlainText", Boolean.class,
             "Sets whether to create a plain text report");

    public static final Tag<String> ExtendedOracles = Tag.from("ExtendedOracles", String.class,
            "Select extended oracles to be applied during runtime for testing");

    /**
     * Spy mode settings
     */

    public static final Tag<List<String>> SpyTagAttributes = Tag.from("SpyTagAttributes", (Class<List<String>>) (Class<?>) List.class,
            "Specify the widget attributes that you wish to visualize during Spy Mode. Use a semicolon separated list.");
}
