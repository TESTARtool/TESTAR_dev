package nl.ou.testar.genetic.programming.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static nl.ou.testar.genetic.programming.strategy.ActionExecutionStatus.LEAST;
import static nl.ou.testar.genetic.programming.strategy.ActionExecutionStatus.UNEXECUTED;

public class StrategyGuiStateImpl implements StrategyGuiState {
    private List<Action> actions = new ArrayList<>();
    private List<Action> previousActions = new ArrayList<>();
    private List<String> previousStates = new ArrayList<>();
    private Map<String, Integer> executed = new TreeMap<>();
    private Random rnd = new Random(System.currentTimeMillis());
    private State state;

    StrategyGuiStateImpl() { }

    public boolean isAvailable(final Role actionType) {
        return actions.stream().anyMatch(action -> action.get(Tags.Role) == actionType);
    }

    public int getNumberOfActions() {
        return actions.size();
    }

    public int getNumberOfActions(final Role actionType) {
        return (actionType == null) ? 0 : getActionsOfType(actionType).size();
    }

    public int getNumberOfUnexecutedActionsOfRole(final Role actionType) {
        return (int) actions.stream()
                .filter(action -> action.get(Tags.Role) == actionType && !(executed.keySet().contains(action.get(Tags.Abstract_R_T_P_ID))))
                .count();
    }

    public Action getRandomActionOfType(final Role actionType) {
        if (actionType == null) {
            return null;
        }
        final List<Action> actions = getActionsOfType(actionType);
        return actions.size() == 0 ? null : actions.get(rnd.nextInt(actions.size()));
    }

    public Action getRandomAction() {
        return !actions.isEmpty() ? actions.get(rnd.nextInt(actions.size())) : null;
    }

    public Action getRandomActionOfTypeOtherThan(final Role actionType) {
        final List<Action> actions = this.actions.stream()
                .filter(action -> !(action.get(Tags.Role) == actionType))
                .collect(Collectors.toList());
        return actions.size() == 0 ? null : actions.get(rnd.nextInt(actions.size()));
    }

    public Action getRandomActionOfType(final ActionExecutionStatus actionExecutionStatus) {
        return getRandomActionOfType(actionExecutionStatus, actions);
    }

    public Action getRandomActionOfType(final ActionExecutionStatus actionExecutionStatus, final List<Action> providedListOfActions) {
        if (executed.size() == 0) {
            System.out.println("List of executed actions is empty, returning a random action from the list of actions.");
            return getRandomActionOfType(providedListOfActions);
        }
        int i;
        final List<Action> filteredListOfActions = new ArrayList<>();

        if (UNEXECUTED == actionExecutionStatus) {
            i = 0;
        } else if (LEAST == actionExecutionStatus) {
            final boolean notExecutedAction = actions.stream()
                    .anyMatch(action -> !(executed.containsKey(action.get(Tags.AbstractID))));
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
                    .filter(action -> !executed.containsKey(action.get(Tags.AbstractID)))
                    .forEach(filteredListOfActions::add);
        } else {
            providedListOfActions.stream()
                    .filter(action -> executed.containsKey(action.get(Tags.AbstractID)) && executed.get(action.get(Tags.AbstractID)) == i)
                    .forEach(filteredListOfActions::add);
        }
        return (filteredListOfActions.size() == 0) ? null : filteredListOfActions.get(rnd.nextInt(filteredListOfActions.size()));
    }

    private Action getRandomActionOfType(List<Action> providedListOfActions) {
        if (providedListOfActions.size() != 0) {
            System.out.println("Getting a random action from the provided list.");
            return providedListOfActions.get(rnd.nextInt(providedListOfActions.size()));
        } else {
            System.out.println("Provided list is empty, returning null.");
            return null;
        }
    }

    public Action getRandomUnexecutedActionOfType(final Role actionType) {
        if (actionType == null) {
            System.out.println("ActionType is null, returning null");
            return null;
        }
        return getRandomActionOfType(UNEXECUTED, getActionsOfType(actionType));
    }

    public Action randomLeastExecutedAction() {
        return this.getRandomActionOfType(ActionExecutionStatus.LEAST);
    }

    public Action randomMostExecutedAction() {
        return this.getRandomActionOfType(ActionExecutionStatus.MOST);
    }

    public Action randomUnexecutedAction() {
        return this.getRandomActionOfType(ActionExecutionStatus.UNEXECUTED);
    }

    public List<Action> getActionsOfType(final Role actionType) {
        return actions.stream()
                .filter(action -> action.get(Tags.Role) == actionType)
                .collect(Collectors.toList());
    }

    public Action previousAction() {
        if (previousActions != null && !previousActions.isEmpty()) {
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


    public void updateState(final State state, final Set<Action> acts) {
        this.state = state;
        this.actions = new ArrayList<>(acts);
    }

    public void addActionToPreviousActions(final Action action) {
        System.out.println("Adding the selected action of type '" + action.get(Tags.Role) + " ' to the history...");
        try {
            previousActions.add(action);
            this.incrementPreviousExecutedActions(action);
        } catch (NoSuchTagException e) {
            System.out.println("This was an irregular action, I've not added it to the history.");
        }

    }

    private void incrementPreviousExecutedActions(final Action previousAction) {
        final String pa = previousAction.get(Tags.AbstractID);
        if (executed.containsKey(pa)) {
            executed.put(pa, executed.get(pa) + 1);
        } else {
            executed.put(pa, 1);
        }
    }

    public boolean hasStateNotChanged() {
        return previousStates.size() >= 2 && previousStates.get(previousStates.size() - 1).equals(previousStates.get(previousStates.size() - 2));
    }

    public void addStateToPreviousStates(final State st) {
        previousStates.add(st.get(Tags.Abstract_R_T_P_ID));
    }
}
