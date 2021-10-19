/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar.SimpleGuiStateGraph;

import nl.ou.testar.RandomActionSelector;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GuiStateGraphWithVisitedActions {
    protected Set<IdBasedGuiState> idBasedGuiStates;
    protected String startingStateAbstractCustomId;
    protected String previousStateAbstractCustomId;
    protected String previousActionAbstractCustomId;

    public GuiStateGraphWithVisitedActions() {
        idBasedGuiStates = new HashSet<IdBasedGuiState>();
    }

    public Set<String> getAbstractCustomIdsOfUnvisitedActions(State state){
        return getIdBasedGuiState(state.get(Tags.AbstractIDCustom)).getUnvisitedActionIds();
    }

    //TODO move into a new action selector:
    public Action selectAction(State state, Set<Action> actions){
        // saving the starting node of the graph:
        if(startingStateAbstractCustomId==null){
            startingStateAbstractCustomId=state.get(Tags.AbstractIDCustom);
        }
        // adding state transition to the graph: previous state + previous action = current state
        if(previousStateAbstractCustomId!=null && previousActionAbstractCustomId != null){
            IdBasedGuiState previousState = getIdBasedGuiState(previousStateAbstractCustomId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                idBasedGuiStates.remove(previousState);
//                System.out.println(this.getClass()+": new state transition: previousStateId="+previousStateAbstractCustomId+", targetStateId="+state.get(Tags.AbstractIDCustom)+", previousActionAbstractCustomId="+previousActionAbstractCustomId);
                previousState.addStateTransition(new GuiStateTransition(previousStateAbstractCustomId,state.get(Tags.AbstractIDCustom),previousActionAbstractCustomId));
                idBasedGuiStates.add(previousState);
            }
        }
        Action returnAction = null;
        IdBasedGuiState currentIdBasedGuiState = getIdBasedGuiState(state.get(Tags.AbstractIDCustom));

        if(currentIdBasedGuiState==null) { // did not contain the state ID -> a new state
            // new state:
//            System.out.println(this.getClass()+": selectAction(): new state");
            currentIdBasedGuiState = createIdBasedGuiState(state, actions);
        }

        //checking if there are state based special instructions (for example for file system window)
        //TODO implement a way to give application specific rules like: if title=="Select one or multiple files", then always select cancel and mark all actions visited

        // the action selection:
        if(currentIdBasedGuiState.getUnvisitedActionIds().size()==actions.size()){
            // all actions unvisited -> new state -> randomly select action:
            System.out.println(this.getClass()+": new state, selecting randomly from "+actions.size()+" available actions");
            returnAction = RandomActionSelector.selectAction(actions);
        }else{
            // already visited state
            if(currentIdBasedGuiState.getUnvisitedActionIds().size()==0){
                System.out.println(this.getClass()+":  all actions visited! Finding the action leading to a state with most unvisited actions");
                String actionId = getAbstractCustomIdOfActionLeadingToStateWithMostUnvisitedActions(currentIdBasedGuiState.getAbstractCustomStateId());
                returnAction =getActionWithAbstractCustomId(actions, actionId);
            }else{
                System.out.println(this.getClass()+": selectAction(): existing state, ID="+currentIdBasedGuiState.getAbstractCustomStateId()+", available action count="+actions.size()+",unvisited action count="+currentIdBasedGuiState.getUnvisitedActionIds().size());
                long graphTime = System.currentTimeMillis();
                Random rnd = new Random(graphTime);
                ArrayList<String> unvisitedActions = new ArrayList<String>(currentIdBasedGuiState.getUnvisitedActionIds());
                String abstractCustomIdOfRandomUnvisitedAction = unvisitedActions.get(rnd.nextInt(unvisitedActions.size()));
//                System.out.println(this.getClass()+": unvisitedAction.size="+unvisitedActions.size()+", random Id="+abstractCustomIdOfRandomUnvisitedAction);
                returnAction = getActionWithAbstractCustomId(actions, abstractCustomIdOfRandomUnvisitedAction);
            }
        }
        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println(this.getClass()+": selectAction(): no unvisited actions found! Getting purely random action.");
            returnAction = RandomActionSelector.selectAction(actions);
        }
        //updating the list of states:
        idBasedGuiStates.remove(currentIdBasedGuiState); // should not be a problem if state not there (new state)?
        currentIdBasedGuiState.addVisitedAction(returnAction.get(Tags.AbstractIDCustom));
        idBasedGuiStates.add(currentIdBasedGuiState);
        // saving the state and action for state transition after knowing the target state:
        previousActionAbstractCustomId = returnAction.get(Tags.AbstractIDCustom);
        previousStateAbstractCustomId = state.get(Tags.AbstractIDCustom);
        return returnAction;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param abstractCustomActionId
     * @return
     */
    protected Action getActionWithAbstractCustomId(Set<Action> actions, String abstractCustomActionId){
        for(Action action:actions){
            // find the action with abstractCustomId:
            if(action.get(Tags.AbstractIDCustom).equals(abstractCustomActionId)){
                return action;
            }
        }
        return null;
    }

    protected String getAbstractCustomIdOfActionLeadingToStateWithMostUnvisitedActions(String currentStateId){
        IdBasedGuiState currentState = getIdBasedGuiState(currentStateId);
        if(currentState==null || currentState.getUnvisitedActionIds()==null){
            System.out.println(this.getClass()+": ERROR, current state or transitions is null!");
        }
        int numberOfMostUnvisitedActions = 0;
        String returnActionId = null;
        for(GuiStateTransition transition:currentState.getStateTransitions()){
            if(transition==null || transition.getSourceStateAbstractCustomId()==null){
                System.out.println(this.getClass()+": ERROR, transition or source state id is null!");
            }
            if(transition.getSourceStateAbstractCustomId().equals(currentStateId)){
                //source state is the same as current id, as it should be if no errors
                if(transition.getTargetStateAbstractCustomId()==null){
                    System.out.println(this.getClass()+": ERROR, target state ID is null!");
                }
                if(currentState.equals(transition.getTargetStateAbstractCustomId())){
                    // source state id == target state id -> no actual state transition with this action
                    System.out.println(this.getClass()+": not actually a state transition.");
                }else{
                    IdBasedGuiState targetState = getIdBasedGuiState(transition.getTargetStateAbstractCustomId());
                    if(targetState==null){
                        System.out.println(this.getClass()+": ERROR, target state is null!");
                    }
                    if(targetState.getUnvisitedActionIds().size()>numberOfMostUnvisitedActions){
                        numberOfMostUnvisitedActions = targetState.getUnvisitedActionIds().size();
                        System.out.println(this.getClass()+": unvisited actions = "+numberOfMostUnvisitedActions);
                        returnActionId = transition.getActionAbstractCustomId();
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
     * @param abstractCustomStateId
     * @return
     */
    protected IdBasedGuiState getIdBasedGuiState(String abstractCustomStateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getAbstractCustomStateId().equals(abstractCustomStateId)){
                return state;
            }
        }
        return null;
    }

    protected IdBasedGuiState createIdBasedGuiState(State state, Set<Action> actions){
        Set<String> actionIds = new HashSet<>();
        for(Action action:actions){
            actionIds.add(action.get(Tags.AbstractIDCustom));
        }
        return new IdBasedGuiState(state.get(Tags.AbstractIDCustom),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getAbstractCustomStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

}
