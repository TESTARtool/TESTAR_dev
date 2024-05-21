/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2024 Open Universiteit - www.ou.nl
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

package org.testar.action.priorization;

import java.util.Set;

import org.testar.IActionExecutor;
import org.testar.IActionSelector;
import org.testar.RandomActionSelector;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

/**
 * Sample class that tries to detect similar Actions between previous and current State,
 * to decrease the possibilities to select a static widget and previous executed Action.
 * 
 * Actions have an OriginWidget associated, and Widgets have an AbtractID property
 * that allows TESTAR to identify web elements based on Abstract Properties.
 * Example: WebWidgetId (test.settings -> AbstractStateAttributes)
 * 
 * If some Action.OriginWidget still existing between previous and current State
 * or if some Action.OriginWidget was executed previously,
 * increase a numeric similarity weight that will reduce the % to be selected
 */
public class SimilarityDetection implements IActionSelector, IActionExecutor {

	private Set<Action> similarityActions;
	private int maxValue;

	/**
	 * Utility Class to check Action similarity between two concurrent States.
	 * Set desired maxValue, this is being increased 1 by 1.
	 * 
	 * @param initialActions
	 * @param maxValue
	 */
	public SimilarityDetection(Set<Action> initialActions, int maxValue) {
		this.similarityActions = initialActions;
		this.maxValue = maxValue;
		for(Action a : this.similarityActions) {
			// Initially to 0 because will be increased to 1
			a.set(ActionTags.SimilarityValue, 0);
		}
	}


	@Override
	public Action selectAction(State state, Set<Action> actions) {
		// Given the current set of Actions of the State take the OriginWidget AbstractID,
		// and compare with the previous existing Actions/OriginWidget to increase the similarity value.
		// Minimal similarity value 1, Maximal similarity is given in the constructor. 
		// Higher similarity value means that Action/OriginWidget remains more time static in the State.
		actions = modifySimilarActions(actions);

		System.out.println("--------------------- DEBUG SIMILARITY VALUES ---------------------");
		for(Action a : actions) {
			System.out.println("Widget : " + a.get(Tags.OriginWidget).get(Tags.Desc, "") +
					" with similarity : " + a.get(ActionTags.SimilarityValue, 0));
		}

		return reduceProbabilityBySimilarity(state, actions);
	}

	/**
	 * Given the current set of Actions of the State take the OriginWidget AbstractID,
	 * and compare with the previous existing Actions/OriginWidget to increase the similarity value.
	 * Minimal similarity value 1, Maximal similarity is given in the constructor.
	 * 
	 * Higher similarity value means that Action/OriginWidget remains more time static in the State.
	 * 
	 * @param previousActions
	 * @param newActions
	 * @return actions with similarity value modified
	 */
	private Set<Action> modifySimilarActions(Set<Action> newActions){
		for(Action newAction : newActions) {
			for (Action oldAction : similarityActions) {

				// If we detect a coincidence determine and increase new similarity value
				if(originWidgetAbstractIdCoincidence(newAction, oldAction)) {
					newAction.set(ActionTags.SimilarityValue, increaseSimilarityValue(oldAction));
					// Stop comparing this newAction and go to the next one
					break;
				}
			}

			// We didn't find any coincidence this means, set default similarity value
			if(newAction.get(ActionTags.SimilarityValue, null) == null) {
				// Minimal similarity value is 1
				newAction.set(ActionTags.SimilarityValue, 1);
			}
		}

		// Update the saved actions with the new Actions
		similarityActions = newActions;

		return newActions;
	}

	/**
	 * Given two Actions, compare the AbstractID of the OriginWidget,
	 * and determine if the OriginWidget is the same from the Abstract point of view.
	 * 
	 * @param newAction
	 * @param oldAction
	 * @return true or false
	 */
	private boolean originWidgetAbstractIdCoincidence(Action newAction, Action oldAction) {
		// Compare OriginWidget AbstractID to check coincidences
		String newAbstractID = newAction.get(Tags.OriginWidget).get(Tags.AbstractID, "NopeNew");
		String oldAbstractID = oldAction.get(Tags.OriginWidget).get(Tags.AbstractID, "NopeOld");
		return newAbstractID.equals(oldAbstractID);
	}

	/**
	 * Increase and return the similarity value of one action by 1.
	 * Maximum similarity value is given in the constructor.
	 * 
	 * @param oldAction
	 * @return (number) similarity value
	 */
	private int increaseSimilarityValue(Action oldAction) {
		// Update Similarity Value Tag
		int similarityValue = oldAction.get(ActionTags.SimilarityValue);
		similarityValue = similarityValue + 1;
		// Maximal similarity value
		if(similarityValue > maxValue) {similarityValue = maxValue;}
		return similarityValue;
	}

	private Action reduceProbabilityBySimilarity(State state, Set<Action> actions) {
		// Check the totalWeight
		double totalWeight = 0;
		for(Action a : actions) {
			totalWeight = totalWeight + (1.0 / a.get(ActionTags.SimilarityValue));
		}

		// Just in case, if something wrong return purely random
		if(totalWeight == 0) {
			return RandomActionSelector.selectRandomAction(actions);
		}

		System.out.println("******************* DEBUG SIMILARITY PROBABILITY ******************");

		// Create a percentage weight of each action based on the totalWeight
		WeightedAction actionProbability = new WeightedAction();
		for(Action a : actions) {
			double actionWeight = (1.0 / a.get(ActionTags.SimilarityValue)) / totalWeight * 100;
			actionProbability.addEntry(a, actionWeight);

			System.out.println("Widget : " + a.get(Tags.OriginWidget).get(Tags.Desc, "") + " with similarity : " + actionWeight);
		}

		return actionProbability.getRandom();
	}


	@Override
	public void executeAction(Action action) {
		increaseSpecificExecutedAction(action);
	}

	/**
	 * Additional method to increase the similarity value of an executed action.
	 * 
	 * @param executedAction
	 */
	private void increaseSpecificExecutedAction(Action executedAction) {
		String executedID = executedAction.get(Tags.OriginWidget).get(Tags.AbstractID, "NopeExecuted");

		System.out.println("====================== DEBUG EXECUTED ACTION ======================");

		for(Action savedAction : similarityActions) {
			String savedID = savedAction.get(Tags.OriginWidget).get(Tags.AbstractID, "NopeSaved");

			if(savedID.equals(executedID)) {
				savedAction.set(ActionTags.SimilarityValue, increaseSimilarityValue(savedAction));
				System.out.println("Increase executed Widget : " + savedAction.get(Tags.OriginWidget).get(Tags.Desc, "") + 
						" to : " + savedAction.get(ActionTags.SimilarityValue, -1));
				return;
			}
		}
	}
}
