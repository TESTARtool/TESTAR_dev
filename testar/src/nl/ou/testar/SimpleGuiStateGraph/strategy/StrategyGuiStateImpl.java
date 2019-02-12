package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.SimpleGuiStateGraph.GuiStateTransition;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus.LEAST;
import static nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus.UNEX;

public class StrategyGuiStateImpl implements StrategyGuiState {
    private String concreteStateId;
    //TODO use QlearningValues instead and only 1 hash map
    private HashMap<String, Double> concreteActionIdsAndRewards;
    private HashMap<String, Double> concreteActionIdsAndQValues;
    private HashMap<String, Integer> concreteActionIdsAndExecutionCounters;
    private Set<GuiStateTransition> stateTransitions;

    private List<Action> actions = new ArrayList<>();
    private State state = null;
    private List<Action> previousActions = new ArrayList<>();
    private List<String> previousStates = new ArrayList<>();
    private TreeMap<String, Integer> executed = new TreeMap<>();
    private Random rnd = new Random(System.currentTimeMillis());

    StrategyGuiStateImpl(final String concreteStateId, final HashMap<String, Double> concreteActionIdsAndRewards) {
        this.concreteStateId = concreteStateId;
        this.concreteActionIdsAndRewards = concreteActionIdsAndRewards;
        this.concreteActionIdsAndQValues = concreteActionIdsAndRewards; // all Q values are the same as R Max in the beginning
        //creating execution counters for each action:
        concreteActionIdsAndExecutionCounters = new HashMap<String, Integer>();
        for (String id : concreteActionIdsAndRewards.keySet()) {
            concreteActionIdsAndExecutionCounters.put(id, 0);
        }
        stateTransitions = new HashSet<GuiStateTransition>();
    }

    /**
     * Finding the highest Q value, that is also in the given set of available actions
     *
     * @param actions
     * @return
     */
    public double getMaxQValueOfTheState(Set<Action> actions) {
        double qValue = 0;
        for (Map.Entry<String, Double> entry : concreteActionIdsAndQValues.entrySet()) {
            if (entry.getValue() > qValue) {
                for (Action action : actions) {
                    if (action.get(Tags.ConcreteID).equals(entry.getKey())) {
                        qValue = entry.getValue();
                    }
                }
            }
        }
        return qValue;
    }

    public ArrayList<String> getActionsIdsWithMaxQvalue(Set<Action> actions) {
        ArrayList<String> actionIdsWithMaxQvalue = new ArrayList<String>();
        double maxQValue = getMaxQValueOfTheState(actions);
        for (String actionId : concreteActionIdsAndQValues.keySet()) {
            if (concreteActionIdsAndQValues.get(actionId).equals(maxQValue)) {
                //checking that the actionID from the model is also in the list of available actions of the state:
                for (Action action : actions) {
                    if (action.get(Tags.ConcreteID).equals(actionId)) {
                        actionIdsWithMaxQvalue.add(actionId);
                    }
                }
            }
        }
        System.out.println("DEBUG: max Q value of the state was " + maxQValue + ", and " + actionIdsWithMaxQvalue.size() + " action with that value");
        return actionIdsWithMaxQvalue;
    }

    /**
     * For some reason, the actionIDs are changing even if the ConcreteStateID is the same
     * So updating the actionIDs
     */
    StrategyGuiStateImpl updateActionIdsOfTheStateIntoModel(Set<Action> actions, double R_MAX) {
        actions.stream()
                .filter(action -> concreteActionIdsAndQValues.containsKey(action.get(Tags.ConcreteID)))
                .forEach(action -> {
                    concreteActionIdsAndQValues.put(action.get(Tags.ConcreteID), R_MAX);
                    concreteActionIdsAndRewards.put(action.get(Tags.ConcreteID), R_MAX);
                    concreteActionIdsAndExecutionCounters.put(action.get(Tags.ConcreteID), 0);
                });
        return this;
    }

    void addStateTransition(GuiStateTransition newTransition, double gammaDiscount, double maxRMaxOfTheNewState) {
        //updating reward and Q value for the executed action:
        updateRMaxAndQValues(newTransition.getActionConcreteId(), gammaDiscount, maxRMaxOfTheNewState);
        if (stateTransitions.size() > 0) {
            //if existing transitions, checking for identical ones:
            for (GuiStateTransition guiStateTransition : stateTransitions) {
                if (guiStateTransition.getSourceStateConcreteId().equals(newTransition.getSourceStateConcreteId())) {
                    // the same source state, as it should be:
                    if (guiStateTransition.getActionConcreteId().equals(newTransition.getActionConcreteId())) {
                        // also the action is the same:
                        if (guiStateTransition.getTargetStateConcreteId().equals(newTransition.getTargetStateConcreteId())) {
                            // also the target state is the same -> identical transition
                            System.out.println(this.getClass() + ": addStateTransition: identical transition found - no need to save again");
                            return;
                        } else {
                            // same source state and same action, but different target state -> some external factor or the data values affect the behaviour
                            System.out.println(this.getClass() + ": addStateTransition: WARNING: same source state, same action, but different target state!");
                        }
                    }
                } else {
                    System.out.println(this.getClass() + ": ERROR, source state is NOT same as in other state transitions from the same state!");
                }
            }
        }
        // otherwise adding the new state transition:
//        System.out.println(this.getClass()+": addStateTransition: adding the new state transition");
        stateTransitions.add(newTransition);
    }

    private void updateRMaxAndQValues(String actionConcreteId, double gammaDiscount, double maxQValueOfTheNewState) {
        int executionCounter = concreteActionIdsAndExecutionCounters.get(actionConcreteId);
        executionCounter++;
        System.out.println("DEBUG: execution counter for action " + actionConcreteId + " is now " + executionCounter);
        concreteActionIdsAndExecutionCounters.put(actionConcreteId, executionCounter);
        double reward = calculateReward(executionCounter);
        System.out.println("DEBUG: new reward for action " + actionConcreteId + " is " + reward);
        concreteActionIdsAndRewards.put(actionConcreteId, reward);
        double qValue = calculateQValue(reward, gammaDiscount, maxQValueOfTheNewState);
        System.out.println("DEBUG: new Q value for action " + actionConcreteId + " is " + qValue);
        concreteActionIdsAndQValues.put(actionConcreteId, qValue);
    }

    private double calculateReward(int executionCounter) {
        double reward = 0.0;
        if (executionCounter == 0) {
            System.out.println("ERROR - calculating Q value for unvisited action should not be needed!");
        } else {
            System.out.println("DEBUG: executionCounter=" + executionCounter);
            int divider = executionCounter + 1;
            reward = 1.0 / (double) divider;
            System.out.println("DEBUG: reward=" + reward);
        }
        return reward;
    }

    private double calculateQValue(double reward, double gammaDiscount, double maxQValueOfTheNewState) {
        return reward + gammaDiscount * maxQValueOfTheNewState;
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

    public HashMap<String, Double> getConcreteActionIdsAndRewards() {
        return concreteActionIdsAndRewards;
    }

    public void setConcreteActionIdsAndRewards(HashMap<String, Double> concreteActionIdsAndRewards) {
        this.concreteActionIdsAndRewards = concreteActionIdsAndRewards;
    }

    public boolean isAvailable(final Role actionType) {
        return actions.stream().anyMatch(action -> action.get(Tags.Role) == actionType);
    }

    public int getNumberOfActions() {
        return actions.size();
    }

    public int getNumberOfActions(final Role actionType) {
        return (actionType == null) ? 0 : getActionsOfType(actionType).size();
    }

    public int getNumberOfActions(final Role actionType, final ActionExecutionStatus actionExecutionStatus) {
        int result = 0;
        if (actionExecutionStatus == UNEX) {
            result = (int) actions.stream()
                    .filter(action -> action.get(Tags.Role) == actionType && !(executed.keySet().contains(action.get(Tags.ConcreteID))))
                    .count();
        }
        return result;
    }

    public Action getRandomAction(final Role actionType) {
        if (actionType == null) {
            return null;
        }
        final List<Action> actions = getActionsOfType(actionType);
        return actions.size() == 0 ? null : actions.get(rnd.nextInt(actions.size()));
    }

    public Action getRandomAction() {
        return actions.get(rnd.nextInt(actions.size()));
    }

    public Action getRandomActionOfTypeOtherThan(final Role actionType) {
        final List<Action> actions = this.actions.stream()
                .filter(action -> !(action.get(Tags.Role) == actionType))
                .collect(Collectors.toList());
        return actions.size() == 0  ? null : actions.get(rnd.nextInt(actions.size()));
    }

    public Action getRandomAction(final ActionExecutionStatus actionExecutionStatus) {
        return getRandomAction(actionExecutionStatus, actions);
    }

    public Action getRandomAction(final ActionExecutionStatus actionExecutionStatus, final List<Action> providedListOfActions) {
        if (executed.size() == 0) {
            System.out.println("List of executed actions is empty, returning a random action from the list of actions.");
            return getRandomAction(providedListOfActions);
        }
        int i;
        final List<Action> filteredListOfActions = new ArrayList<>();

        if (UNEX == actionExecutionStatus) {
            i = 0;
        } else if (LEAST == actionExecutionStatus) {
            final boolean notExecutedAction = actions.stream()
                    .anyMatch(action -> !(executed.containsKey(action.get(Tags.ConcreteID))));
            if (notExecutedAction) {
                i = 0;
            } else {
                i = Collections.min(executed.values());
            }
        } else {
            i = Collections.max(executed.values());
        }

        if (i == 0) {
            providedListOfActions.stream()
                    .filter(action -> !executed.containsKey(action.get(Tags.ConcreteID)))
                    .forEach(filteredListOfActions::add);
        } else {
            providedListOfActions.stream()
                    .filter(action -> executed.containsKey(action.get(Tags.ConcreteID)) && executed.get(action.get(Tags.ConcreteID)) == i)
                    .forEach(filteredListOfActions::add);
        }
        return (filteredListOfActions.size() == 0) ? null : filteredListOfActions.get(rnd.nextInt(filteredListOfActions.size()));
    }

    private Action getRandomAction(List<Action> providedListOfActions) {
        if (providedListOfActions.size() != 0) {
            System.out.println("Getting a random action from the provided list.");
            return providedListOfActions.get(rnd.nextInt(providedListOfActions.size()));
        } else {
            System.out.println("Provided list is empty, returning null.");
            return null;
        }
    }

    public Action getRandomAction(final Role actionType, final ActionExecutionStatus actionExecutionStatus) {
        if (actionType == null) {
            System.out.println("ActionType is null, returning null");
            return null;
        }
        return getRandomAction(actionExecutionStatus, getActionsOfType(actionType));
    }

    public List<Action> getActionsOfType(Role actiontype) {
        List<Action> actionsoftype = new ArrayList<>();
        for (Action a : actions) {
            if (a.get(Tags.Role) == actiontype) {
                actionsoftype.add(a);
            }
        }
        System.out.println("Returning all actions of type " + actiontype.toString() + ": found " + actionsoftype.size());
        return actionsoftype;
    }

    public Action previousAction() {
        if (previousActions.size() != 0) {
            System.out.println("Returning the previous action");
            return previousActions.get(previousActions.size() - 1);

        } else {
            System.out.println("There are no previous actions, returning null.");
            return getRandomAction();
        }
    }

    public int getNumberOfPreviousActions() {
        System.out.println("Returning the number of previous actions: " + previousActions.size());
        return previousActions.size();
    }


    public void setState(State state, Set<Action> acts) {
        this.state = state;
        this.actions = new ArrayList<>(acts);
    }

    public void setPreviousAction(Action previousAction) {
        System.out.println("Adding the selected action to the history...");
        try {
            previousActions.add(previousAction);

            int i = 1;

            String pa = previousAction.get(Tags.ConcreteID);
            if (executed.containsKey(pa))
                i = executed.get(pa) + 1;
            executed.put(pa, i);
        } catch (NoSuchTagException e) {
            System.out.println("This was an irregular action, I've not added it to the history.");
        }

    }

    public boolean hasStateNotChanged() {
        return previousStates.size() >= 2 && previousStates.get(previousStates.size() - 1) == previousStates.get(previousStates.size() - 2);
    }

    public void setPreviousState(State st) {
        System.out.println("Adding state to the history...");
        if (previousStates.size() != 0 && previousStates.get(previousStates.size() - 1) == state.get(Tags.ConcreteID)) {
            System.out.println("Hmmm I'm still in the same state!");
        } else if (previousStates.contains(st.get(Tags.ConcreteID))) {
            System.out.println("Hey, I've been here before!");
        }
        previousStates.add(st.get(Tags.ConcreteID));

    }
}
