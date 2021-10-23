package nl.ou.testar.ReinforcementLearning.RewardFunctions.SvenRewards;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.CounterBasedRewardFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class VisitedUnvisitedRewardFunction implements RewardFunction {

    private static final Logger logger = LoggerFactory.getLogger(VisitedUnvisitedRewardFunction.class);

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {
        if (executedAbstractAction == null) {
            return 0f;
        }

        int executionCounter = executedAbstractAction.getAttributes().get(RLTags.Counter, 0) + 1;
        executedAbstractAction.getAttributes().set(RLTags.Counter, executionCounter);

        float actionsCount = currentAbstractState.getActions().size();
        float unvisitedActionsCount = 0;


        for (AbstractAction action : currentAbstractState.getActions()) {
            if (action.getAttributes().get(RLTags.Counter, 0) == 0) {
                unvisitedActionsCount++;
            }
        }

        logger.debug("DEBUG: executionCounter={}", executionCounter);
        logger.debug("DEBUG: totalActions={}", actionsCount);
        logger.debug("DEBUG: actionsUnvisited={}", unvisitedActionsCount);

        // Give a negative reward if all actions have already been executed, so that the state does not need to be visited anymore.
        float reward = -0.5f;

        if (unvisitedActionsCount != 0) {
            reward = unvisitedActionsCount / actionsCount;
        }
        logger.debug("DEBUG: reward={}", reward);

        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }
}