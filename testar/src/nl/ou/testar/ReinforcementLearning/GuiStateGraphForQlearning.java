package nl.ou.testar.ReinforcementLearning;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import javax.annotation.Nonnull;
import java.util.*;

public class GuiStateGraphForQlearning {
    private String previousStateConcreteId;
    private String previousActionConcreteId;
    private double R_MAX;
    private double gammaDiscount;

    private Set<QlearningGuiState> qlearningGuiStates;
    private String startingStateConcreteId;

    protected Set<QlearningGuiState> getQlearningGuiStates() {
        return qlearningGuiStates;
    }

    public String getStartingStateConcreteId() {
        return startingStateConcreteId;
    }

    public void setStartingStateConcreteId(String startingStateConcreteId) {
        this.startingStateConcreteId = startingStateConcreteId;
    }

    public String getPreviousActionConcreteId() {
        return previousActionConcreteId;
    }

    public void setPreviousActionConcreteId(String previousActionConcreteId) {
        this.previousActionConcreteId = previousActionConcreteId;
    }

    public String getPreviousStateConcreteId() {
        return previousStateConcreteId;
    }

    public void setPreviousStateConcreteId(String previousStateConcreteId) {
        this.previousStateConcreteId = previousStateConcreteId;
    }

    public GuiStateGraphForQlearning(double R_MAX, double gammaDiscount) {
        this.R_MAX = R_MAX;
        this.gammaDiscount=gammaDiscount;

        qlearningGuiStates = new HashSet<QlearningGuiState>();
    }

    /**
     * Resetting the last action and last state to null for a new test sequence
     */
    public void startANewTestSequence(){
        previousActionConcreteId=null;
        previousStateConcreteId=null;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param concreteActionId
     * @return
     */
    protected Action getActionWithConcreteId(Set<Action> actions, String concreteActionId){
        System.out.println("DEBUG: trying to find action with a matching ID:");
        for(Action action:actions){
            System.out.println("DEBUG: action.ConcreteID="+action.get(Tags.ConcreteID)+", idToMatch="+concreteActionId);
            // find the action with concreteId:
            if(action.get(Tags.ConcreteID).equals(concreteActionId)){
                System.out.println("DEBUG: match found!");
                return action;
            }
        }
        System.out.println("ERROR: matching action ID not found!");
        return null;
    }

    /**
     * Returns null if not found
     *
     * @param concreteStateId
     * @return
     */
    protected QlearningGuiState getStateByConcreteId(String concreteStateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getConcreteStateId().equals(concreteStateId)){
                return state;
            }
        }
        //System.out.println("DEBUG: state with ID="+concreteStateId+" was not found - a new state.");
        return null;
    }

    protected QlearningGuiState createQlearningGuiState(State state, Set<Action> actions){
        HashMap<String, Double> actionIds = new HashMap<String, Double>();
        for(Action action:actions){
            actionIds.put(action.get(Tags.ConcreteID), R_MAX);
        }
        return new QlearningGuiState(state.get(Tags.ConcreteID),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getConcreteStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

    /**
     * This method retrieves for a set of actions the q-values for the State action combination
     * @param state The current state
     * @param actions The list of actions
     * @return A map containing actions and their q-value, the default return value is 0
     */
    @Nonnull
    public Multimap<Double, Action> getActionQValues(@Nonnull State state, @Nonnull Set<Action> actions) {
        Multimap <Double, Action> qvalues = HashMultimap.create();
        // TODO Aaron get get Q-values for selected actions
        return qvalues;
    }
}
