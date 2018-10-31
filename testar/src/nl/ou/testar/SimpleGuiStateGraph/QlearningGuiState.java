package nl.ou.testar.SimpleGuiStateGraph;

import org.fruit.alayer.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class QlearningGuiState {
    protected String concreteStateId;
    protected HashMap<String, Double> concreteActionIdsAndRewards;
    protected HashMap<String, Double> concreteActionIdsAndQValues;
    protected HashMap<String, Integer> concreteActionIdsAndExecutionCounters;
    protected Set<String> unvisitedActionIds;
    protected Set<GuiStateTransition> stateTransitions;

    public QlearningGuiState(String concreteStateId, HashMap<String, Double> concreteActionIdsAndRewards) {
        this.concreteStateId = concreteStateId;
        this.concreteActionIdsAndRewards = concreteActionIdsAndRewards;
        this.concreteActionIdsAndQValues = concreteActionIdsAndRewards; // all Q values are the same as R Max in the beginning
        this.unvisitedActionIds = concreteActionIdsAndRewards.keySet(); // all are unvisited when creating
        //creating execution counters for each action:
        concreteActionIdsAndExecutionCounters = new HashMap<String, Integer>();
        for(String id:concreteActionIdsAndRewards.keySet()){
            concreteActionIdsAndExecutionCounters.put(id,0);
        }
        stateTransitions = new HashSet<GuiStateTransition>();
    }

    public double getMaxQValueOfTheState(){
        double qValue = 0;
        for(double q:concreteActionIdsAndQValues.values()){
            if(q>qValue) qValue = q;
        }
        return qValue;
    }

    public ArrayList<String> getActionsIdsWithMaxQvalue(){
        ArrayList<String> actionIdsWithMaxQvalue = new ArrayList<String>();
        double maxQValue = getMaxQValueOfTheState();
        for(String actionId:concreteActionIdsAndQValues.keySet()){
            if(concreteActionIdsAndQValues.get(actionId).equals(maxQValue)){
                actionIdsWithMaxQvalue.add(actionId);
            }
        }
        System.out.println("DEBUG: max Q value of the state was "+maxQValue+", and "+actionIdsWithMaxQvalue.size()+" action with that value");
        return actionIdsWithMaxQvalue;
    }

    public void addStateTransition(GuiStateTransition newTransition, double gammaDiscount, double maxRMaxOfTheNewState){
        if(stateTransitions.size()>0){
            //if existing transitions, checking for identical ones:
            for(GuiStateTransition guiStateTransition:stateTransitions){
                if(guiStateTransition.getSourceStateConcreteId().equals(newTransition.getSourceStateConcreteId())){
                    // the same source state, as it should be:
                    if(guiStateTransition.getActionConcreteId().equals(newTransition.getActionConcreteId())){
                        // also the action is the same:
                        if(guiStateTransition.getTargetStateConcreteId().equals(newTransition.getTargetStateConcreteId())){
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
        updateRMaxAndQValues(newTransition.getActionConcreteId(), gammaDiscount, maxRMaxOfTheNewState);
    }

    private void updateRMaxAndQValues(String actionConcreteId, double gammaDiscount, double maxQValueOfTheNewState){
        int executionCounter = concreteActionIdsAndExecutionCounters.get(actionConcreteId);
        executionCounter++;
        System.out.println("DEBUG: execution counter for action "+actionConcreteId+" is now "+executionCounter);
        concreteActionIdsAndExecutionCounters.put(actionConcreteId,executionCounter);
        double reward = calculateReward(executionCounter);
        System.out.println("DEBUG: new reward for action "+actionConcreteId+" is "+reward);
        concreteActionIdsAndRewards.put(actionConcreteId,reward);
        double qValue = calculateQValue(reward,gammaDiscount,maxQValueOfTheNewState);
        System.out.println("DEBUG: new Q value for action "+actionConcreteId+" is "+qValue);
        concreteActionIdsAndQValues.put(actionConcreteId,qValue);
    }

    private double calculateReward(int executionCounter){
        double reward=0;
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

    public void addVisitedAction(String concreteActionId){
        if(unvisitedActionIds.contains(concreteActionId)){
            System.out.println(this.getClass()+": addVisitedAction: action removed from the unvisited actions");
            unvisitedActionIds.remove(concreteActionId);
        }else{
            System.out.println(this.getClass()+": addVisitedAction: action not found from the unvisited actions");
        }
    }

    public Set<GuiStateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public String getConcreteStateId() {
        return concreteStateId;
    }

    public void setConcreteStateId(String concreteStateId) {
        this.concreteStateId = concreteStateId;
    }

    public HashMap<String, Double>  getConcreteActionIdsAndRewards() {
        return concreteActionIdsAndRewards;
    }

    public void setConcreteActionIdsAndRewards(HashMap<String, Double>  concreteActionIdsAndRewards) {
        this.concreteActionIdsAndRewards = concreteActionIdsAndRewards;
    }

    public Set<String> getUnvisitedActionIds() {
        return unvisitedActionIds;
    }

    public void setUnvisitedActionIds(Set<String> unvisitedActionIds) {
        this.unvisitedActionIds = unvisitedActionIds;
    }
}
