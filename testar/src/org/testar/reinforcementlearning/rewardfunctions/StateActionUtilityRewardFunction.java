package org.testar.reinforcementlearning.rewardfunctions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.testar.reinforcementlearning.RLTags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.util.Set;

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
        float stateReward = abstractActions.stream().reduce(0, (subtotal, action) ->
        {
            if(action.getAttributes().get(RLTags.ExCounter, 0) > 0){
                logger.info("Visited action: " + action.getAttributes().get(Tags.Desc, ""));
            }
            return action.getAttributes().get(RLTags.ExCounter, 0) > 0 ? subtotal: subtotal + 1;
        }, Integer::sum) / (float)abstractActions.size();
        final Multimap<Integer, AbstractAction> counterActionsMultimap = ArrayListMultimap.create();
        abstractActions.forEach(action -> counterActionsMultimap.put(action.getAttributes().get(RLTags.ExCounter, 0), action));
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

//        float counterWeight = 0.4f;
//        float stateWeight = 0.3f;
//        final float reward = counterWeight * actionReward + stateWeight * stateReward;
        final float reward = actionReward + stateReward;
        logger.info("ID={} Reward={}", executedAbstractAction.getId(), reward);
        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
