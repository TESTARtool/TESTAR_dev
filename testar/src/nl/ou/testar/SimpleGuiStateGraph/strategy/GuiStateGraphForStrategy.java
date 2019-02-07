package nl.ou.testar.SimpleGuiStateGraph.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;

import java.util.List;
import java.util.Optional;
import java.util.Set;

interface GuiStateGraphForStrategy {

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
    Optional<Action> getActionWithConcreteId(Set<Action> actions, String concreteActionId);

    /**
     * Get the a state by concrete id
     *
     * @param concreteStateId - Concrete state identifier
     * @return Optional containing a StrategyGuiState or empty
     */
    Optional<StrategyGuiState> getStateByConcreteId(String concreteStateId);

    /**
     * Create new GUI state
     *
     * @param state   - The new state
     * @param actions - Actions available
     * @return Optional - newly created GUI state
     */
    StrategyGuiState createStrategyGuiState(final State state, Set<Action> actions);

    Set<StrategyGuiState> getStrategyGuiStates();

    String getStartingStateConcreteId();

    void setStartingStateConcreteId(final String startingStateConcreteId);

    String getPreviousStateConcreteId();

    void setPreviousStateConcreteId(String previousStateConcreteId);

    String getPreviousActionConcreteId();

    void setPreviousActionConcreteId(String previousActionConcreteId);

    boolean isAvailable(Role actiontype);

    int getNumberOfActions();

    int getNumberOfActions(Role actiontype);

    Action getRandomAction(Role actiontype);

    Action getRandomAction();

    Action getRandomActionOfTypeOtherThan(Role actiontype);

    Action getRandomAction(String status);

    Action getRandomAction(String status, List<Action> providedListofActions);

    Action getRandomAction(Role actiontype, String status);

    List<Action> getActionsOfType(Role actiontype);

    Action previousAction();

    int getNumberOfPreviousActions();

    void setState(IEnvironment env, State state, Set<Action> acts);

    void setPreviousAction(Action previousAction);

    boolean hasStateNotChanged();

    void setPreviousState(State st);
}
