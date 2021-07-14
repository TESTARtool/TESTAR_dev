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

package org.testar.json;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Pair;
import org.testar.json.object.StateModelDifferenceJsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * OLD implementation
 */
public class JsonArtefactStateModelDifference {

	private JsonArtefactStateModelDifference() {}

	/*@JsonCreator
	public static void createModelDifferenceArtefact(List<String> stateModelOne, List<String> stateModelTwo,
			String directory, Set<String> disappearedAbstractStates, Set<String> newAbstractStates,
			Map<String, Set<Pair<String,String>>> disappearedActions, Map<String, Set<Pair<String,String>>> newActions) {

		StateModelDifferenceJsonObject modelDiff = new StateModelDifferenceJsonObject(
				stateModelOne, stateModelTwo,
				disappearedAbstractStates, newAbstractStates,
				disappearedActions, newActions);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String outputFile = directory + "ArtefactStateModelDifference.json";

		try{
			FileWriter fileWriter = new FileWriter(outputFile);
			gson.toJson(modelDiff, fileWriter);
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Created JSON Model Difference artefact: " + outputFile);
		}catch(Exception e){
			System.out.println("ERROR! Creating JSON ArtefactModelSifference!");
		}

	}*/

}