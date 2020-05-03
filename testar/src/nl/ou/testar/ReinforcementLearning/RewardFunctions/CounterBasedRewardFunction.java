package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

/**
 * Implementation of the reward function based on a counter
 */
public class CounterBasedRewardFunction implements RewardFunction {

    static final double DEFAULT_REWARD = 0d;

    /**
     * {@inheritDoc}
     */
    @Override
    public double getReward(final AbstractState currentAbstractState, final AbstractAction executedAction) {
        int executionCounter = executedAction.getAttributes().get(RLTags.counter, 0) + 1;
        executedAction.getAttributes().set(RLTags.counter, executionCounter);
        double reward = DEFAULT_REWARD;

        System.out.println("DEBUG: executionCounter= " + executionCounter);
        reward = 1.0 / (double) executionCounter;
        System.out.println("DEBUG: reward= " + reward);

        return reward;
    }

}
