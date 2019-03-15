package nl.ou.testar.genetic.programming.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;

import java.util.List;
import java.util.Set;

public interface StrategyGuiState {
    boolean isAvailable(final Role actionType);

    int getNumberOfActions();

    int getNumberOfActions(final Role actionType);

    int getNumberOfUnexecutedActionsOfRole(final Role actionType);

    Action getRandomActionOfType(final Role actionType);

    Action getRandomActionOfTypeOtherThan(final Role actionType);

    Action getRandomAction();

    Action getRandomActionOfType(final ActionExecutionStatus actionExecutionStatus);

    Action getRandomActionOfType(final ActionExecutionStatus actionExecutionStatus, final List<Action> providedListOfActions);

    Action getRandomUnexecutedActionOfType(final Role actionType);

    Action randomLeastExecutedAction();

    Action randomMostExecutedAction();

    Action randomUnexecutedAction();

    List<Action> getActionsOfType(final Role actionType);

    Action previousAction();

    int getNumberOfPreviousActions();

    void updateState(final State state, final Set<Action> acts);

    void addActionToPreviousActions(final Action previousAction);

    boolean hasStateNotChanged();

    void addStateToPreviousStates(final State state);

    void setStateTag(final Tag<String> stateTag);

    void clear();

    void updateActionNotSetList();

    int getNumberOfActionsNotFound();

    int getNumberOfIrregularActions();

    Action getAlternativeAction();
}
