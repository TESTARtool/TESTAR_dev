/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
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

package org.fruit.monkey;

import com.orientechnologies.orient.core.sql.executor.OResultSet;

import nl.ou.testar.StateModel.Exception.StateModelException;
import org.testar.statemodel.StateModelManager;

public class ReplayStateModelUtil {

	private ReplayStateModelUtil() {}

	/**
	 * Query the OrientDB database to get the model identifier using the name and version of the model we want to replay
	 * 
	 * @param stateModelManager
	 * @param replayName
	 * @param replayVersion
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayModelIdentifier(StateModelManager stateModelManager, String replayName, String replayVersion) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select modelIdentifier from AbstractStateModel where applicationName='" + replayName +"' and applicationVersion='" + replayVersion + "'");

		String replayModelIdentifier = ""; // {modelIdentifier: 16q1mx4113647030708}
		if(resultSet.hasNext()) {
			try {
				replayModelIdentifier = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplayModelIdentifier: AbstractStateModel found (%s, %s) but there is an error extracting the modelIdentifier", replayName, replayVersion);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayModelIdentifier: AbstractStateModel not found for name: %s, version: %s", replayName, replayVersion);
			throw new StateModelException(msg);
		}
		// {modelIdentifier: 16q1mx4113647030708} to 16q1mx4113647030708
		System.out.println(String.format("getReplayModelIdentifier... %s ", replayModelIdentifier));
		replayModelIdentifier = replayModelIdentifier.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplayModelIdentifier... Replaying State Model identifier %s ...", replayModelIdentifier));

		return replayModelIdentifier;
	}

	/**
	 * Extract the number of TestSequences that exists in the desired State Model to replay
	 * 
	 * @param stateModelManager
	 * @param replayModelIdentifier
	 * @param replayName
	 * @param replayVersion
	 * @return
	 * @throws StateModelException
	 */
	public static int getReplayTestSequenceNumber(StateModelManager stateModelManager, String replayModelIdentifier, String replayName, String replayVersion) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select count(*) from TestSequence where modelIdentifier='" + replayModelIdentifier + "'");

		int numberTestSequences = 0;
		if(resultSet.hasNext()) {
			try {
				numberTestSequences = extractNumber(resultSet.next().toString());
			} catch (Exception e) {
				String msg = String.format("getReplayTestSequenceNumber: ERROR parsing the number of TestSequences for AbstractStateModel (%s, %s)", replayName, replayVersion);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayTestSequenceNumber: TestSequences not found for AbstractStateModel with name: %s, version: %s", replayName, replayVersion);
			throw new StateModelException(msg);
		}
		if(numberTestSequences == 0) {
			String msg = String.format("getReplayTestSequenceNumber: 0 TestSequences found (this must not happen) for AbstractStateModel (%s, %s)", replayName, replayVersion);
			throw new StateModelException(msg);
		}

		return numberTestSequences;
	}

	/**
	 * Get the sequence identifier of the indicated TestSequence counter we want to replay
	 * 
	 * @param stateModelManager
	 * @param replayTestSequenceCounter
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplaySequenceIdentifierByCounter(StateModelManager stateModelManager, int replayTestSequenceCounter) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select sequenceId from TestSequence where counter='" + replayTestSequenceCounter + "'");

		String sequenceIdentifier = ""; // {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e}
		if(resultSet.hasNext()) {
			try {
				sequenceIdentifier = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplaySequenceIdentifierByCounter: SequenceIdentifier found for counter %s but there is an error parsing the id", replayTestSequenceCounter);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplaySequenceIdentifierByCounter: SequenceIdentifier not found for counter %s", replayTestSequenceCounter);
			throw new StateModelException(msg);
		}
		if(sequenceIdentifier.isEmpty()) {
			String msg = String.format("getReplaySequenceIdentifierByCounter: SequenceIdentifier is empty for counter %s", replayTestSequenceCounter);
			throw new StateModelException(msg);
		}
		// {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e} to 215bd479-3d67-4093-826b-44807ae7323e
		System.out.println(String.format("getReplaySequenceIdentifierByCounter... %s ", sequenceIdentifier));
		sequenceIdentifier = sequenceIdentifier.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplaySequenceIdentifierByCounter... Sequence identifier %s ...", sequenceIdentifier));

		return sequenceIdentifier;
	}

	/**
	 * Get the sequence identifier of the indicated TestSequence time stamp we want to replay
	 * 
	 * @param stateModelManager
	 * @param sequenceTime
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplaySequenceIdentifierByTime(StateModelManager stateModelManager, String sequenceTime) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select sequenceId from TestSequence where startDateTime.asString()='" + sequenceTime + "'");

		String sequenceIdentifier = ""; // {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e}
		if(resultSet.hasNext()) {
			try {
				sequenceIdentifier = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplaySequenceIdentifierByTime: SequenceIdentifier found for timestamp %s but there is an error parsing the id", sequenceTime);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplaySequenceIdentifierByTime: SequenceIdentifier not found for timestamp %s", sequenceTime);
			throw new StateModelException(msg);
		}
		if(sequenceIdentifier.isEmpty()) {
			String msg = String.format("getReplaySequenceIdentifierByTime: SequenceIdentifier is empty for timestamp %s", sequenceTime);
			throw new StateModelException(msg);
		}
		// {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e} to 215bd479-3d67-4093-826b-44807ae7323e
		System.out.println(String.format("getReplaySequenceIdentifierByTime... %s ", sequenceIdentifier));
		sequenceIdentifier = sequenceIdentifier.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplaySequenceIdentifierByTime... Sequence identifier %s ...", sequenceIdentifier));

		return sequenceIdentifier;
	}

	/**
	 * Based on a sequence identifier, extract the number of actions steps that exists to replay
	 * 
	 * @param stateModelManager
	 * @param sequenceIdentifier
	 * @return
	 * @throws StateModelException
	 */
	public static int getReplayActionStepsCount(StateModelManager stateModelManager, String sequenceIdentifier) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select count(*) from SequenceStep where stepId containstext '" + sequenceIdentifier + "'");

		int replayActionCount = 0;
		if(resultSet.hasNext()) {
			try {
				replayActionCount = extractNumber(resultSet.next().toString());
			} catch (Exception e) {
				String msg = String.format("getReplayActionStepsCount: ERROR parsing the number of SequenceSteps for SequenceIdentifier %s", sequenceIdentifier);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayActionStepsCount: SequenceStep not found for SequenceIdentifier %s", sequenceIdentifier);
			throw new StateModelException(msg);
		}
		if(replayActionCount == 0) {
			String msg = String.format("getReplayActionStepsCount: 0 SequenceStep found (someone did a 0 actions execution) for SequenceIdentifier %s", sequenceIdentifier);
			throw new StateModelException(msg);
		}

		return replayActionCount;
	}

	/**
	 * Based on an action step, get the counter of this action
	 * 
	 * @param stateModelManager
	 * @param actionSequence
	 * @return
	 * @throws StateModelException
	 */
	public static int getReplayCounterOfActionStep(StateModelManager stateModelManager, String actionSequence) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select counter from SequenceStep where stepId='" + actionSequence + "'");

		int counterStep = 0;
		if(resultSet.hasNext()) {
			try {
				counterStep = extractNumber(resultSet.next().toString());
			} catch (Exception e) {
				String msg = String.format("getReplayCounterOfActionStep: ERROR parsing the counter of SequenceStep stepId %s", actionSequence);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayCounterOfActionStep: counter not found for SequenceStep stepId %s", actionSequence);
			throw new StateModelException(msg);
		}
		if(counterStep == 0) {
			String msg = String.format("getReplayCounterOfActionStep: 0 counter SequenceStep found (counter must not be 0) for SequenceStep stepId %s", actionSequence);
			throw new StateModelException(msg);
		}

		return counterStep;
	}

	/**
	 * Get the AbstractActionId of the model based on a counter action step
	 * 
	 * @param stateModelManager
	 * @param counterStep
	 * @param replayModelIdentifier
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayAbstractActionIdofCounter(StateModelManager stateModelManager, int counterStep, String replayModelIdentifier) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select actionId from AbstractAction where counter='" + counterStep +"' and modelIdentifier='" + replayModelIdentifier + "'");

		String abstractActionReplayId = ""; // {actionId: AACje7hg01f4180421590}
		if(resultSet.hasNext()) {
			try {
				abstractActionReplayId = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplayAbstractActionIdofCounter: AbstractAction Id to replay found but error parsing the id");
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayAbstractActionIdofCounter: AbstractAction Id to replay NOT found");
			throw new StateModelException(msg);
		}
		if(abstractActionReplayId.isEmpty()) {
			String msg = String.format("getReplayAbstractActionIdofCounter: AbstractAction Id to replay is EMPTY");
			throw new StateModelException(msg);
		}
		// {actionId: AACje7hg01f4180421590} to AACje7hg01f4180421590
		System.out.println(String.format("getReplayAbstractActionIdofCounter... %s ", abstractActionReplayId));
		abstractActionReplayId = abstractActionReplayId.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplayAbstractActionIdofCounter... Replaying Action %s ...", abstractActionReplayId));

		return abstractActionReplayId;
	}

	private static int extractNumber(final String str) {

		if(str == null || str.isEmpty()) return 0;

		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for(char c : str.toCharArray()){
			if(Character.isDigit(c)){
				sb.append(c);
				found = true;
			} else if(found){
				// If we already found a digit before and this char is not a digit, stop looping
				break;                
			}
		}

		return Integer.parseInt(sb.toString());
	}

}
