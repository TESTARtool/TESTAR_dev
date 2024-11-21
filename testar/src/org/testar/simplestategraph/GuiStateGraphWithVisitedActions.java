/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.simplestategraph;

import org.testar.IActionSelector;
import org.testar.RandomActionSelector;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GuiStateGraphWithVisitedActions implements IActionSelector {
    protected Set<IdBasedGuiState> idBasedGuiStates;
    protected String startingStateAbstractId;
    protected String previousStateAbstractId;
    protected String previousActionAbstractId;

    public GuiStateGraphWithVisitedActions() {
        idBasedGuiStates = new HashSet<IdBasedGuiState>();
    }

    public Set<String> getAbstractIdsOfUnvisitedActions(State state){
        return getIdBasedGuiState(state.get(Tags.AbstractID)).getUnvisitedActionIds();
    }

    //TODO move into a new action selector:
    @Override
    public Action selectAction(State state, Set<Action> actions){
        System.out.println("---------------------------------------------------------");
        // saving the starting node of the graph:
        if(startingStateAbstractId==null){
            startingStateAbstractId=state.get(Tags.AbstractID);
        }
        // adding state transition to the graph: previous state + previous action = current state
        if(previousStateAbstractId!=null && previousActionAbstractId != null){
            IdBasedGuiState previousState = getIdBasedGuiState(previousStateAbstractId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                idBasedGuiStates.remove(previousState);
                previousState.addStateTransition(new GuiStateTransition(previousStateAbstractId,state.get(Tags.AbstractID),previousActionAbstractId));
                idBasedGuiStates.add(previousState);
            }
        }
        Action returnAction = null;
        IdBasedGuiState currentIdBasedGuiState = getIdBasedGuiState(state.get(Tags.AbstractID));

        if(currentIdBasedGuiState==null) { // did not contain the state ID -> a new state
            // new state:
            currentIdBasedGuiState = createIdBasedGuiState(state, actions);
        }

        //checking if there are state based special instructions (for example for file system window)
        //TODO implement a way to give application specific rules like: if title=="Select one or multiple files", then always select cancel and mark all actions visited

        // the action selection:
        if(currentIdBasedGuiState.getUnvisitedActionIds().size()==actions.size()){
            // all actions unvisited -> new state -> randomly select action:
            System.out.println(this.getClass()+": new state, selecting randomly from "+actions.size()+" available actions");
            returnAction = RandomActionSelector.selectRandomAction(actions);
        }else{
            // already visited state
            if(currentIdBasedGuiState.getUnvisitedActionIds().size()==0){
                System.out.println(this.getClass()+":  all actions visited! Finding the action leading to a state with most unvisited actions");
                String actionId = getAbstractIdOfActionLeadingToStateWithMostUnvisitedActions(currentIdBasedGuiState.getAbstractStateId());
                returnAction =getActionWithAbstractId(actions, actionId);
            }else{
                System.out.println(this.getClass()+": selectAction(): existing state, ID="+currentIdBasedGuiState.getAbstractStateId()+", available action count="+actions.size()+",unvisited action count="+currentIdBasedGuiState.getUnvisitedActionIds().size());
                long graphTime = System.currentTimeMillis();
                Random rnd = new Random(graphTime);
                ArrayList<String> unvisitedActions = new ArrayList<String>(currentIdBasedGuiState.getUnvisitedActionIds());
                String abstractIdOfRandomUnvisitedAction = unvisitedActions.get(rnd.nextInt(unvisitedActions.size()));
                returnAction = getActionWithAbstractId(actions, abstractIdOfRandomUnvisitedAction);
            }
        }
        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println(this.getClass()+": selectAction(): no unvisited actions found! Getting purely random action.");
            returnAction = RandomActionSelector.selectRandomAction(actions);
        }
        //updating the list of states:
        idBasedGuiStates.remove(currentIdBasedGuiState); // should not be a problem if state not there (new state)?
        currentIdBasedGuiState.addVisitedAction(returnAction.get(Tags.AbstractID));
        idBasedGuiStates.add(currentIdBasedGuiState);
        // saving the state and action for state transition after knowing the target state:
        previousActionAbstractId = returnAction.get(Tags.AbstractID);
        previousStateAbstractId = state.get(Tags.AbstractID);
        System.out.println(this.getClass() + ": return selected action = " + returnAction.get(Tags.Desc, "NoDescAvailable"));
        return returnAction;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param abstractActionId
     * @return
     */
    protected Action getActionWithAbstractId(Set<Action> actions, String abstractActionId){
        for(Action action:actions){
            // find the action with abstractId:
            if(action.get(Tags.AbstractID).equals(abstractActionId)){
                return action;
            }
        }
        return null;
    }

    protected String getAbstractIdOfActionLeadingToStateWithMostUnvisitedActions(String currentStateId){
        IdBasedGuiState currentState = getIdBasedGuiState(currentStateId);
        if(currentState==null || currentState.getUnvisitedActionIds()==null){
            System.out.println(this.getClass()+": ERROR, current state or transitions is null!");
        }
        int numberOfMostUnvisitedActions = 0;
        String returnActionId = null;
        for(GuiStateTransition transition:currentState.getStateTransitions()){
            if(transition==null || transition.getSourceStateAbstractId()==null){
                System.out.println(this.getClass()+": ERROR, transition or source state id is null!");
            }
            if(transition.getSourceStateAbstractId().equals(currentStateId)){
                //source state is the same as current id, as it should be if no errors
                if(transition.getTargetStateAbstractId()==null){
                    System.out.println(this.getClass()+": ERROR, target state ID is null!");
                }
                if(currentState.getAbstractStateId().equals(transition.getTargetStateAbstractId())){
                    // source state id == target state id -> no actual state transition with this action
                    System.out.println(this.getClass()+": not actually a state transition.");
                }else{
                    IdBasedGuiState targetState = getIdBasedGuiState(transition.getTargetStateAbstractId());
                    if(targetState==null){
                        System.out.println(this.getClass()+": ERROR, target state is null!");
                    }
                    if(targetState.getUnvisitedActionIds().size()>numberOfMostUnvisitedActions){
                        numberOfMostUnvisitedActions = targetState.getUnvisitedActionIds().size();
                        System.out.println(this.getClass()+": unvisited actions = "+numberOfMostUnvisitedActions);
                        returnActionId = transition.getActionAbstractId();
                    }
                }
            }else{
                System.out.println(this.getClass()+": ERROR in state transitions, source state id NOT same as current state id!");
            }
        }
        if(returnActionId==null){
            System.out.println(this.getClass()+": all actions in all target states have been visited! TODO:implement more depth");
            //TODO implement recursive search for state with unvisited actions
            return "not found, depth=1";
        }
        return returnActionId;
    }

    /**
     * Returns null if not found
     *
     * @param abstractStateId
     * @return
     */
    protected IdBasedGuiState getIdBasedGuiState(String abstractStateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getAbstractStateId().equals(abstractStateId)){
                return state;
            }
        }
        return null;
    }

    protected IdBasedGuiState createIdBasedGuiState(State state, Set<Action> actions){
        Set<String> actionIds = new HashSet<>();
        for(Action action:actions){
            actionIds.add(action.get(Tags.AbstractID));
        }
        return new IdBasedGuiState(state.get(Tags.AbstractID),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getAbstractStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

}
