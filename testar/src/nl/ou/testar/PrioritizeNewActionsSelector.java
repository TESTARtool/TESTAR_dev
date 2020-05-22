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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
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
 * If addExecutedAction() is used every time an action executed, then the least executed actions are prioritized.
 *
 */
public class PrioritizeNewActionsSelector {

    public PrioritizeNewActionsSelector() {
        // empty constructor, required because of previousActions that will be set during first use
    }

    private Set<Action> previousActions;
    private Set<Action> executedActions = new HashSet<Action> ();

    public Set<Action> getPrioritizedActions(Set<Action> actions) {
        Set<Action> prioritizedActions = new HashSet<Action>();
        //checking if it is the first round of actions:
        if(previousActions==null) {
            System.out.println("no previous actions -> all actions are new actions");
            prioritizedActions = actions;
        }else{
            System.out.println("not the first round, get the new actions compared to previous state");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, previousActions);
        }
        if((prioritizedActions.size()>0) && (executedActions.size()>0)) {
            System.out.println("there are new actions to choose from and there are executed actions, checking if they have been already executed");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(prioritizedActions, executedActions);
        }
        if(prioritizedActions.size()==0){
            System.out.println("no new and unexecuted actions, checking if any unexecuted actions");
            prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, executedActions);
        }
        if(prioritizedActions.size()==0){
            System.out.println("no unexecuted actions, returning all actions");
            prioritizedActions = actions;
            System.out.println("removing executed actions, size before="+executedActions.size());
            // and removing those actions from executed actions to reset the counter:
            Set<Action> tempExecutedActions = executedActions; // to prevent concurrent editing while iterating
            for(Action executedAction:executedActions){
                for(Action action:actions){
                    if(ActionSelectionUtils.areSimilarActions(executedAction,action)){
                        tempExecutedActions.remove(executedAction);
                    }
                }
            }
            executedActions = tempExecutedActions;
            System.out.println("removed executed actions, size after="+executedActions.size());
        }
        //saving the current actions for the next round:
        previousActions = actions;
        return(prioritizedActions);
    }

    public void addExecutedAction(Action action){
        executedActions.add(action);
    }
}


