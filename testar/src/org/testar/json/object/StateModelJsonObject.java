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


import java.util.SortedSet;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StateModelJsonObject {

	String timestamp;
	String url;
	SutJsonObject sut;
	ToolJsonObject tool;
	String stateModelDataStore;
	String stateModelDataStoreType;
	String stateModelDataStoreServer;
	String stateModelDataStoreDirectory;
	String stateModelDataStoreDB;
	String stateModelDataStoreUser;
	String stateModelDataStorePassword;
	String stateModelIdentifier;
	String stateModelAppName;
	String stateModelAppVersion;
	StateModelDifferenceJsonObject stateModelDifference;
	String abstractionId;
	boolean deterministic;
	long unvisitedAbstractActions;
	long numberAbstractStates;
	long numberAbstractActions;
	long numberConcreteStates;
	long numberConcreteActions;
	boolean storeWidgets;
	long numberWidgets;
	long numberTestSequences;
	SortedSet<StateModelTestSequenceJsonObject> testSequences;

	@JsonCreator
	public StateModelJsonObject(String timestamp, String url, SutJsonObject sut, ToolJsonObject tool, Settings settings,
			String stateModelAppName, String stateModelAppVersion, String stateModelIdentifier,
			String abstractionId, boolean deterministic, long unvisitedAbstractActions,
			long numberAbstractStates, long numberAbstractActions, long numberConcreteStates, long numberConcreteActions,
			boolean storeWidgets, long numberWidgets, long numberTestSequences) {
		this.timestamp = timestamp;
		this.url = url;
		this.sut = sut;
		this.tool = tool;
		this.stateModelDataStore = settings.get(ConfigTags.DataStore);
		this.stateModelDataStoreType = settings.get(ConfigTags.DataStoreType);
		this.stateModelDataStoreServer = settings.get(ConfigTags.DataStoreServer);
		this.stateModelDataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
		this.stateModelDataStoreDB = settings.get(ConfigTags.DataStoreDB);
		this.stateModelDataStoreUser = settings.get(ConfigTags.DataStoreUser);
		this.stateModelDataStorePassword = settings.get(ConfigTags.DataStorePassword);
		this.stateModelIdentifier = stateModelIdentifier;
		this.stateModelAppName = stateModelAppName;
		this.stateModelAppVersion = stateModelAppVersion;
		this.abstractionId = abstractionId;
		this.deterministic = deterministic;
		this.unvisitedAbstractActions = unvisitedAbstractActions;
		this.numberAbstractStates = numberAbstractStates;
		this.numberAbstractActions = numberAbstractActions;
		this.numberConcreteStates = numberConcreteStates;
		this.numberConcreteActions = numberConcreteActions;
		this.storeWidgets = storeWidgets;
		this.numberWidgets = numberWidgets;
		this.numberTestSequences = numberTestSequences;
	}

	public void setStateModelDifference(StateModelDifferenceJsonObject stateModelDifference) {
		this.stateModelDifference = stateModelDifference;
	}

	public void setTestSequences(SortedSet<StateModelTestSequenceJsonObject> testSequences) {
		this.testSequences = testSequences;
	}

}