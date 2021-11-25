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

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.orientechnologies.orient.core.sql.executor.OResultSet;

import nl.ou.testar.StateModel.Exception.StateModelException;
import org.testar.StateManagementTags;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.StateModelManager;

public class ReplayStateModelUtil {

	private ReplayStateModelUtil() {}

	/**
	 * Query the OrientDB database to get the model identifier using the name and version of the model we want to replay. 
	 * 
	 * @param stateModelManager
	 * @param replayName
	 * @param replayVersion
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayModelIdentifier(StateModelManager stateModelManager, String replayName, String replayVersion) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select modelIdentifier from AbstractStateModel where applicationName='" + replayName +"' and applicationVersion='" + replayVersion + "'");

		String replayModelIdentifier = ""; // result set String is {modelIdentifier: 16q1mx4113647030708}
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
	 * Query the desired StateModel to extract the used abstractionAttributes. 
	 * 
	 * @param stateModelManager
	 * @param modelIdentifier
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayModelAbstractAttributes(StateModelManager stateModelManager, String modelIdentifier) throws StateModelException {
	    OResultSet resultSet = stateModelManager.queryStateModel("select abstractionAttributes from AbstractStateModel where modelIdentifier='" + modelIdentifier +"'");

	    String abstractAttributes = "";
	    if(resultSet.hasNext()) {
	        try {
	            abstractAttributes = resultSet.next().toString().replace("\n", "").trim();
	        } catch (Exception e) {
	            String msg = String.format("getReplayModelIdentifier: abstractAttributes found but there is an error extracting the information");
	            e.printStackTrace();
	            throw new StateModelException(msg);
	        }
	    } else {
	        String msg = String.format("getReplayModelIdentifier: abstractAttributes not found");
	        throw new StateModelException(msg);
	    }
	    // {abstractionAttributes: [Widget title, Path to the widget, Widget control type]}
	    abstractAttributes = abstractAttributes.replace("{", "").replace("}", "").trim().split(":")[1].trim();
	    return abstractAttributes;
	}

	/**
	 * Extract the number of TestSequences that exists in the desired State Model to replay. 
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
	 * Get the counter of the initial TestSequence of the desired State Model to replay. 
	 * 
	 * @param stateModelManager
	 * @param replayModelIdentifier
	 * @param replayName
	 * @param replayVersion
	 * @return
	 * @throws StateModelException
	 */
	public static int getReplayInitialTestSequence(StateModelManager stateModelManager, String replayModelIdentifier, String replayName, String replayVersion) throws StateModelException {
	    OResultSet resultSet = stateModelManager.queryStateModel("select counter from TestSequence where modelIdentifier='" + replayModelIdentifier + "'");

	    int initialTestSequences = 0;
	    if(resultSet.hasNext()) {
	        try {
	            initialTestSequences = extractNumber(resultSet.next().toString());
	        } catch (Exception e) {
	            String msg = String.format("getReplayInitialTestSequence: ERROR parsing the initial TestSequences counter for AbstractStateModel (%s, %s)", replayName, replayVersion);
	            e.printStackTrace();
	            throw new StateModelException(msg);
	        }
	    } else {
	        String msg = String.format("getReplayInitialTestSequence: Initial TestSequence counter not found for AbstractStateModel with name: %s, version: %s", replayName, replayVersion);
	        throw new StateModelException(msg);
	    }
	    if(initialTestSequences == 0) {
	        String msg = String.format("getReplayInitialTestSequence: 0 initial TestSequences counter (this must not happen) for AbstractStateModel (%s, %s)", replayName, replayVersion);
	        throw new StateModelException(msg);
	    }

	    return initialTestSequences;
	}

	/**
	 * Get the sequence identifier of the indicated TestSequence counter we want to replay. 
	 * 
	 * @param stateModelManager
	 * @param replayTestSequenceCounter
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplaySequenceIdentifierByCounter(StateModelManager stateModelManager, int replayTestSequenceCounter) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select sequenceId from TestSequence where counter='" + replayTestSequenceCounter + "'");

		String sequenceIdentifier = ""; // result set String is {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e}
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
	 * Get the sequence identifier of the indicated TestSequence time stamp we want to replay. 
	 * 
	 * @param stateModelManager
	 * @param sequenceTime
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplaySequenceIdentifierByTime(StateModelManager stateModelManager, String sequenceTime) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select sequenceId from TestSequence where startDateTime.asString()='" + sequenceTime + "'");

		String sequenceIdentifier = ""; // result set String is {sequenceId: 215bd479-3d67-4093-826b-44807ae7323e}
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
	 * Based on a sequence identifier, extract the number of actions steps that exists to replay. 
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
	 * Get the concreteActionId of the specific actionSequence step. 
	 * From testSequenceStep action to ConcreteActionId. 
	 * 
	 * @param stateModelManager
	 * @param actionSequence
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayConcreteActionStep(StateModelManager stateModelManager, String actionSequence) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select concreteActionId from SequenceStep where stepId='" + actionSequence + "'");

		String concreteActionId = ""; // result set String is {concreteActionId: AACje7hg01f4180421590}
		if(resultSet.hasNext()) {
			try {
				concreteActionId = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplayConcreteActionStep: ERROR parsing the concreteActionId of SequenceStep stepId %s", actionSequence);
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayConcreteActionStep: concreteActionId not found for SequenceStep stepId %s", actionSequence);
			throw new StateModelException(msg);
		}
		if(concreteActionId.isEmpty()) {
			String msg = String.format("getReplayConcreteActionStep: concreteActionId to replay found but is EMPTY for SequenceStep stepId %s", actionSequence);
			throw new StateModelException(msg);
		}

		// {concreteActionId: AACje7hg01f4180421590} to AACje7hg01f4180421590
		System.out.println(String.format("getReplayConcreteActionStep... %s ", concreteActionId));
		concreteActionId = concreteActionId.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplayConcreteActionStep... Replaying Action %s ...", concreteActionId));

		return concreteActionId;
	}

	/**
	 * Get the abstractActionId of one AbstractStateModel by checking if it is related with a concrete action. 
	 * From ConcreteActionId to AbstractActionId. 
	 * 
	 * @param stateModelManager
	 * @param replayModelIdentifier
	 * @param concreteActionId
	 * @return
	 * @throws StateModelException
	 */
	public static String getReplayAbstractActionIdFromConcreteAction(StateModelManager stateModelManager, String replayModelIdentifier, String concreteActionId) throws StateModelException {
		OResultSet resultSet = stateModelManager.queryStateModel("select actionId from AbstractAction where modelIdentifier='" + replayModelIdentifier + "' and concreteActionIds.asString() containstext '" + concreteActionId + "'");

		String abstractActionReplayId = ""; // result set String is {actionId: AACje7hg01f4180421590}
		if(resultSet.hasNext()) {
			try {
				abstractActionReplayId = resultSet.next().toString().replace("\n", "").trim();
			} catch (Exception e) {
				String msg = String.format("getReplayAbstractActionIdFromConcreteAction: AbstractAction Id to replay found but error parsing the id");
				e.printStackTrace();
				throw new StateModelException(msg);
			}
		} else {
			String msg = String.format("getReplayAbstractActionIdFromConcreteAction: AbstractAction Id to replay NOT found");
			throw new StateModelException(msg);
		}
		if(abstractActionReplayId.isEmpty()) {
			String msg = String.format("getReplayAbstractActionIdFromConcreteAction: AbstractAction Id to replay is EMPTY");
			throw new StateModelException(msg);
		}
		// {actionId: AACje7hg01f4180421590} to AACje7hg01f4180421590
		System.out.println(String.format("getReplayAbstractActionIdFromConcreteAction... %s ", abstractActionReplayId));
		abstractActionReplayId = abstractActionReplayId.replace("{", "").replace("}", "").trim().split(":")[1].trim();
		System.out.println(String.format("getReplayAbstractActionIdFromConcreteAction... Replaying Action %s ...", abstractActionReplayId));

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

	/**
	 * Compare two abstract attributes strings to check if the abstraction is the same. 
	 * 
	 * @param replayAbsAtt
	 * @param currentAbsAtt
	 * @return
	 */
	public static boolean sameAbstractionAttributes(String replayAbsAtt, String currentAbsAtt) {
	    String[] replayArr = replayAbsAtt.replace("[", "").replace("]", "").replaceAll("\\s","").split(",");
	    String[] currentArr = currentAbsAtt.replace("[", "").replace("]", "").replaceAll("\\s","").split(",");
	    Arrays.sort(replayArr);
	    Arrays.sort(currentArr);
	    return Arrays.equals(replayArr, currentArr);
	}

	/**
	 * Helper class to transform State Model String attributes
	 * into a StateManagementTag Setting Strings
	 * 
	 * From: [Widget title, Path to the widget, Widget control type]
	 * To: [WidgetTitle,WidgetPath,WidgetControlType]
	 * 
	 * @return
	 */
	private static String transformDescriptionToAbtractTag(String abstractionAttributes) {
	    // Input: "[Widget title, Path to the widget, Widget control type]"
	    // TO: "Widget title, Path to the widget, Widget control type"
	    abstractionAttributes = abstractionAttributes.replace("[", "").replace("]", "");

	    // Array: ["Widget title"][" Path to the widget"][" Widget control type"]
	    // ToArray: ["WidgetTitle"]["WidgetPath"]["WidgetControlType"]
	    String description[] = abstractionAttributes.split(",");
	    String modelTags[] = new String[description.length];
	    for(int i = 0; i < description.length; i++) {
	        String stripDescription = StringUtils.stripStart(description[i], null);
	        for(Tag<?> t : StateManagementTags.getAllTags()) {
	            if(t.name().equals(stripDescription)) {
	                modelTags[i] = StateManagementTags.getSettingsStringFromTag(t);
	            }
	        }
	    }

	    // Return [WidgetTitle,WidgetPath,WidgetControlType]
	    return Arrays.toString(modelTags);
	}

}
