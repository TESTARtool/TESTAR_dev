package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the reward function based on a counter
 */
public class CounterBasedRewardFunction implements RewardFunction {

    private static final Logger logger = LoggerFactory.getLogger(CounterBasedRewardFunction.class);

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        int executionCounter = executedAction.getAttributes().get(RLTags.Counter, 0) + 1;
        executedAction.getAttributes().set(RLTags.Counter, executionCounter);

        logger.debug("DEBUG: executionCounter={}", executionCounter);
        float reward = 1.0f / (float) executionCounter;
        logger.debug("DEBUG: reward={}", reward);

        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
