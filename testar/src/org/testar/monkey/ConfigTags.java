/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
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

import org.testar.monkey.alayer.Tag;

import java.util.List;

public final class ConfigTags {

	private ConfigTags() {}

	public static final Tag<RuntimeControlsProtocol.Modes> Mode = Tag.from("Mode", RuntimeControlsProtocol.Modes.class, 
			"Set the mode you want TESTAR to start in: Spy, Generate, Replay");

	public static final Tag<String> SUTConnector = Tag.from("SUTConnector", String.class, 
			"Indicate how you want to connect to the SUT: COMMAND_LINE, SUT_WINDOW_TITLE, SUT_PROCESS_NAME");

	public static final Tag<String> SUTConnectorValue = Tag.from("SUTConnectorValue", String.class, 
			"The connector value: executable path, windows title, process name");

	public static final Tag<Boolean> AccessBridgeEnabled = Tag.from("AccessBridgeEnabled", Boolean.class, 
			"Enable Java Access Bridge to test Java Swing applications");

	public static final Tag<Integer> Sequences = Tag.from("Sequences", Integer.class, 
			"Number of times to repeat a test");

	public static final Tag<Integer> SequenceLength = Tag.from("SequenceLength", Integer.class, 
			"For each test sequence, the number of GUI actions to perform");

	public static final Tag<String> SuspiciousTags = Tag.from("SuspiciousTags", String.class, 
			"Regular expressions ORACLE to find suspicious messages in the GUI Tags");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> TagsForSuspiciousOracle = Tag.from("TagsForSuspiciousOracle", (Class<List<String>>) (Class<?>) List.class, 
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
	public static final Tag<List<String>> LogOracleCommands = Tag.from("LogOracleCommands", (Class<List<String>>) (Class<?>) List.class, 
			"A list of commands of which standard output should be monitored");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> LogOracleFiles = Tag.from("LogOracleFiles", (Class<List<String>>) (Class<?>) List.class, 
			"A list of paths of log files to monitor");

	public static final Tag<String> ClickFilter = Tag.from("ClickFilter", String.class, 
			"Regular expressions to FILTER GUI widgets");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> TagsToFilter = Tag.from("TagsToFilter", (Class<List<String>>) (Class<?>) List.class, 
			"The Tags to apply the ClickFilter regex expressions");

	public static final Tag<String> ProcessesToKillDuringTest = Tag.from("ProcessesToKillDuringTest", String.class, 
			"Regular expressions to kill processes that can start up and interfere when testing the SUT");

	public static final Tag<String> ProtocolClass = Tag.from("ProtocolClass", String.class, 
			"Indicate the location of the protocol class for your specific SUT");

	/**
	 * State Model settings 
	 */

	public static final Tag<String> ApplicationName = Tag.from("ApplicationName", String.class, 
			"Name to identify the SUT. Especially important to identify a State Model");

	public static final Tag<String> ApplicationVersion = Tag.from("ApplicationVersion", String.class, 
			"Version to identify the SUT. Especially important to identify a State Model");

	public static final Tag<Boolean> StateModelEnabled = Tag.from("StateModelEnabled", Boolean.class, 
			"Enable or disable the State Model feature");

	public static final Tag<String> DataStore = Tag.from("DataStore", String.class, 
			"The graph database we use to store the State Model: OrientDB");

	public static final Tag<String> DataStoreType = Tag.from("DataStoreType", String.class, 
			"The mode we use to connect to the database: remote or plocal");

	public static final Tag<String> DataStoreServer = Tag.from("DataStoreServer", String.class, 
			"IP address to connect if we desired to use remote mode");

	public static final Tag<String> DataStoreDirectory = Tag.from("DataStoreDirectory", String.class, 
			"Path of the directory on which local OrientDB exists, if we use plocal mode");

	public static final Tag<String> DataStoreDB = Tag.from("DataStoreDB", String.class, 
			"The name of the desired database on which we want to store the State Model.");

	public static final Tag<String> DataStoreUser = Tag.from("DataStoreUser", String.class, 
			"User credential to authenticate TESTAR in OrientDB");

	public static final Tag<String> DataStorePassword = Tag.from("DataStorePassword", String.class, 
			"Password credential to authenticate TESTAR in OrientDB");

	public static final Tag<String> DataStoreMode = Tag.from("DataStoreMode", String.class, 
			"Indicate how TESTAR should store the model objects in the database: instant, delayed, hybrid, none");

	public static final Tag<String> ActionSelectionAlgorithm = Tag.from("ActionSelectionAlgorithm", String.class, 
			"State Model Action Selection mechanism to explore the SUT: random or unvisited");

	public static final Tag<Boolean> StateModelStoreWidgets = Tag.from("StateModelStoreWidgets", Boolean.class, 
			"Save all widget tree information in the State Model every time TESTAR discovers a new Concrete State");

	public static final Tag<Boolean> ResetDataStore = Tag.from("ResetDataStore", Boolean.class, 
			"WARNING: Delete all existing State Models from the selected database before creating a new one");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> AbstractStateAttributes = Tag.from("AbstractStateAttributes", (Class<List<String>>) (Class<?>) List.class, 
			"Specify the widget attributes that you wish to use in constructing the widget and state hash strings. Use a comma separated list.");

	/**
	 * WebDriver settings 
	 */

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> ClickableClasses = Tag.from("ClickableClasses", (Class<List<String>>) (Class<?>) List.class, 
			"Indicate which web CSS classes need to be considered clickable");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> TypeableClasses = Tag.from("TypeableClasses", (Class<List<String>>) (Class<?>) List.class, 
			"Indicate which web CSS classes need to be considered typeable");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> DeniedExtensions = Tag.from("DeniedExtensions", (Class<List<String>>) (Class<?>) List.class, 
			"Indicate which web URL extensions need to be ignored when testing");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> DomainsAllowed = Tag.from("DomainsAllowed", (Class<List<String>>) (Class<?>) List.class, 
			"Indicate which web URL domains need to be ignored when testing");

	public static final Tag<Boolean> FollowLinks = Tag.from("FollowLinks", Boolean.class, 
			"Indicate if allowing to follow links opened in new tabs");

	public static final Tag<Boolean> BrowserFullScreen = Tag.from("BrowserFullScreen", Boolean.class, 
			"Indicate if perform the web testing with the browser in full screen");

	public static final Tag<Boolean> SwitchNewTabs = Tag.from("SwitchNewTabs", Boolean.class, 
			"Indicate if switch to a new web tab if opened");

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

	public static final Tag<String> ExtendedSettingsFile = Tag.from("ExtendedSettingsFile", String.class, 
			"Relative path to extended XML settings file");

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

	public static final Tag<Double> ReplayRetryTime = Tag.from("ReplayRetryTime", Double.class, 
			"Inside the replay mode, establishes the time window in seconds for trying to replay a UI action of a replayed test sequence");

	public static final Tag<Boolean> StopGenerationOnFault = Tag.from("StopGenerationOnFault", Boolean.class, 
			"Sets whether to finish a test in the presence of a fail (e.g. Suspicious Tag detected)");

	public static final Tag<Double> TimeToFreeze = Tag.from("TimeToFreeze", Double.class, 
			"Sets the time window, in seconds, for which to wait for a not responding SUT. After that, the test will finish with a fail");

	public static final Tag<Boolean> UseRecordedActionDurationAndWaitTimeDuringReplay = Tag.from("UseRecordedActionDurationAndWaitTimeDuringReplay", Boolean.class, 
			"Inside the replay mode sets whether to use the action duration (ActionDuration and TimeToWaitAfterAction) as specified in the generated test sequence");

	public static final Tag<Boolean> VisualizeActions = Tag.from("VisualizeActions", Boolean.class, 
			"Sets whether to display overlay information, inside the SPY mode, for all the UI actions derived from the test set up");

	public static final Tag<Boolean> KeyBoardListener = Tag.from("KeyBoardListener", Boolean.class, 
			"Sets whether to listen to keyboard shortcuts during the exceution");

	public static final Tag<Boolean> UseSystemActions = Tag.from("UseSystemActions", Boolean.class, 
			"ANDROID: Indicate if add system calls");

	public static final Tag<String> PathToReplaySequence = Tag.from("PathToReplaySequence", String.class, 
			"The sequence to REPLAY is the one indicated in this parameter");

	public static final Tag<Double> RefreshSpyCanvas = Tag.from("RefreshSpyCanvas", Double.class, 
			"Time in milliseconds that indicates the frequency of refreshing the screen in SPY mode");

	public static final Tag<Boolean> AlwaysCompile = Tag.from("AlwaysCompile", Boolean.class, 
			"Compile the protocol before launching the selected TESTAR mode");

	public static final Tag<Boolean> FlashFeedback = Tag.from("FlashFeedback", Boolean.class, 
			"Sets whether to draw a feedback message in the top-left corner of the screen");

	public static final Tag<Double> MaxReward = Tag.from("MaxReward", Double.class, 
			"MaxReward value for the QLearningActionSelector");

	public static final Tag<Double> Discount = Tag.from("Discount", Double.class, 
			"Discount value for the QLearningActionSelector");

	public static final Tag<Boolean> CreateWidgetInfoJsonFile = Tag.from("CreateWidgetInfoJsonFile", Boolean.class, 
			"Sets whether create a JSON file with information about widgets and their location on the screenshot");

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> MyClassPath = Tag.from("MyClassPath", (Class<List<String>>) (Class<?>) List.class, 
			"The relative path that contains the TESTAR protocols");

	public static final Tag<String> ProtocolCompileDirectory = Tag.from("ProtocolCompileDirectory", String.class, 
			"The relative path on which compile the TESTAR protocols and create the class files");

	public static final Tag<String> OutputDir = Tag.from("OutputDir", String.class, 
			"The relative path to save TESTAR output results");

	public static final Tag<String> TempDir = Tag.from("TempDir", String.class, 
			"The relative path to temporarily  save TESTAR files");
	
	public static final Tag<Boolean> ReportInHTML = Tag.from("ReportInHTML", Boolean.class,
			 "Sets whether to create a HTML report");
	
	public static final Tag<Boolean> ReportInPlainText = Tag.from("ReportInPlainText", Boolean.class,
			 "Sets whether to create a plain text report");

	/**
	 * Other settings
	 */

	public static final Tag<Double> FaultThreshold = Tag.from("FaultThreshold", Double.class);

	@SuppressWarnings("unchecked")
	public static final Tag<List<String>> Delete = Tag.from("Delete", (Class<List<String>>) (Class<?>) List.class);

	@SuppressWarnings("unchecked")
	public static final Tag<List<Pair<String, String>>> CopyFromTo = Tag.from("CopyFromTo", (Class<List<Pair<String, String>>>) (Class<?>) List.class);

	/*
	//TODO web driver settings for login feature
	public static final Tag<Pair<String, String>> Login = Tag.from("Login", (Class<Pair<String, String>>) (Class<?>) Pair.class);
	// Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
	public static final Tag<Pair<String, String>> Username = Tag.from("Username", (Class<Pair<String, String>>) (Class<?>) Pair.class);
	public static final Tag<Pair<String, String>> Password = Tag.from("Password", (Class<Pair<String, String>>) (Class<?>) Pair.class);
	 */
}
