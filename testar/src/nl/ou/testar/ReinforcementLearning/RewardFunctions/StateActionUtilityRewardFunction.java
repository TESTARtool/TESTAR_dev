package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of the reward function based on a counter
 */
public class StateActionUtilityRewardFunction extends CounterBasedRewardFunction{

    private static final Logger logger = LogManager.getLogger(StateActionUtilityRewardFunction.class);

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {
        if (executedAbstractAction == null || executedAbstractAction.getAttributes() == null) {
            return 0f;
        }

        float actionReward = super.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAbstractAction, actions);

        final Set<AbstractAction> abstractActions = currentAbstractState.getActions();
        float stateReward = abstractActions.stream().reduce(0, (subtotal, action) -> action.getAttributes().get(RLTags.Counter, 0) > 0 ? subtotal + 1: subtotal, Integer::sum) / (float)abstractActions.size();
        final Multimap<Integer, AbstractAction> counterActionsMultimap = ArrayListMultimap.create();
        abstractActions.forEach(action -> counterActionsMultimap.put(action.getAttributes().get(RLTags.Counter, 0), action));
        logger.info(counterActionsMultimap);
        logger.info(abstractActions.size());
        logger.info(counterActionsMultimap.keySet());
//
//        final Set<Integer> counters = counterActionsMultimap.keySet();
//        float stateReward = 0;
//        if (!counters.isEmpty()) {
//            logger.info("!counters.isEmpty() {}", counters.size());
//            stateReward = ((float)counters.stream().reduce(0, (subtotal , element) -> element >0 ? subtotal + 1: subtotal )) / abstractActions.size();
//        }

        logger.info("ID={} stateReward={}", executedAbstractAction.getId(), stateReward);

        final float reward = actionReward + stateReward;
        logger.info("ID={} Reward={}", executedAbstractAction.getId(), reward);
        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
