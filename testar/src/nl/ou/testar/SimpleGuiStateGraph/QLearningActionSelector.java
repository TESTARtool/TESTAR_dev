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
import java.util.Random;
import java.util.Set;

public class QLearningActionSelector {
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

    public Action selectAction(State state, Set<Action> actions) {
        // saving the starting node of the graph:
        if(graph.startingStateAbstractCustomId==null){
            graph.startingStateAbstractCustomId=state.get(Tags.AbstractIDCustom);
        }

        // Finding the current state from the previous states:
        QlearningGuiState currentQlearningGuiState = graph.getStateByAbstractCustomId(state.get(Tags.AbstractIDCustom));

        // If it's a new state:
        if(currentQlearningGuiState==null) { // did not contain the state ID -> a new state
            // new state:
//            System.out.println(this.getClass()+": selectAction(): new state");
            currentQlearningGuiState = graph.createQlearningGuiState(state, actions);
        }else{
            //update the actions of the state - for some reason the action IDs are changing:
            currentQlearningGuiState.updateActionIdsOfTheStateIntoModel(actions, R_MAX);
        }


        System.out.println("DEBUG: state ID from model="+currentQlearningGuiState.getAbstractCustomStateId());
       for(String id:currentQlearningGuiState.abstractCustomActionIdsAndQValues.keySet()){
            System.out.println("DEBUG: id="+id+", Q-value="+currentQlearningGuiState.abstractCustomActionIdsAndQValues.get(id));
       }

        // adding state transition to the graph: previous state + previous action = current state
        if(graph.previousStateAbstractCustomId!=null && graph.previousActionAbstractCustomId != null){ //else the first action and there is no transition yet
            QlearningGuiState previousState = graph.getStateByAbstractCustomId(graph.previousStateAbstractCustomId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                graph.qlearningGuiStates.remove(previousState);//removing the old version of the state
//                System.out.println(this.getClass()+": new state transition: previousStateId="+previousStateAbstractCustomId+", targetStateId="+state.get(Tags.AbstractIDCustom)+", previousActionAbstractCustomId="+previousActionAbstractCustomId);
                previousState.addStateTransition(new GuiStateTransition(graph.previousStateAbstractCustomId,state.get(Tags.AbstractIDCustom),graph.previousActionAbstractCustomId),gammaDiscount,currentQlearningGuiState.getMaxQValueOfTheState(actions));
                graph.qlearningGuiStates.add(previousState);//adding the updated version of the state
            }
        }
        Action returnAction = null;
        ArrayList<String> actionIdsWithMaxQvalue = currentQlearningGuiState.getActionsIdsWithMaxQvalue(actions);
        if(actionIdsWithMaxQvalue.size()==0){
            System.out.println("ERROR: Qlearning did not find actions with max Q value!");
            returnAction = RandomActionSelector.selectAction(actions);
        }else{
            //selecting randomly of the actionIDs that have max Q value:
            System.out.println("DEBUG: IDs of actions with max Q value:");
            for(String id:actionIdsWithMaxQvalue){
                System.out.println("DEBUG: id="+id);
            }
            long graphTime = System.currentTimeMillis();
            Random rnd = new Random(graphTime);
            String abstractCustomIdOfRandomAction = actionIdsWithMaxQvalue.get(rnd.nextInt(actionIdsWithMaxQvalue.size()));
            System.out.println("DEBUG: randomly chosen id="+abstractCustomIdOfRandomAction);
            System.out.println("DEBUG: stateID from state="+state.get(Tags.AbstractIDCustom));
            returnAction = graph.getActionWithAbstractCustomId(actions, abstractCustomIdOfRandomAction);
        }

        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println("ERROR: QlearningActionSelector.selectAction(): no action found! Getting purely random action.");
            returnAction = RandomActionSelector.selectAction(actions);
        }
        //updating the list of states:
        graph.qlearningGuiStates.remove(currentQlearningGuiState); // should not be a problem if state not there (new state)?
        graph.qlearningGuiStates.add(currentQlearningGuiState);
        // saving the state and action for state transition after knowing the target state:
        graph.previousActionAbstractCustomId = returnAction.get(Tags.AbstractIDCustom);
        graph.previousStateAbstractCustomId = state.get(Tags.AbstractIDCustom);
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
            numberOfActions = numberOfActions+state.abstractCustomActionIdsAndExecutionCounters.size();
            for(int executionCounter:state.abstractCustomActionIdsAndExecutionCounters.values()){
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
