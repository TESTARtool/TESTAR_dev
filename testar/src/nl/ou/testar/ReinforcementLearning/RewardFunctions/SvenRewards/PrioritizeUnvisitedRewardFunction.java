package nl.ou.testar.ReinforcementLearning.RewardFunctions.SvenRewards;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class PrioritizeUnvisitedRewardFunction implements RewardFunction {

    private static final Logger logger = LoggerFactory.getLogger(PrioritizeUnvisitedRewardFunction.class);

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions, AbstractState previousAbstractState) {
        if (executedAbstractAction == null) {
            return 0f;
        }

        int executionCounter = executedAbstractAction.getAttributes().get(RLTags.Counter, 0) + 1;
        executedAbstractAction.getAttributes().set(RLTags.Counter, executionCounter);

        float previousActionsCount = currentAbstractState.getActions().size();
        float unvisitedActionsCount = 0;

        for (AbstractAction action : currentAbstractState.getActions()) {
            if (action.getAttributes().get(RLTags.Counter, 0) == 0) {
                unvisitedActionsCount++;
            }
        }

        float previousStatesUnvisitedActionsCount = 0;

        for (AbstractAction action : previousAbstractState.getActions()) {
            if (action.getAttributes().get(RLTags.Counter, 0) == 0) {
                previousStatesUnvisitedActionsCount++;
            }
        }

        logger.debug("DEBUG: executionCounter={}", executionCounter);
        logger.debug("DEBUG: totalActions={}", previousActionsCount);
        logger.debug("DEBUG: actionsUnvisited={}", unvisitedActionsCount);

        float reward = 0f;
        // Penalize the executed action when there are still unvisited actions from the previous state whilst the actions has been executed multiple times
        if (previousStatesUnvisitedActionsCount > 0 && executionCounter > 1) {
            reward = -1;
        }

        if (unvisitedActionsCount != 0) {
            reward += unvisitedActionsCount / previousActionsCount;
        }
        logger.debug("DEBUG: reward={}", reward);

        return reward;
    }

    @Override
    public void reset() {
        // do nothing
    }
}