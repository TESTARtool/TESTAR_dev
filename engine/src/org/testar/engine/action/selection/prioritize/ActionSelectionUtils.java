/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.engine.action.selection.prioritize;

import org.testar.core.action.Action;
import org.testar.core.tag.Tags;
import java.util.HashSet;
import java.util.Set;

public class ActionSelectionUtils {

    public static Set<Action> getSetOfNewActions(Set<Action> currentActions, Set<Action> previousActions){
        Set<Action> newActions = new HashSet<Action>();
        //going through the current actions:
        for(Action currentAction:currentActions){
            //comparing each current action to each previous action:
            boolean matchingActionFound = false;
            for(Action previousAction:previousActions){
                if(areSimilarActions(previousAction, currentAction)) {
                	// Match found, we don't need to continue
                	matchingActionFound = true;
                	break;
                }
            }
            if(!matchingActionFound){
                //new action found, adding to new actions set:
                newActions.add(currentAction);
            }
        }
        return newActions;
    }

    public static boolean areSimilarActions(Action action_1, Action action_2){
        if(action_1.get(Tags.Desc, "NoCurrentDescAvailable").equalsIgnoreCase(action_2.get(Tags.Desc, "NoPreviousDescAvailable"))){
            // both have the same (non-empty) description -> match found
            return true;
        }
        // type into actions have a different Desc because the input is different:
        // if both actions are type into actions:
        else if((action_1.get(Tags.Role, null)!=null)&&
                (action_2.get(Tags.Role, null)!=null)&&
                (action_1.get(Tags.Role, null).toString().equalsIgnoreCase("ClickTypeInto"))&&
                (action_2.get(Tags.Role, null).toString().equalsIgnoreCase("ClickTypeInto"))){
            String currentTargetDesc = action_1.get(Tags.Desc, "currentDescNotAvailable");
            currentTargetDesc = currentTargetDesc.substring(currentTargetDesc.indexOf("into"));
            String previousTargetDesc = action_2.get(Tags.Desc, "previousDescNotAvailable");
            previousTargetDesc = previousTargetDesc.substring(previousTargetDesc.indexOf("into"));
            if(currentTargetDesc.equalsIgnoreCase(previousTargetDesc)){
                // both are typing actions into same target widgets -> match found
                return true;
            }
        }
        //TODO currently it considers different list items of the same list as different actions - tries out all
        //TODO also scroll bar actions get into looping
        return false;
    }

}
