package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.State;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.util.function.Consumer;

/**
 * Implementation of the reward function based on a counter
 */
public class CounterBasedRewardFunction implements RewardFunction {

    public static Consumer<WriterExperimentsParams> WRITER_EXPERIMENTS_CONSUMER = WriterExperiments::writeMetrics;

    private static final Logger logger = LogManager.getLogger(CounterBasedRewardFunction.class);

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        if (executedAction == null || executedAction.getAttributes() == null) {
            return 0f;
        }

        int executionCounter = executedAction.getAttributes().get(RLTags.Counter, 0) + 1;
        executedAction.getAttributes().set(RLTags.Counter, executionCounter);

        logger.info("ID={} executionCounter={}", executedAction.getId(), executionCounter);
        float reward = 1.0f / (float) executionCounter;
        logger.info("ID={} reward={}", executedAction.getId(), reward);

        // Write metrics information inside rlRewardMetrics.txt file to be stored in the centralized file server
        String information = String.format("ID | %s | executionCounter | %s | reward | %s ", 
                executedAction.getId(), executionCounter, reward);
//        WRITER_EXPERIMENTS_CONSUMER.accept(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
//                .setFilename("rlRewardMetrics")
//                .setInformation(information)
//                .setNewLine(true)
//                .build());

        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
