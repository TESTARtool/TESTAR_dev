/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.StateModelTags;

public class SettingsFileStructure {

	private SettingsFileStructure() {}

	public static String getTestSettingsStructure() {
		// First, create the main structure that contains specific format and descriptions
		String mainString = String.join(System.getProperty("line.separator")
				, "#################################################################"
				, "# TESTAR mode"
				, "#"
				, "# Set the mode you want TESTAR to start in: Spy, Generate, Replay"
				, "#################################################################"
				, ""
				, ConfigTags.Mode.name() + " = "
				, ""
				, "#################################################################"
				, "# Connect to the System Under Test (SUT)"
				, "#"
				, "# Indicate how you want to connect to the SUT:"
				, "#"
				, "# SUTCONNECTOR = COMMAND_LINE, SUTConnectorValue property must be a command line that"
				, "# starts the SUT."
				, "# It should work from a Command Prompt terminal window (e.g. java - jar SUTs/calc.jar )."
				, "# For web applications, follow the next format: web_browser_path SUT_URL."
				, "#"
				, "# SUTCONNECTOR = SUT_WINDOW_TITLE, then SUTConnectorValue property must be the title displayed"
				, "# in the SUT main window. The SUT must be manually started and closed."
				, "#"
				, "# SUTCONNECTOR = SUT_PROCESS_NAME: SUTConnectorValue property must be the process name of the SUT."
				, "# The SUT must be manually started and closed."
				, "#################################################################"
				, ""
				, ConfigTags.SUTConnector.name() + " = "
				, ConfigTags.SUTConnectorValue.name() + " = "
				, ""
				, "#################################################################"
				, "# Regular expressions that indicates which processes conform the SUT"
				, "#"
				, "# This regex expression can be used to attach TESTAR to a multi-processes SUT"
				, "#################################################################"
				, ""
				, ConfigTags.SUTProcesses.name() + " = "
				, ""
				, "#################################################################"
				, "# Java Swing applications & Access Bridge Enabled"
				, "#"
				, "# Activate the Java Access Bridge in your Windows System:"
				, "#		(Control Panel / Ease of Access / Ease of Access Center / Make the computer easier to see)"
				, "#"
				, "# Enable the variable Access Bridge Enabled in TESTAR as true"
				, "#################################################################"
				, ""
				, ConfigTags.AccessBridgeEnabled.name() + " = "
				, ""
				, "#################################################################"
				, "# Sequences"
				, "#"
				, "# Number of sequences and the length of these sequences"
				, "#################################################################"
				, ""
				, ConfigTags.Sequences.name() + " = "
				, ConfigTags.SequenceLength.name() + " = "
				, ""
				, "#################################################################"
				, "# SUT Protocol"
				, "#"
				, "# ProtocolClass: " + ConfigTags.ProtocolClass.getDescription()
				, "# AlwaysCompile: " + ConfigTags.AlwaysCompile.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ProtocolClass.name() + " = "
				, ConfigTags.AlwaysCompile.name() + " = "
				, ""
				, "#################################################################"
				, "# Actionfilter"
				, "#"
				, "# Regular expression and Tags to apply them."
				, "# More filters can be added in Spy mode, "
				, "# these will be added to the protocol_filter.xml file."
				, "#################################################################"
				, ""
				, ConfigTags.ClickFilter.name() + " = "
				, ConfigTags.TagsToFilter.name() + " = "
				, ""
				, "#################################################################"
				, "# Processfilter"
				, "#"
				, "# Regular expression. Kill the processes that your SUT can start up"
				, "# but that you do not want to test."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessesToKillDuringTest.name() + " = "
				, ""
				, "#################################################################"
				, "# Oracles based on suspicious tag values"
				, "#"
				, "# Regular expression and Tags to apply them"
				, "#################################################################"
				, ""
				, ConfigTags.SuspiciousTags.name() + " = "
				, ConfigTags.TagsForSuspiciousOracle.name() + " = "
				, ""
				, "#################################################################"
				, "# Oracles based on Suspicious Outputs detected by Process Listeners"
				, "#"
				, "# Requires ProcessListenerEnabled"
				, "# (Only available for desktop applications through COMMAND_LINE)"
				, "#"
				, "# Regular expression SuspiciousProcessOutput contains the specification"
				, "# of what is considered to be suspicious output."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessListenerEnabled.name() + " = "
				, ConfigTags.SuspiciousProcessOutput.name() + " = "
				, ""
				, "#################################################################"
				, "# Process Logs"
				, "#"
				, "# Required ProcessListenerEnabled"
				, "# (Only available for desktop applications through COMMAND_LINE)"
				, "#"
				, "# Allow TESTAR to store execution logs coming from the processes."
				, "# You can use the regular expression ProcessLogs below to filter"
				, "# the logs. Use .*.* if you want to store all the outputs of the "
				, "# process."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessLogs.name() + " = "
				, ""
				, "#################################################################"
				, "# Oracles based on suspicious titles in SUT output files"
				, "#################################################################"
				, ""
				, ConfigTags.LogOracleEnabled.name() + " = "
				, ConfigTags.LogOracleRegex.name() + " = "
				, ConfigTags.LogOracleCommands.name() + " = "
				, ConfigTags.LogOracleFiles.name() + " = "
				, ""
				, "#################################################################"
				, "# Time configurations"
				, "#"
				, "# ActionDuration: " + ConfigTags.ActionDuration.getDescription()
				, "# TimeToWaitAfterAction: " + ConfigTags.TimeToWaitAfterAction.getDescription()
				, "# StartupTime: " + ConfigTags.StartupTime.getDescription()
				, "# MaxTime: " + ConfigTags.MaxTime.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ActionDuration.name() + " = "
				, ConfigTags.TimeToWaitAfterAction.name() + " = "
				, ConfigTags.StartupTime.name() + " = "
				, ConfigTags.MaxTime.name() + " = "
				, ""
				, "#################################################################"
				, "# System Under Test (SUT) identification"
				, "#"
				, "# ApplicationName: " + ConfigTags.ApplicationName.getDescription()
				, "# ApplicationVersion: " + ConfigTags.ApplicationVersion.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ApplicationName.name() + " = "
				, ConfigTags.ApplicationVersion.name() + " = "
				, ""
				, "#################################################################"
				, "# State model inference settings"
				, "#"
				, "# StateModelEnabled: " + StateModelTags.StateModelEnabled.getDescription()
				, "# DataStore: " + StateModelTags.DataStore.getDescription()
				, "# DataStoreType: " + StateModelTags.DataStoreType.getDescription()
				, "# DataStoreServer: " + StateModelTags.DataStoreServer.getDescription()
				, "# DataStoreDirectory: " + StateModelTags.DataStoreDirectory.getDescription()
				, "# DataStoreDB: " + StateModelTags.DataStoreDB.getDescription()
				, "# DataStoreUser: " + StateModelTags.DataStoreUser.getDescription()
				, "# DataStorePassword: " + StateModelTags.DataStorePassword.getDescription()
				, "# DataStoreMode: " + StateModelTags.DataStoreMode.getDescription()
				, "# ActionSelectionAlgorithm: " + StateModelTags.ActionSelectionAlgorithm.getDescription()
				, "# StateModelStoreWidgets: " + StateModelTags.StateModelStoreWidgets.getDescription()
				, "# ResetDataStore: " + StateModelTags.ResetDataStore.getDescription()
				, "#################################################################"
				, ""
				, StateModelTags.StateModelEnabled.name() + " = "
				, StateModelTags.DataStore.name() + " = "
				, StateModelTags.DataStoreType.name() + " = "
				, StateModelTags.DataStoreServer.name() + " = "
				, StateModelTags.DataStoreDirectory.name() + " = "
				, StateModelTags.DataStoreDB.name() + " = "
				, StateModelTags.DataStoreUser.name() + " = "
				, StateModelTags.DataStorePassword.name() + " = "
				, StateModelTags.DataStoreMode.name() + " = "
				, StateModelTags.ActionSelectionAlgorithm.name() + " = "
				, StateModelTags.StateModelStoreWidgets.name() + " = "
				, StateModelTags.ResetDataStore.name() + " = "
				, ""
				, "#################################################################"
				, "# State identifier attributes"
				, "#"
				, "# Specify the widget attributes that you wish to use in constructing"
				, "# the widget and state hash strings. Use a comma separated list."
				, "#################################################################"
				, ""
				, ConfigTags.AbstractStateAttributes.name() + " = "
				, ""
				, "#################################################################"
				, "# LLM agents settings"
				, "#"
				, "# Specify the settings to connect with an LLM server and"
				, "# the prompt configurations to guide the LLM agent in accomplishing a test goal."
				, "#################################################################"
				, ""
				, ConfigTags.LlmPlatform.name() + " = "
				, ConfigTags.LlmModel.name() + " = "
				, ConfigTags.LlmHostUrl.name() + " = "
				, ConfigTags.LlmAuthorizationHeader.name() + " = "
				, ConfigTags.LlmActionFewshotFile.name() + " = "
				, ConfigTags.LlmOracleFewshotFile.name() + " = "
				, ConfigTags.LlmTemperature.name() + " = "
				, ConfigTags.LlmHistorySize.name() + " = "
				, ConfigTags.LlmStateless.name() + " = "
				, ConfigTags.LlmTestGoals.name() + " = "
				, ""
				, "#################################################################"
				, "# WebDriver features"
				, "#"
				, "# ClickableClasses: " + ConfigTags.ClickableClasses.getDescription()
				, "# TypeableClasses: " + ConfigTags.TypeableClasses.getDescription()
				, "# DeniedExtensions: " + ConfigTags.DeniedExtensions.getDescription()
				, "# WebDomainsAllowed: " + ConfigTags.WebDomainsAllowed.getDescription()
				, "# WebPathsAllowed: " + ConfigTags.WebPathsAllowed.getDescription()
				, "# FollowLinks: " + ConfigTags.FollowLinks.getDescription()
				, "# BrowserFullScreen: " + ConfigTags.BrowserFullScreen.getDescription()
				, "# SwitchNewTabs: " + ConfigTags.SwitchNewTabs.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ClickableClasses.name() + " = "
				, ConfigTags.TypeableClasses.name() + " = "
				, ConfigTags.DeniedExtensions.name() + " = "
				, ConfigTags.WebDomainsAllowed.name() + " = "
				, ConfigTags.WebPathsAllowed.name() + " = "
				, ConfigTags.FollowLinks.name() + " = "
				, ConfigTags.BrowserFullScreen.name() + " = "
				, ConfigTags.SwitchNewTabs.name() + " = "
				, ""
				, "#################################################################"
				, "# WebDriver advanced state fetcher configuration"
				, "#"
				, "# WebIgnoredTags: " + ConfigTags.WebIgnoredTags.getDescription()
				, "# WebIgnoredAttributes: " + ConfigTags.WebIgnoredAttributes.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.WebIgnoredTags.name() + " = "
				, ConfigTags.WebIgnoredAttributes.name() + " = "
				, ""
				, "#################################################################"
				, "# WebDriver Browser Console Oracles"
				, "#"
				, "# WebConsoleErrorOracle: " + ConfigTags.WebConsoleErrorOracle.getDescription()
				, "# WebConsoleErrorPattern: " + ConfigTags.WebConsoleErrorPattern.getDescription()
				, "# WebConsoleWarningOracle: " + ConfigTags.WebConsoleWarningOracle.getDescription()
				, "# WebConsoleWarningPattern: " + ConfigTags.WebConsoleWarningPattern.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.WebConsoleErrorOracle.name() + " = "
				, ConfigTags.WebConsoleErrorPattern.name() + " = "
				, ConfigTags.WebConsoleWarningOracle.name() + " = "
				, ConfigTags.WebConsoleWarningPattern.name() + " = "
				, ""
				, "#################################################################"
				, "# Override display scale"
				, "#"
				, "# Overrides the displayscale obtained from the system."
				, "# Can solve problems when the mouse clicks are not aligned with"
				, "# the elements on the screen. This can easily be detected when"
				, "# running the spy mode. For example hover over a text element and"
				, "# the popup window should appear with information about the"
				, "# element, if the popup window is not shown or when the mouse is"
				, "# located somewhere else you can try to override the displayscale"
				, "# Values should be provided as doubles (1.5)."
				, "#################################################################"
				, ""
				, ConfigTags.OverrideWebDriverDisplayScale.name() + " = "
				, ""
				, "#################################################################"
				, "# Settings (string) that can be used for user specified protocols"
				, "#################################################################"
				, ""
				, ConfigTags.ProtocolSpecificSetting_1.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_2.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_3.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_4.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_5.name() + " = "
				, ""
				, "#################################################################"
				, "# Extended settings file"
				, "#"
				, "# Relative path to extended settings file."
				, "#################################################################"
				, ""
				, ConfigTags.ExtendedSettingsFile.name() + " = "
				, ""
				, "#################################################################"
				, "# Set the types of reports to create"
				, "#################################################################"
				, ""
				, ConfigTags.ReportInHTML.name() + " = "
				, ConfigTags.ReportInPlainText.name() + " = "
				, ""
				, "#################################################################"
				, "# Advanced test oracles"
				, "#################################################################"
				, ""
				, ConfigTags.ExtendedOracles.name() + " = "
				, ConfigTags.ExternalOracles.name() + " = "
				, ""
				, "#################################################################"
				, "# Settings to configure code coverage features"
				, "#################################################################"
				, ""
				, ConfigTags.JacocoCoverage.name() + " = "
				, ConfigTags.JacocoCoverageIpAddress.name() + " = "
				, ConfigTags.JacocoCoveragePort.name() + " = "
				, ConfigTags.JacocoCoverageClasses.name() + " = "
				, ConfigTags.JacocoCoverageAccumulate.name() + " = "
				, ""
				, "#################################################################"
				, "# Replay mode features"
				, "# GenerateReplayableSequence: " + ConfigTags.GenerateReplayableSequence.getDescription()
				, "# PathToReplaySequence: " + ConfigTags.PathToReplaySequence.getDescription()
				, "# ReplayRetryTime: " + ConfigTags.ReplayRetryTime.getDescription()
				, "# UseRecordedActionDurationAndWaitTimeDuringReplay: " + ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.GenerateReplayableSequence.name() + " = "
				, ConfigTags.PathToReplaySequence.name() + " = "
				, ConfigTags.ReplayRetryTime.name() + " = "
				, ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay.name() + " = "
				, ""
				);

		// Second, create a list of secondary configuration tags settings
		// To add their descriptions to the file
		List<Tag<?>> secondarySettingsList = new ArrayList<>();
		secondarySettingsList.add(ConfigTags.ShowVisualSettingsDialogOnStartup);
		secondarySettingsList.add(ConfigTags.ForceForeground);
		secondarySettingsList.add(ConfigTags.SpyTagAttributes);
		secondarySettingsList.add(ConfigTags.VisualizeActions);
		secondarySettingsList.add(ConfigTags.FormFillingAction);
		secondarySettingsList.add(ConfigTags.LogLevel);
		secondarySettingsList.add(ConfigTags.OnlySaveFaultySequences);
		secondarySettingsList.add(ConfigTags.StopGenerationOnFault);
		secondarySettingsList.add(ConfigTags.TimeToFreeze);
		secondarySettingsList.add(ConfigTags.UseSystemActions);
		secondarySettingsList.add(ConfigTags.RefreshSpyCanvas);
		secondarySettingsList.add(ConfigTags.FlashFeedback);
		secondarySettingsList.add(ConfigTags.MaxReward);
		secondarySettingsList.add(ConfigTags.Discount);
		secondarySettingsList.add(ConfigTags.CreateWidgetInfoJsonFile);
		secondarySettingsList.add(ConfigTags.MyClassPath);
		secondarySettingsList.add(ConfigTags.ProtocolCompileDirectory);
		secondarySettingsList.add(ConfigTags.OutputDir);
		secondarySettingsList.add(ConfigTags.TempDir);

		StringJoiner secondaryString = new StringJoiner(System.getProperty("line.separator"));
		for(Tag<?> set : secondarySettingsList) {
			// Make sure it contains a description
			if(!set.getDescription().isEmpty()) {
				secondaryString.add("#################################################################");
				secondaryString.add("# " + set.getDescription());
				secondaryString.add("#################################################################");
				secondaryString.add("");
				secondaryString.add(set.name() + " = ");
				secondaryString.add("");
			}
		}

		// Finally, create the structure for other settings
		StringJoiner otherString = new StringJoiner(System.getProperty("line.separator"));
		otherString.add("#################################################################");
		otherString.add("# Other settings");
		otherString.add("#################################################################");
		otherString.add("");

		return mainString
				.concat(System.getProperty("line.separator") + secondaryString.toString())
				.concat(System.getProperty("line.separator") + otherString.toString());
	}

}
