/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.engine.action.selection.prioritize;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.action.selection.random.RandomActionSelector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides functions for prioritizing new actions for action selection.
 * Requires using a constructor since class variables are used for saving information.
 *
 * getPrioritizedActions() to pick new actions from a set of actions.
 *
 * The first time use will return the same full set of actions, but the following uses will return only
 * the actions that were not part of the previous set.
 *
 * If addSelectedAction() is used every time an action selected, then the least selected actions are prioritized.
 *
 */
public class PrioritizeNewActionsSelector extends RandomActionSelector {

    public PrioritizeNewActionsSelector() {
        // empty constructor, required because of previousActions that will be set during first use
    }

    private Set<Action> previousActions;
    private Map<Action, Integer> selectedActions = new HashMap<>();

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        Action selectedAction = new RandomActionSelector().selectAction(state, getPrioritizedActions(actions));
        addSelectedAction(selectedAction);
        System.out.println("Selected action: " + selectedAction.get(Tags.Desc, "NoCurrentDescAvailable")
                + " -- Times selected: " + timesSelected(selectedAction));
        return selectedAction;
    }

    private Set<Action> getPrioritizedActions(Set<Action> actions) {
        System.out.println("---------------------------------------------------------");
        Set<Action> prioritizedActions = new HashSet<Action>();
        //checking if it is the first round of actions:
        if(previousActions==null) {
            System.out.println("no previous actions -> all actions are new actions");
            prioritizedActions = actions;
        }else{
            System.out.println("not the first round, get the new actions compared to previous state");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, previousActions);
        }
        if((prioritizedActions.size()>0) && (selectedActions.size()>0)) {
        	
        	// Do not check reseted Actions, with counter == 0
        	Set<Action> selectedActionsToCheck = new HashSet<>();
        	for(Map.Entry<Action, Integer> entry : selectedActions.entrySet()){
        		if(entry.getValue() > 0) {
        			selectedActionsToCheck.add(entry.getKey());
        		}
        	}

            System.out.println("there are new actions to choose from and there are selected actions, checking if they have been already selected");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(prioritizedActions, selectedActionsToCheck);
        }
        if(prioritizedActions.size()==0){
            System.out.println("no new and unselected actions, checking if any unselected actions");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, selectedActions.keySet());
        }
        if(prioritizedActions.size()==0){
            System.out.println("no unselected actions, returning all actions");
            prioritizedActions = actions;

            System.out.println("reset selected actions, size = " + selectedActions.size());
            int numberOfCleanedActions = 0;
            // reset the action counter:
            for(Map.Entry<Action, Integer> entry : selectedActions.entrySet()){
            	for(Action action : actions){
            		if(ActionSelectionUtils.areSimilarActions(entry.getKey(), action)){
            			entry.setValue(0);
            			numberOfCleanedActions = numberOfCleanedActions + 1;
            		}
            	}
            }
            System.out.println("reseted actions = " + numberOfCleanedActions);

        }
        
        //saving the current actions for the next round:
        previousActions = actions;
        return prioritizedActions;
    }
    
    private void addSelectedAction(Action action){
    	for(Map.Entry<Action, Integer> entry : selectedActions.entrySet()){
    		if(ActionSelectionUtils.areSimilarActions(entry.getKey(), action)) {
    			entry.setValue(entry.getValue()+1);
    			return;
    		}
    	}
    	selectedActions.put(action, 1);
    }

    private int timesSelected(Action action) {
    	for(Map.Entry<Action, Integer> entry : selectedActions.entrySet()){
    		if(ActionSelectionUtils.areSimilarActions(entry.getKey(), action)) {
    			return entry.getValue();
    		}
    	}
        return 0;
    }
}
