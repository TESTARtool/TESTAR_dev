package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.State;

/**
 * Implementation of the reward function based on a counter
 */
public class CounterBasedRewardFunction implements RewardFunction {

    private static final Logger logger = LogManager.getLogger(CounterBasedRewardFunction.class);

    private final float defaultReward;

    public CounterBasedRewardFunction(float defaultReward) {
        logger.info("CounterBasedRewardFunction initialised with defaultReward={}", defaultReward);
        this.defaultReward = defaultReward;
    }

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        if (executedAction == null || executedAction.getAttributes() == null) {
            return defaultReward;
        }

        int executionCounter = executedAction.getAttributes().get(RLTags.Counter, 0) + 1;
        executedAction.getAttributes().set(RLTags.Counter, executionCounter);

        logger.info("ID={} executionCounter={}", executedAction.getId(), executionCounter);
        float reward = 1.0f / (float) executionCounter;
        logger.info("ID={} reward={}", executedAction.getId(), reward);

        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
