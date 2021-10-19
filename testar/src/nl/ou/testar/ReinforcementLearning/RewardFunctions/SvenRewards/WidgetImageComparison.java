package nl.ou.testar.ReinforcementLearning.RewardFunctions.SvenRewards;

import com.google.common.collect.Iterables;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.*;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.monkey.ConfigTags;

import java.util.Set;

public class WidgetImageComparison implements RewardFunction {
    private final WidgetTreeZhangShashaBasedRewardFunction widgetComparison;
    private final ImageRecognitionBasedRewardFunction imageComparison;
    private final float defaultReward;
    private final float widgetComparisonShare;

    public WidgetImageComparison(final float defaultReward, final float widgetComparisonShare) {
        this.defaultReward = defaultReward;
        this.widgetComparisonShare = widgetComparisonShare;

        widgetComparison = new WidgetTreeZhangShashaBasedRewardFunction(new LRKeyrootsHelper(), new TreeDistHelper());
        imageComparison = new ImageRecognitionBasedRewardFunction(defaultReward);
    }

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, Action executedAction, AbstractAction executedAbstractAction, AbstractAction selectedAction, Set<Action> actions) {
        float widgetReward = widgetComparison.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAction, actions);
        float pixelReward = imageComparison.getReward(state, currentConcreteState, currentAbstractState, executedAction, executedAbstractAction, selectedAction, actions);

        // Combine the two reward functions based on their share;
        return (widgetReward * widgetComparisonShare) + (pixelReward * (1f - widgetComparisonShare));
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }
}
