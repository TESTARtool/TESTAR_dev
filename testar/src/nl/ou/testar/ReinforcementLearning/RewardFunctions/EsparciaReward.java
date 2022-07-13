package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of the reward function based on a counter
 */
public class EsparciaReward implements RewardFunction {

    public static Consumer<WriterExperimentsParams> WRITER_EXPERIMENTS_CONSUMER = WriterExperiments::writeMetrics;

    private static final Logger logger = LogManager.getLogger(CounterBasedRewardFunction.class);

    private final float R_max;

    public EsparciaReward(float R_max){
        this.R_max = R_max;
    }

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {

        if (executedAbstractAction == null || executedAbstractAction.getAttributes() == null) {
            return 0f;
        }

        int executionCounter = executedAbstractAction.getAttributes().get(RLTags.ExCounter, 0);
        executedAbstractAction.getAttributes().set(RLTags.ExCounter, executionCounter + 1);
        logger.info("ID={} executionCounter={}", executedAbstractAction.getId(), executedAbstractAction.getAttributes().get(RLTags.ExCounter, -1));
        logger.info("ID={} executionCounter2={}", executedAbstractAction.getId(), executionCounter);
        float reward = executionCounter != 0 ? 1.0f / (float) executionCounter : R_max;
        logger.info("ID={} reward={}", executedAbstractAction.getId(), reward);

        // Write metrics information inside rlRewardMetrics.txt file to be stored in the centralized file server
        String information = String.format("ID | %s | executionCounter | %s | reward | %s ",
                executedAbstractAction.getId(), executionCounter, reward);
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
