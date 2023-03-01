package org.testar.reinforcementlearning.rewardfunctions;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;


/**
 * Interface for reward function implementation
 */
public interface RewardFunction {

    /**
     * Get the reward for a given action
     *
     *
     * @param state
     * @param currentConcreteState The {@link ConcreteState} the SUT is in
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @param actions
     * @return The calculated reward
     */
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions);

    /**
     * Resets the reward function
     */
    public void reset();
}
