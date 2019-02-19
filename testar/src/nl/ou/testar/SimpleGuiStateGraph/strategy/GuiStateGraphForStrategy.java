package nl.ou.testar.SimpleGuiStateGraph.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.Optional;
import java.util.Set;

public interface GuiStateGraphForStrategy {

    /**
     * Resetting the last action and last state to null for a new test sequence
     */
    void startANewTestSequence();

    /**
     * Get an action by action id
     *
     * @param actions          - Get specific action by action identifier
     * @param concreteActionId - Concrete action identifier
     * @return Optional - Optional containing an Action or empty
     */
    Optional<Action> getActionWithAbstractId(Set<Action> actions, String concreteActionId);

    /**
     * Get the a state by concrete id
     *
     * @param concreteStateId - Concrete state identifier
     * @return Optional containing a StrategyGuiStateImpl or empty
     */
    Optional<StrategyGuiStateImpl> getStateByAbstractId(String concreteStateId);

    /**
     * Create new GUI state
     *
     * @param state   - The new state
     * @param actions - Actions available
     * @return Optional - newly created GUI state
     */
    StrategyGuiStateImpl createStrategyGuiState(final State state, Set<Action> actions);

    Set<StrategyGuiStateImpl> getStrategyGuiStates();

    Optional<String> getStartingStateAbstractId();

    void setStartingStateAbstractId(final String startingStateAbstractId);

    String getPreviousStateAbstractId();

    void setPreviousStateAbstractId(String previousStateConcreteId);

    String getPreviousActionAbstractId();

    void setPreviousActionAbstractId(String previousActionConcreteId);
}
