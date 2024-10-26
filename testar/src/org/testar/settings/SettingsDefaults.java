/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 - 2024 Open Universiteit - www.ou.nl
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

import org.testar.monkey.Main;
import org.testar.monkey.Pair;
import org.testar.monkey.RuntimeControlsProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testar.monkey.ConfigTags.*;

public class SettingsDefaults {

	private SettingsDefaults() {}

	public static List<Pair<?, ?>> getSettingsDefaults(){

		List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();

		defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
		defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
		defaults.add(Pair.from(FaultThreshold, 0.1));
		defaults.add(Pair.from(LogLevel, 1));
		defaults.add(Pair.from(Mode, RuntimeControlsProtocol.Modes.Spy));
		defaults.add(Pair.from(OutputDir, Main.outputDir));
		defaults.add(Pair.from(TempDir, Main.tempDir));
		defaults.add(Pair.from(OnlySaveFaultySequences, false));
		defaults.add(Pair.from(PathToReplaySequence, Main.tempDir));
		defaults.add(Pair.from(ActionDuration, 0.1));
		defaults.add(Pair.from(TimeToWaitAfterAction, 0.1));
		defaults.add(Pair.from(VisualizeActions, false));
		defaults.add(Pair.from(KeyBoardListener, true));
		defaults.add(Pair.from(SequenceLength, 10));
		defaults.add(Pair.from(ReplayRetryTime, 30.0));
		defaults.add(Pair.from(Sequences, 1));
		defaults.add(Pair.from(MaxTime, 31536000.0));
		defaults.add(Pair.from(StartupTime, 8.0));
		defaults.add(Pair.from(SUTConnectorValue, ""));
		defaults.add(Pair.from(Delete, new ArrayList<String>()));
		defaults.add(Pair.from(CopyFromTo, new ArrayList<Pair<String, String>>()));
		defaults.add(Pair.from(SuspiciousTags, "(?!x)x"));
		defaults.add(Pair.from(ClickFilter, "(?!x)x"));
		defaults.add(Pair.from(MyClassPath, Arrays.asList(Main.settingsDir)));
		defaults.add(Pair.from(ProtocolClass, "org.testar.monkey.DefaultProtocol"));
		defaults.add(Pair.from(ForceForeground, true));
		defaults.add(Pair.from(UseRecordedActionDurationAndWaitTimeDuringReplay, true));
		defaults.add(Pair.from(StopGenerationOnFault, true));
		defaults.add(Pair.from(TimeToFreeze, 10.0));
		defaults.add(Pair.from(RefreshSpyCanvas, 0.5));
		defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
		defaults.add(Pair.from(MaxReward, 9999999.0));
		defaults.add(Pair.from(Discount, .95));
		defaults.add(Pair.from(AccessBridgeEnabled, false));
		defaults.add(Pair.from(SUTProcesses, ""));
		defaults.add(Pair.from(StateModelEnabled, false));
		defaults.add(Pair.from(DataStore, ""));
		defaults.add(Pair.from(DataStoreType, ""));
		defaults.add(Pair.from(DataStoreServer, ""));
		defaults.add(Pair.from(DataStoreDirectory, ""));
		defaults.add(Pair.from(DataStoreDB, ""));
		defaults.add(Pair.from(DataStoreUser, ""));
		defaults.add(Pair.from(DataStorePassword, ""));
		defaults.add(Pair.from(DataStoreMode, ""));
		defaults.add(Pair.from(ResetDataStore, false));
		defaults.add(Pair.from(ApplicationName, ""));
		defaults.add(Pair.from(ApplicationVersion, ""));
		defaults.add(Pair.from(ActionSelectionAlgorithm, "random"));
		defaults.add(Pair.from(StateModelStoreWidgets, true));
		defaults.add(Pair.from(AlwaysCompile, true));
		defaults.add(Pair.from(ProcessListenerEnabled, false));
		defaults.add(Pair.from(SuspiciousProcessOutput, "(?!x)x"));
		defaults.add(Pair.from(ProcessLogs, ".*.*"));
		defaults.add(Pair.from(OverrideWebDriverDisplayScale, ""));
		defaults.add(Pair.from(CreateWidgetInfoJsonFile, false));
		defaults.add(Pair.from(FormFillingAction, false));
		defaults.add(Pair.from(ReportInHTML, true));
		defaults.add(Pair.from(ReportInPlainText, false));

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
		defaults.add(Pair.from(FlashFeedback, true));
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

		defaults.add(Pair.from(DomainsAllowed, new ArrayList<String>() {
			{
				add("www.ou.nl");
				add("mijn.awo.ou.nl");
				add("login.awo.ou.nl");
			}
		}));

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

		return defaults;
	}

}
