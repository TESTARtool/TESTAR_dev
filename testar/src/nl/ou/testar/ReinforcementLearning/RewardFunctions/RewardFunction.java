package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;


/**
 * Interface for reward function implementation
 */
public interface RewardFunction {

    /**
     * Get the reward for a given action
     * @return The reward
     */
    public double getReward(final AbstractState currentAbstractState, final AbstractAction executedAction);
}
