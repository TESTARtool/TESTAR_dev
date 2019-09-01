package nl.ou.testar.ReinforcementLearning;

import com.google.common.collect.Multimap;
import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class QLearningActionSelector implements ActionSelector {
    private final RewardFunction rewardFunction;
    private final QFunction qFunction;
    private final GuiStateGraphForQlearning graph;

    private final double GAMMA_DISCOUNT;
    private final double R_MAX;

    public QLearningActionSelector(@Nonnull Settings settings, @Nonnull RewardFunction rewardFunction, @Nonnull QFunction qFunction, GuiStateGraphForQlearning guiStateGraphForQlearning){
        this.rewardFunction = rewardFunction;
        this.qFunction = qFunction;
        graph = guiStateGraphForQlearning;
        R_MAX = settings.get(ConfigTags.MaxReward);
        GAMMA_DISCOUNT = settings.get(ConfigTags.Discount);
    }

    @Nullable
    @Override
    public Action selectAction(@Nonnull State state, @Nonnull Set<Action> actions) {
        final Multimap<Double, Action> map = graph.getActionQValues(state, actions);
        final Double maxValue = Collections.max(map.keySet());
        final Collection<Action> actionsWithMaxValue = map.get(maxValue);

        if (actionsWithMaxValue.isEmpty()) {
            return null;
        } else if (actionsWithMaxValue.size() == 1) {
            return actionsWithMaxValue.iterator().next();
        } else {
            final HashSet<Action> actionSetMaxValues = new HashSet<>(actionsWithMaxValue);
            return RandomActionSelector.selectAction(actionSetMaxValues);
        }
    }

    private double getReward(@Nullable State fromState, @Nullable State toState, @Nullable Action action) {
        int counter = graph.getExecutionCounter(fromState, toState, action);
        return rewardFunction.getReward(counter);
    }

    @Override
    public void updateQValue(@Nullable State beginState, @Nullable State endState, @Nullable Action action) {
        double reward = getReward(beginState, endState, action);
        double oldQValue = graph.getQValue(beginState, action);
        double maxQValue = graph.getMaxQvalues(action);
        double qValue = qFunction.getQValue(oldQValue, maxQValue, reward);
        graph.saveqValue(action, qValue);
    }

}
