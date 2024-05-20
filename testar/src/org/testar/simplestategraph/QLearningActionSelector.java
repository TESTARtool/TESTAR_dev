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
import java.util.Random;
import java.util.Set;

public class QLearningActionSelector implements IActionSelector {
    private double R_MAX;
    private double gammaDiscount;
    private GuiStateGraphForQlearning graph;

    public QLearningActionSelector(double R_MAX, double gammaDiscount){
        System.out.println("DEBUG: creating Q-learning action selector, R-MAX="+R_MAX+", gammaDiscount="+gammaDiscount);
        this.R_MAX = R_MAX;
        this.gammaDiscount=gammaDiscount;
        graph = new GuiStateGraphForQlearning(R_MAX,gammaDiscount);
    }

    public void resetGraphForNewTestSequence(){
        graph.startANewTestSequence();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        System.out.println("---------------------------------------------------------");
        // saving the starting node of the graph:
        if(graph.startingStateAbstractId==null){
            graph.startingStateAbstractId=state.get(Tags.AbstractID);
        }

        // Finding the current state from the previous states:
        QlearningGuiState currentQlearningGuiState = graph.getStateByAbstractId(state.get(Tags.AbstractID));

        // If it's a new state:
        if(currentQlearningGuiState==null) { // did not contain the state ID -> a new state
            // new state:
            currentQlearningGuiState = graph.createQlearningGuiState(state, actions);
        }else{
            //update the actions of the state - for some reason the action IDs are changing:
            currentQlearningGuiState.updateActionIdsOfTheStateIntoModel(actions, R_MAX);
        }

        // adding state transition to the graph: previous state + previous action = current state
        if(graph.previousStateAbstractId!=null && graph.previousActionAbstractId != null){ //else the first action and there is no transition yet
            QlearningGuiState previousState = graph.getStateByAbstractId(graph.previousStateAbstractId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                graph.qlearningGuiStates.remove(previousState);//removing the old version of the state
                previousState.addStateTransition(new GuiStateTransition(graph.previousStateAbstractId,state.get(Tags.AbstractID),graph.previousActionAbstractId),gammaDiscount,currentQlearningGuiState.getMaxQValueOfTheState(actions));
                graph.qlearningGuiStates.add(previousState);//adding the updated version of the state
            }
        }
        
        Action returnAction = null;
        ArrayList<String> actionIdsWithMaxQvalue = currentQlearningGuiState.getActionsIdsWithMaxQvalue(actions);
        if(actionIdsWithMaxQvalue.size()==0){
            System.out.println("ERROR: Qlearning did not find actions with max Q value!");
            returnAction = RandomActionSelector.selectRandomAction(actions);
        }else{
            //selecting randomly of the actionIDs that have max Q value:
            Random rnd = new Random();
            String abstractIdOfRandomAction = actionIdsWithMaxQvalue.get(rnd.nextInt(actionIdsWithMaxQvalue.size()));
            System.out.println("DEBUG: randomly chosen id="+abstractIdOfRandomAction);
            System.out.println("DEBUG: stateID from state="+state.get(Tags.AbstractID));
            returnAction = graph.getActionWithAbstractId(actions, abstractIdOfRandomAction);
        }

        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println("ERROR: QlearningActionSelector.selectAction(): no action found! Getting purely random action.");
            returnAction = RandomActionSelector.selectRandomAction(actions);
        }
        //updating the list of states:
        graph.qlearningGuiStates.remove(currentQlearningGuiState); // should not be a problem if state not there (new state)?
        graph.qlearningGuiStates.add(currentQlearningGuiState);
        // saving the state and action for state transition after knowing the target state:
        graph.previousActionAbstractId = returnAction.get(Tags.AbstractID);
        graph.previousStateAbstractId = state.get(Tags.AbstractID);
        System.out.println("DEBUG: return selected action = " + returnAction.get(Tags.Desc, "NoDescAvailable"));
        return returnAction;
    }

    public void printReport(){
        System.out.println("***************************");
        System.out.println("*  Q learning report      *");
        System.out.println("***************************");
        System.out.println("Number of states in graph: "+graph.qlearningGuiStates.size());
        int numberOfActions = 0;
        int numberOfExecutedActions = 0;
        int numberOfExecutionsSum = 0;
        for(QlearningGuiState state:graph.qlearningGuiStates){
            numberOfActions = numberOfActions+state.abstractActionIdsAndExecutionCounters.size();
            for(int executionCounter:state.abstractActionIdsAndExecutionCounters.values()){
                if(executionCounter>0){
                    numberOfExecutedActions++;
                }
                numberOfExecutionsSum = numberOfExecutionsSum + executionCounter;
            }
        }
        System.out.println("Sum of actions in all of the states: "+numberOfActions);
        System.out.println("Sum of executed actions in all of the states: "+numberOfExecutedActions);
        System.out.println("Sum of action execution counters in all of the actions: "+numberOfExecutionsSum);
    }
}
