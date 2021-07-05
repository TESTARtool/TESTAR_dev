package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.Iterables;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import java.util.Deque;

/**
 * This reward function is based on the article
 * "Simple Fast Algorithms for the Editing Distance Between Trees and Related Problems"
 * by Zhang AND Shasha
 * DOI: 10.1137/0218082
 */
public class WidgetTreeZhangShashaBasedRewardFunction implements RewardFunction {

    private static final Logger logger = LogManager.getLogger(WidgetTreeZhangShashaBasedRewardFunction.class);
    public static final float MIN_REWARD = 0.0001f;

    private final LRKeyrootsHelper lrKeyrootsHelper;
    private final TreeDistHelper treeDistHelper;

    static State previousState = null;

    final static MultiKeyMap treeDist = new MultiKeyMap();
    final static MultiKeyMap forestDist = new MultiKeyMap();

    public WidgetTreeZhangShashaBasedRewardFunction(final LRKeyrootsHelper lrKeyrootsHelper, final TreeDistHelper treeDistHelper) {
        this.lrKeyrootsHelper = lrKeyrootsHelper;
        this.treeDistHelper = treeDistHelper;
    }

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        if (state == null) {
            return 0f;
        }

        if (previousState == null) {
            previousState = state;
            logger.info("Default reward for previous state:{} and current state {} is {}", previousState, state, 0f);
            return 0f;
        }

        final Deque<Widget> lrKeyroots1 = lrKeyrootsHelper.getLRKeyroots(previousState);
        final Deque<Widget> lrKeyroots2 = lrKeyrootsHelper.getLRKeyroots(state);

        for (final Widget keyRoot1 : lrKeyroots1) {
            for (final Widget keyRoot2 : lrKeyroots2) {
                treeDistHelper.treeDist(keyRoot1, keyRoot2, forestDist, treeDist);
                forestDist.clear();
            }
        }

        int reward = (int) treeDist.get(previousState, state);

        /**
         * For debugging purposes
         */
        // State (process Widget) has normally only 1 child =  windows container
        logger.info("Number widgets Previous State='{}'", previousState.childCount() > 0 ? Iterables.size(previousState) : 0);
        logger.info("Number widgets Current State='{}'", state.childCount() > 0 ? Iterables.size(state) : 0);
        
        // State is basically the process Widget, think that getAbstractRepresentation is not informative
        logger.info("State='{}'", state.getAbstractRepresentation());

        // First child of the state is normally the windows container, not really informative
        logger.info("First child of previous state='{}'", previousState.child(0).getAbstractRepresentation());

        // This is returning the representation of the State = Widget process, not really informative
        logger.info("Reward for previous state:'{}' and current state '{}' is '{}'", previousState.getAbstractRepresentation(), state.getAbstractRepresentation(), reward);
        logger.info("Reward for Action Transition from Previous State to Current State is '{}'", reward);

        previousState = state;
        treeDist.clear();

        logger.info("ID={} reward={}", executedAction.getId(), reward);
        return reward;
    }

    @Override
    public void reset() {
        previousState = null;
        treeDist.clear();
        logger.info("WidgetTreeZhangShashaBasedRewardFunction was reset");
    }
}
