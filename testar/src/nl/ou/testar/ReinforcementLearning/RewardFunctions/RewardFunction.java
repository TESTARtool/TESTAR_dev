package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;


/**
 * Interface for reward function implementation
 */
public interface RewardFunction {

    /**
     * Get the reward for a given action
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @return The calculated reward
     */
    public double getReward(final AbstractState currentAbstractState, final AbstractAction executedAction);
}
