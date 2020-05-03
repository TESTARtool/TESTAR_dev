package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

import java.util.HashSet;
import java.util.Set;

public class WidgetTreeBasedRewardFunction implements RewardFunction {

    Set<String> actionsInPreviousState = new HashSet<>();

    /**
     * Calculates the reward by dividing the number of new actions in a state by the total number of actions
     *
     *
     * @param currentAbstractState
     * @param executedAction
     * @return The reward
     */
    @Override
    public double getReward(final AbstractState currentAbstractState, final AbstractAction executedAction) {
        final Set<String> actionsInCurrentState = currentAbstractState.getActionIds();
        Long count = actionsInCurrentState.stream()
                .filter(abstractAction -> !actionsInPreviousState.contains(abstractAction))
                .count();
        actionsInPreviousState = actionsInCurrentState;
        return count.doubleValue() / actionsInCurrentState.size();
    }
}
