/***************************************************************************************************
 *
 * Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
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
