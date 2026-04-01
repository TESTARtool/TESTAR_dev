/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.similarity;

import java.util.Set;

import org.testar.config.ActionTags;
import org.testar.core.execution.ActionSelectorService;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.action.selection.random.RandomActionSelector;

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
public class SimilarityDetection implements ActionSelectorService {

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

		Action selectedAction = reduceProbabilityBySimilarity(state, actions);
		increaseSpecificSelectedAction(selectedAction); // Increase for next iteration
		return selectedAction;
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
			return new RandomActionSelector().selectAction(state, actions);
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

	/**
	 * Additional method to increase the similarity value of a selected action.
	 * 
	 * @param executedAction
	 */
	private void increaseSpecificSelectedAction(Action selectedAction) {
		String executedID = selectedAction.get(Tags.OriginWidget).get(Tags.AbstractID, "NopeExecuted");

		System.out.println("====================== SELECTED ACTION ======================");

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
