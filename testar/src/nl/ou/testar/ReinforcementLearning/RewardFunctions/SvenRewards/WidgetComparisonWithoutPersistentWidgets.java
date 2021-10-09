package nl.ou.testar.ReinforcementLearning.RewardFunctions.SvenRewards;

import com.google.common.collect.Iterables;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Set;

public class WidgetComparisonWithoutPersistentWidgets implements RewardFunction {
    private State previousState = null;
    //private Set<Action> previousActions;

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, Action executedAction, AbstractAction executedAbstractAction, AbstractAction selectedAction, Set<Action> actions) {
        System.out.println(". . . . . Enfoque 3 . . . . .");
        float reward = 0f;

        if(previousState == null) {
            previousState = state;
            return reward;
        }

        // Consider all widgets (widget tree)
        int numWidgetsNow = getWidgetsNum(state);
        int numWidgetsBefore = getWidgetsNum(previousState);

        System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
        System.out.println("*** numWidgetsNow: " + numWidgetsNow);

        // Widget Tree difference reward
        if (numWidgetsBefore < numWidgetsNow) {
            reward = (float) numWidgetsBefore/numWidgetsNow;
        } else {
            reward = (float) numWidgetsNow/numWidgetsBefore;
        }

        // Also decrement reward based on Widget Tree ZIndex

        System.out.println(". . . . . Provisional Reward: " + reward);

        if (executedAction != null) {
            reward -= (0.01 * executedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
        } else {
            System.out.println("WARNING: It was not possible to get the OriginWidget");
        }

        previousState = state;

        return reward;
    }

    private int getWidgetsNum(State state) {
        if(state == null)
            return 0;
        return Iterables.size(state);
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }
}
