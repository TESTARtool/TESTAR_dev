package nl.ou.testar.genetic.programming.strategy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.util.*;
import java.util.stream.Collectors;

import static nl.ou.testar.genetic.programming.strategy.ActionExecutionStatus.LEAST;
import static nl.ou.testar.genetic.programming.strategy.ActionExecutionStatus.UNEXECUTED;

public class StrategyGuiStateImpl implements StrategyGuiState {
    private List<Action> actions = new ArrayList<>();
    private List<Action> previousActions = new ArrayList<>();
    private List<String> previousStates = new ArrayList<>();
    private Map<String, Integer> executed = new TreeMap<>();
    private int actionNotFound = 0;
    private List<Action> irregularActions = new ArrayList<>();
    private Random rnd = new Random(System.currentTimeMillis());

    private Tag<String> ACTION_ID = Tags.Desc;
    private Tag<String> STATE_ID;

    private static final Logger logger = LogManager.getLogger(StrategyGuiStateImpl.class);

    StrategyGuiStateImpl() {
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

    public int getNumberOfUnexecutedActionsOfRole(final Role actionType) {
        return (int) actions.stream()
                .filter(action -> action.get(Tags.Role) == actionType && !(executed.keySet().contains(action.get(ACTION_ID))))
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
            logger.debug("List of executed actions is empty, returning a random action from the list of actions.");
            return getRandomActionOfType(providedListOfActions);
        }
        int i;
        final List<Action> filteredListOfActions = new ArrayList<>();

        if (UNEXECUTED == actionExecutionStatus) {
            i = 0;
        } else if (LEAST == actionExecutionStatus) {
            final boolean notExecutedAction = actions.stream()
                    .anyMatch(action -> !(executed.containsKey(action.get(ACTION_ID))));
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
                    .filter(action -> !executed.containsKey(action.get(ACTION_ID)))
                    .forEach(filteredListOfActions::add);
        } else {
            providedListOfActions.stream()
                    .filter(action -> executed.containsKey(action.get(ACTION_ID)) && executed.get(action.get(ACTION_ID)) == i)
                    .forEach(filteredListOfActions::add);
        }
        return (filteredListOfActions.size() == 0) ? null : filteredListOfActions.get(rnd.nextInt(filteredListOfActions.size()));
    }

    private Action getRandomActionOfType(List<Action> providedListOfActions) {
        if (providedListOfActions.size() != 0) {
            logger.debug("Getting a random action from the provided list.");
            return providedListOfActions.get(rnd.nextInt(providedListOfActions.size()));
        } else {
            logger.debug("Provided list is empty, returning null.");
            return null;
        }
    }

    public Action getRandomUnexecutedActionOfType(final Role actionType) {
        if (actionType == null) {
            logger.debug("ActionType is null, returning null");
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
            logger.debug("Returning the previous action");
            return previousActions.get(previousActions.size() - 1);

        } else {
            logger.debug("There are no previous actions, returning null.");
            return getRandomAction();
        }
    }

    public int getNumberOfPreviousActions() {
        logger.debug("Returning the number of previous actions: {}", previousActions.size());
        return previousActions.size();
    }


    public void updateState(final State state, final Set<Action> acts) {
        this.actions = new ArrayList<>(acts);
    }

    public void addActionToPreviousActions(final Action action) {
        logger.debug("Adding the selected action of type '" + action.get(Tags.Role) + "' to the history...");
        try {
            previousActions.add(action);
            this.incrementPreviousExecutedActions(action);
        } catch (NoSuchTagException e) {
            this.irregularActions.add(action);
            logger.debug("Irregular action, not added to the history: {}", e.getMessage());
        }

    }

    private void incrementPreviousExecutedActions(final Action previousAction) {
        final String pa = previousAction.get(ACTION_ID);
        if (executed.containsKey(pa)) {
            executed.put(pa, executed.get(pa) + 1);
        } else {
            executed.put(pa, 1);
        }
    }

    public boolean hasStateNotChanged() {
        return previousStates.size() >= 2 && previousStates.get(previousStates.size() - 1).equals(previousStates.get(previousStates.size() - 2));
    }

    int getTotalNumberOfActions() {
        return this.previousActions.size();
    }

    int getTotalNumberOfUniqueExecutedActions() {
        return this.executed.size();
    }

    void printActionWithTimeExecuted() {
        this.executed.forEach((actionId, noOfExecutions) ->
            this.previousActions.stream()
                    .filter(action -> action.get(ACTION_ID).equals(actionId))
                    .findFirst()
                    .ifPresent(action -> logger.debug("{} executed {} times", action.get(ACTION_ID), noOfExecutions))
        );
    }

    int getTotalVisitedStates() {
        return this.previousStates.size();
    }

    int getTotalNumberOfUniqueStates() {
        return (int) this.previousStates
                .stream()
                .distinct()
                .count();
    }

    public void addStateToPreviousStates(final State st) {
        previousStates.add(st.get(STATE_ID));
    }

    @Override
    public void setStateTag(final Tag<String> stateTag) {
        STATE_ID = stateTag;
    }

    private void setActions(final List<Action> actions) {
        this.actions = actions;
    }

    private void setPreviousActions(final List<Action> previousActions) {
        this.previousActions = previousActions;
    }

    private void setPreviousStates(final List<String> previousStates) {
        this.previousStates = previousStates;
    }

    private void setExecuted(final Map<String, Integer> executed) {
        this.executed = executed;
    }

    private void setIrregularActions(final List<Action> irregularActions) {
        this.irregularActions = irregularActions;
    }

    @Override
    public void clear() {
        this.setActions(new ArrayList<>());
        this.setExecuted(new TreeMap<>());
        this.setPreviousActions(new ArrayList<>());
        this.setPreviousStates(new ArrayList<>());
        this.setIrregularActions(new ArrayList<>());
        this.actionNotFound = 0;
    }

    @Override
    public int getNumberOfIrregularActions() {
        return this.irregularActions.size();
    }

    @Override
    public Action getAlternativeAction() {
        logger.debug("Could not select action, provide alternative action");
        this.updateActionNotSetList();
        return this.getRandomAction();
    }

    @Override
    public void updateActionNotSetList() {
        this.actionNotFound++;
    }

    @Override
    public int getNumberOfActionsNotFound() {
        return this.actionNotFound;
    }
}
