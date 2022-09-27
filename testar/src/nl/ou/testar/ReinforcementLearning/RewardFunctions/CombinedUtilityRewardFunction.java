package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.Helpers.CompareScreenshotsByPixelsHelper;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

/**
 * Implementation of the reward function based on a counter
 */
public class CombinedUtilityRewardFunction extends StateActionUtilityRewardFunction{

    private static final Logger logger = LogManager.getLogger(CombinedUtilityRewardFunction.class);
    private CompareScreenshotsByPixelsRewardFunction pixelComparer;

    public CombinedUtilityRewardFunction(){
        pixelComparer = new CompareScreenshotsByPixelsRewardFunction(new CompareScreenshotsByPixelsHelper());
    }

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {
        if (executedAbstractAction == null || executedAbstractAction.getAttributes() == null) {
            return 0f;
        }

        float utilityActionReward = super.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAbstractAction, actions);

        float pixelReward = pixelComparer.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAbstractAction, actions);

        logger.info("ID={} pixelReward={}", executedAbstractAction.getId(), pixelReward);

        float stateChangeWeight = 1f;
        final float reward = utilityActionReward + stateChangeWeight * pixelReward;
        logger.info("ID={} CombinedReward={}", executedAbstractAction.getId(), reward);
        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
