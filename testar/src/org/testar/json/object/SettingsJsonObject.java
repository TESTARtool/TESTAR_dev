/***************************************************************************************************
 *
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
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

package org.testar.json.object;

import org.apache.commons.lang3.StringUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SettingsJsonObject{

	// SUT execution
	String sutConnector;
	String sutConnectorValue;
	String Mode;
	boolean AccessBridgeEnabled;
	int SequenceLength;
	int Sequences;
	boolean forceForeground;
	String OverrideWebDriverDisplayScale;
	boolean OnlySaveFaultySequences;
	boolean StopGenerationOnFault;
	String ProcessesToKillDuringTest;

	// Oracles
	String suspiciousTitles;
	double timeToFreeze;
	boolean ProcessListenerEnabled;
	String SuspiciousProcessOutput;
	String ProcessLogs;

	// Filtering
	String clickFilter;

	// Execution Times
	double actionDuration;
	double timeToWaitAfterAction;
	double MaxTime;
	double StartupTime;

	// Files
	String protocolClass;
	String OutputDir;
	String TempDir;
	String MyClassPath;

	// RL values
	double maxReward;
	double discount;

	//State Model settings
	boolean StateModelEnabled;
	String DataStore;
	String DataStoreType;
	String DataStoreServer;
	String DataStoreDB;
	String DataStoreUser;
	String DataStorePassword;
	String DataStoreMode;
	String DataStoreDirectory;
	boolean ResetDataStore;
	String ApplicationName;
	String ApplicationVersion;
	String ActionSelectionAlgorithm;
	boolean StateModelStoreWidgets;
	String AbstractStateAttributes;
	String PreviousApplicationName;
	String PreviousApplicationVersion;
	boolean StateModelDifferenceAutomaticReport;

	// PKM connection
	String PKMaddress;
	String PKMport;
	String PKMdatabase;
	String PKMusername;
	String PKMkey;

	@JsonCreator
	public SettingsJsonObject(Settings settings) {
		// SUT execution
		this.sutConnector = settings.get(ConfigTags.SUTConnector);
		this.sutConnectorValue = settings.get(ConfigTags.SUTConnectorValue);
		this.Mode = settings.get(ConfigTags.Mode).toString();
		this.AccessBridgeEnabled = settings.get(ConfigTags.AccessBridgeEnabled);
		this.SequenceLength = settings.get(ConfigTags.SequenceLength);
		this.Sequences = settings.get(ConfigTags.Sequences);
		this.forceForeground = settings.get(ConfigTags.ForceForeground);
		this.OverrideWebDriverDisplayScale = settings.get(ConfigTags.OverrideWebDriverDisplayScale);
		this.OnlySaveFaultySequences = settings.get(ConfigTags.OnlySaveFaultySequences);
		this.StopGenerationOnFault = settings.get(ConfigTags.StopGenerationOnFault);
		this.ProcessesToKillDuringTest = settings.get(ConfigTags.ProcessesToKillDuringTest);

		// Oracles
		this.suspiciousTitles = settings.get(ConfigTags.SuspiciousTitles);
		this.timeToFreeze = settings.get(ConfigTags.TimeToFreeze);
		this.ProcessListenerEnabled = settings.get(ConfigTags.ProcessListenerEnabled);
		this.SuspiciousProcessOutput = settings.get(ConfigTags.SuspiciousProcessOutput);
		this.ProcessLogs = settings.get(ConfigTags.ProcessLogs);

		// Filtering
		this.clickFilter = settings.get(ConfigTags.ClickFilter);

		// Execution Times
		this.actionDuration = settings.get(ConfigTags.ActionDuration);
		this.timeToWaitAfterAction = settings.get(ConfigTags.TimeToWaitAfterAction);
		this.MaxTime = settings.get(ConfigTags.MaxTime);
		this.StartupTime = settings.get(ConfigTags.StartupTime);

		// Files
		this.protocolClass = settings.get(ConfigTags.ProtocolClass);
		this.OutputDir = settings.get(ConfigTags.OutputDir);
		this.TempDir = settings.get(ConfigTags.TempDir);
		this.MyClassPath = StringUtils.join(settings.get(ConfigTags.MyClassPath), ",");

		// RL values
		this.maxReward = settings.get(ConfigTags.MaxReward);
		this.discount = settings.get(ConfigTags.Discount);

		//State Model settings
		this.StateModelEnabled = settings.get(ConfigTags.StateModelEnabled);
		this.DataStore = settings.get(ConfigTags.DataStore);
		this.DataStoreType = settings.get(ConfigTags.DataStoreType);
		this.DataStoreServer = settings.get(ConfigTags.DataStoreServer);
		this.DataStoreDB = settings.get(ConfigTags.DataStoreDB);
		this.DataStoreUser = settings.get(ConfigTags.DataStoreUser);
		this.DataStorePassword = settings.get(ConfigTags.DataStorePassword);
		this.DataStoreMode = settings.get(ConfigTags.DataStoreMode);
		this.DataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
		this.ResetDataStore = settings.get(ConfigTags.ResetDataStore);
		this.ApplicationName = settings.get(ConfigTags.ApplicationName);
		this.ApplicationVersion = settings.get(ConfigTags.ApplicationVersion);
		this.ActionSelectionAlgorithm = settings.get(ConfigTags.ActionSelectionAlgorithm);
		this.StateModelStoreWidgets = settings.get(ConfigTags.StateModelStoreWidgets);
		this.AbstractStateAttributes = StringUtils.join(settings.get(ConfigTags.AbstractStateAttributes), ",");
		this.PreviousApplicationName = settings.get(ConfigTags.PreviousApplicationName);
		this.PreviousApplicationVersion = settings.get(ConfigTags.PreviousApplicationVersion);
		this.StateModelDifferenceAutomaticReport = settings.get(ConfigTags.StateModelDifferenceAutomaticReport);

		// PKM connection
		this.PKMaddress = settings.get(ConfigTags.PKMaddress);
		this.PKMport = settings.get(ConfigTags.PKMport);
		this.PKMdatabase = settings.get(ConfigTags.PKMdatabase);
		this.PKMusername = settings.get(ConfigTags.PKMusername);
		this.PKMkey = "TEMP_TESTAR_TOOL_KEY_STARTS:" + StringUtils.substring(settings.get(ConfigTags.PKMkey), 0, 5);
	}

}