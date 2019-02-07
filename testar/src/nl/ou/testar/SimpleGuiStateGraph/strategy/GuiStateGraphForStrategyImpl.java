package nl.ou.testar.SimpleGuiStateGraph.strategy;

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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus.LEAST;
import static nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus.MOST;
import static nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus.UNEX;

public class GuiStateGraphForStrategyImpl implements GuiStateGraphForStrategy {
    private Set<StrategyGuiState> strategyGuiStates;
    private String startingStateConcreteId;
    private String previousStateConcreteId;
    private String previousActionConcreteId;

    private List<Action> actions = new ArrayList<>();
    private State state = null;
    private List<Action> previousActions = new ArrayList<>();
    private List<String> previousStates = new ArrayList<>();
    private TreeMap<String, Integer> executed = new TreeMap<>();
    private Random rnd = new Random(System.currentTimeMillis());

    GuiStateGraphForStrategyImpl() {
        strategyGuiStates = new HashSet<>();
    }

    public Set<StrategyGuiState> getStrategyGuiStates() {
        return strategyGuiStates;
    }

    public String getStartingStateConcreteId() {
        return startingStateConcreteId;
    }

    public void setStartingStateConcreteId(final String startingStateConcreteId) {
        this.startingStateConcreteId = startingStateConcreteId;
    }

    public String getPreviousStateConcreteId() {
        return previousStateConcreteId;
    }

    public void setPreviousStateConcreteId(String previousStateConcreteId) {
        this.previousStateConcreteId = previousStateConcreteId;
    }

    public String getPreviousActionConcreteId() {
        return previousActionConcreteId;
    }

    public void setPreviousActionConcreteId(String previousActionConcreteId) {
        this.previousActionConcreteId = previousActionConcreteId;
    }

    public void startANewTestSequence() {
        previousActionConcreteId = null;
        previousStateConcreteId = null;
    }

    public Optional<Action> getActionWithConcreteId(final Set<Action> actions, final String concreteActionId) {
        return actions.stream()
                .filter(action -> action.get(Tags.ConcreteID).equals(concreteActionId))
                .findFirst();
    }

    public Optional<StrategyGuiState> getStateByConcreteId(final String concreteStateId) {
        return strategyGuiStates.stream()
                .filter(state -> state.getConcreteStateId().equals(concreteStateId))
                .findFirst();
    }

    public StrategyGuiState createStrategyGuiState(final State state, final Set<Action> actions) {
        final HashMap<String, Double> actionIds = new HashMap<>();
        actions.forEach(action -> actionIds.put(action.get(Tags.ConcreteID), null));
        return new StrategyGuiState(state.get(Tags.ConcreteID), actionIds);
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

    public long getNumberOfActions(final Role actionType, final String status) {
        long result = 0;
        if (UNEX == ActionExecutionStatus.valueOf(status)) {
            result = actions.stream()
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

    public Action getRandomAction(String status) {
        return getRandomAction(status, actions);
    }

    public Action getRandomAction(String status, List<Action> providedListofActions) {
        if (executed.size() == 0) {
            System.out.println("List of executed actions is empty, returning a random action from the list of actions.");
            return getRandomAction(providedListofActions);
        }
        final ActionExecutionStatus actionExecutionStatus = ActionExecutionStatus.valueOf(status);
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
            providedListofActions.stream()
                    .filter(action -> !executed.containsKey(action.get(Tags.ConcreteID)))
                    .forEach(filteredListOfActions::add);
        } else {
            providedListofActions.stream()
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

    public Action getRandomAction(Role actiontype, String status) {
        if (actiontype == null) {
            System.out.println("Actiontype is null, returning null");
            return null;
        }
        System.out.println("Getting a random action of type " + actiontype.toString() + " and status = " + status);
        return getRandomAction(status, getActionsOfType(actiontype));
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


    public void setState(IEnvironment env, State state, Set<Action> acts) {
        this.state = state;
        this.actions = new ArrayList<Action>(acts);
        this.environment = env;
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
