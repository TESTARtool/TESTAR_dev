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

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.*;

public class QlearningGuiState {
    protected String abstractCustomStateId;
    //TODO use QlearningValues instead and only 1 hash map
    protected HashMap<String, Double> abstractCustomActionIdsAndRewards;
    protected HashMap<String, Double> abstractCustomActionIdsAndQValues;
    protected HashMap<String, Integer> abstractCustomActionIdsAndExecutionCounters;
    protected Set<GuiStateTransition> stateTransitions;

    public QlearningGuiState(String abstractCustomStateId, HashMap<String, Double> abstractCustomActionIdsAndRewards) {
        this.abstractCustomStateId = abstractCustomStateId;
        this.abstractCustomActionIdsAndRewards = abstractCustomActionIdsAndRewards;
        this.abstractCustomActionIdsAndQValues = abstractCustomActionIdsAndRewards; // all Q values are the same as R Max in the beginning
        //creating execution counters for each action:
        abstractCustomActionIdsAndExecutionCounters = new HashMap<String, Integer>();
        for(String id:abstractCustomActionIdsAndRewards.keySet()){
            abstractCustomActionIdsAndExecutionCounters.put(id,0);
        }
        stateTransitions = new HashSet<GuiStateTransition>();
    }

    /**
     * Finding the highest Q value, that is also in the given set of available actions
     * @param actions
     * @return
     */
    public double getMaxQValueOfTheState(Set<Action> actions){
        double qValue = 0;
        for(Map.Entry<String, Double> entry:abstractCustomActionIdsAndQValues.entrySet()){
            if(entry.getValue()>qValue){
                for(Action action:actions){
                    if(action.get(Tags.AbstractIDCustom).equals(entry.getKey())){
                        qValue = entry.getValue();
                    }
                }
            }
        }
        return qValue;
    }

    public ArrayList<String> getActionsIdsWithMaxQvalue(Set<Action> actions){
        ArrayList<String> actionIdsWithMaxQvalue = new ArrayList<String>();
        double maxQValue = getMaxQValueOfTheState(actions);
        for(String actionId:abstractCustomActionIdsAndQValues.keySet()){
            if(abstractCustomActionIdsAndQValues.get(actionId).equals(maxQValue)){
                //checking that the actionID from the model is also in the list of available actions of the state:
                for(Action action:actions){
                    if(action.get(Tags.AbstractIDCustom).equals(actionId)){
                        actionIdsWithMaxQvalue.add(actionId);
                    }
                }
            }
        }
        System.out.println("DEBUG: max Q value of the state was "+maxQValue+", and "+actionIdsWithMaxQvalue.size()+" action with that value");
        return actionIdsWithMaxQvalue;
    }

    /**
     * For some reason, the actionIDs are changing even if the AbstractIDCustom is the same
     * So updating the actionIDs
     *
     */
    public void updateActionIdsOfTheStateIntoModel(Set<Action> actions, double R_MAX){
        for(Action action:actions){
            if(abstractCustomActionIdsAndQValues.containsKey(action.get(Tags.AbstractIDCustom))){
                // model contains the action ID
            }else{
                abstractCustomActionIdsAndQValues.put(action.get(Tags.AbstractIDCustom),R_MAX);
                abstractCustomActionIdsAndRewards.put(action.get(Tags.AbstractIDCustom),R_MAX);
                abstractCustomActionIdsAndExecutionCounters.put(action.get(Tags.AbstractIDCustom),0);
            }
        }
    }

    public void addStateTransition(GuiStateTransition newTransition, double gammaDiscount, double maxRMaxOfTheNewState){
        //updating reward and Q value for the executed action:
        updateRMaxAndQValues(newTransition.getActionAbstractCustomId(), gammaDiscount, maxRMaxOfTheNewState);
        if(stateTransitions.size()>0){
            //if existing transitions, checking for identical ones:
            for(GuiStateTransition guiStateTransition:stateTransitions){
                if(guiStateTransition.getSourceStateAbstractCustomId().equals(newTransition.getSourceStateAbstractCustomId())){
                    // the same source state, as it should be:
                    if(guiStateTransition.getActionAbstractCustomId().equals(newTransition.getActionAbstractCustomId())){
                        // also the action is the same:
                        if(guiStateTransition.getTargetStateAbstractCustomId().equals(newTransition.getTargetStateAbstractCustomId())){
                            // also the target state is the same -> identical transition
                            System.out.println(this.getClass()+": addStateTransition: identical transition found - no need to save again");
                            return;
                        }else{
                            // same source state and same action, but different target state -> some external factor or the data values affect the behaviour
                            System.out.println(this.getClass()+": addStateTransition: WARNING: same source state, same action, but different target state!");
                        }
                    }
                }else{
                    System.out.println(this.getClass()+": ERROR, source state is NOT same as in other state transitions from the same state!");
                }
            }
        }
        // otherwise adding the new state transition:
//        System.out.println(this.getClass()+": addStateTransition: adding the new state transition");
        stateTransitions.add(newTransition);
    }

    private void updateRMaxAndQValues(String actionAbstractCustomId, double gammaDiscount, double maxQValueOfTheNewState){
        int executionCounter = abstractCustomActionIdsAndExecutionCounters.get(actionAbstractCustomId);
        executionCounter++;
        System.out.println("DEBUG: execution counter for action "+actionAbstractCustomId+" is now "+executionCounter);
        abstractCustomActionIdsAndExecutionCounters.put(actionAbstractCustomId,executionCounter);
        double reward = calculateReward(executionCounter);
        System.out.println("DEBUG: new reward for action "+actionAbstractCustomId+" is "+reward);
        abstractCustomActionIdsAndRewards.put(actionAbstractCustomId,reward);
        double qValue = calculateQValue(reward,gammaDiscount,maxQValueOfTheNewState);
        System.out.println("DEBUG: new Q value for action "+actionAbstractCustomId+" is "+qValue);
        abstractCustomActionIdsAndQValues.put(actionAbstractCustomId,qValue);
    }

    private double calculateReward(int executionCounter){
        double reward=0.0;
        if(executionCounter==0){
            System.out.println("ERROR - calculating Q value for unvisited action should not be needed!");
        }else{
            System.out.println("DEBUG: executionCounter="+executionCounter);
            int divider = executionCounter+1;
            reward = 1.0/(double)divider;
            System.out.println("DEBUG: reward="+reward);
        }
        return reward;
    }

    private double calculateQValue(double reward, double gammaDiscount, double maxQValueOfTheNewState){
        return reward + gammaDiscount * maxQValueOfTheNewState;
    }

    public Set<GuiStateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public String getAbstractCustomStateId() {
        return abstractCustomStateId;
    }

    public void setAbstractCustomStateId(String abstractCustomStateId) {
        this.abstractCustomStateId = abstractCustomStateId;
    }

    public HashMap<String, Double>  getAbstractCustomActionIdsAndRewards() {
        return abstractCustomActionIdsAndRewards;
    }

    public void setAbstractCustomActionIdsAndRewards(HashMap<String, Double>  abstractCustomActionIdsAndRewards) {
        this.abstractCustomActionIdsAndRewards = abstractCustomActionIdsAndRewards;
    }
}
