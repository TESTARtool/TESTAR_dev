/***************************************************************************************************
 *
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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
                if(currentAction.get(Tags.Desc, "NoCurrentDescAvailable").equalsIgnoreCase(previousAction.get(Tags.Desc, "NoPreviousDescAvailable"))){
                    // match found -> the action is not a new action
                    matchingActionFound = true;
                }// type into actions have a different Desc because the input is different:
                // if both actions are type into actions:
                else if((currentAction.get(Tags.Role, null)!=null)&&
                        (previousAction.get(Tags.Role, null)!=null)&&
                        (currentAction.get(Tags.Role, null).toString().equalsIgnoreCase("ClickTypeInto"))&&
                        (previousAction.get(Tags.Role, null).toString().equalsIgnoreCase("ClickTypeInto"))){
                    String currentTargetDesc = currentAction.get(Tags.Desc, "currentDescNotAvailable");
                    currentTargetDesc = currentTargetDesc.substring(currentTargetDesc.indexOf("into"));
                    String previousTargetDesc = previousAction.get(Tags.Desc, "previousDescNotAvailable");
                    previousTargetDesc = previousTargetDesc.substring(previousTargetDesc.indexOf("into"));
                    if(currentTargetDesc.equalsIgnoreCase(previousTargetDesc)){
                        // match found -> the action is not a new action
                        matchingActionFound = true;
                    }
                }
                //TODO currently it considers different list items of the same list as different actions - tries out all
            }
            if(!matchingActionFound){
                //new action found, adding to new actions set:
                newActions.add(currentAction);
            }
        }
        return newActions;
    }
}
