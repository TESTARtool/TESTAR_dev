package nl.ou.testar.ReinforcementLearning.RewardFunctions.SvenRewards;

import com.google.common.collect.Iterables;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

public class WidgetPixelComparison implements RewardFunction {
    private final WidgetComparison widgetComparison = new WidgetComparison();
    private final PixelComparison pixelComparison = new PixelComparison();

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, Action executedAction, AbstractAction executedAbstractAction, AbstractAction selectedAction, Set<Action> actions) {
        float widgetReward = widgetComparison.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAction, actions);
        float pixelReward = pixelComparison.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAction, actions);

        // Combine the two reward functions;
        return (widgetReward + pixelReward) / 2f;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }
}
