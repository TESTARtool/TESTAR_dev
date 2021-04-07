/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2021 Open Universiteit - www.ou.nl
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

import java.util.Arrays;
import java.util.List;

import org.fruit.Pair;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StateModelDifferenceJsonObject {

	boolean existsPreviousStateModel;
	String previousStateModelAppName;
	String previousStateModelAppVersion;
	int numberDisappearedAbstractStates;
	int numberNewAbstractStates;
	String stateModelDifferenceReport;
	List<Pair<String, List<String>>> specificWidgetTreeDifference;

	@JsonCreator
	public StateModelDifferenceJsonObject() {
		// Default values are empty parameters, by default we consider previous model doesn't exist
		this.existsPreviousStateModel = false;
		this.previousStateModelAppName = "";
		this.previousStateModelAppVersion = "";
		this.numberDisappearedAbstractStates = -1;
		this.numberNewAbstractStates = -1;
		this.stateModelDifferenceReport = "Previous State Model doesn't exist";
		this.specificWidgetTreeDifference = Arrays.asList(new Pair<>("No specific widgetTreeDifference information", Arrays.asList("")));
	}
	
	public void setPreviousStateModelAppName(String previousStateModelAppName) {
		this.previousStateModelAppName = previousStateModelAppName;
	}

	public void setPreviousStateModelAppVersion(String previousStateModelAppVersion) {
		this.previousStateModelAppVersion = previousStateModelAppVersion;
	}

	public void setExistsPreviousStateModel(boolean existsPreviousStateModel) {
		this.existsPreviousStateModel = existsPreviousStateModel;
	}

	public void setNumberDisappearedAbstractStates(int numberDisappearedAbstractStates) {
		this.numberDisappearedAbstractStates = numberDisappearedAbstractStates;
	}

	public void setNumberNewAbstractStates(int numberNewAbstractStates) {
		this.numberNewAbstractStates = numberNewAbstractStates;
	}

	public void setStateModelDifferenceReport(String stateModelDifferenceReport) {
		this.stateModelDifferenceReport = stateModelDifferenceReport;
	}

	public String getStateModelDifferenceReport() {
		return stateModelDifferenceReport;
	}

	public void setSpecificWidgetTreeDifference(List<Pair<String, List<String>>> widgetTreeDifference) {
	    this.specificWidgetTreeDifference = widgetTreeDifference;
	}

}