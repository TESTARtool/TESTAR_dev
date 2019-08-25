package nl.ou.testar.ReinforcementLearning;

import com.google.common.collect.Multimap;
import nl.ou.testar.RandomActionSelector;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class QLearningActionSelector implements ActionSelector {
    private double rMax;
    private double gammaDiscount;
    private GuiStateGraphForQlearning graph;

    public QLearningActionSelector(@Nonnull Settings settings, @Nonnull GuiStateGraphForQlearning graph){
        this.rMax = settings.get(ConfigTags.MaxReward);
        this.gammaDiscount = settings.get(ConfigTags.Discount);
        this.graph = graph;
        System.out.println("DEBUG: creating Q-learning action selector, R-MAX="+ rMax +", gammaDiscount="+gammaDiscount);
    }

    @Nullable
    public Action selectAction(@Nonnull State state, @Nonnull Set<Action> actions) {
        final Multimap<Double, Action> map = graph.getActionQValues(state, actions);
        final Double maxValue = Collections.max(map.keySet());
        final Collection<Action> actionsWithMaxValue = map.get(maxValue);

        if (actionsWithMaxValue.isEmpty()) {
            return null;
        } else if (actionsWithMaxValue.size() == 1) {
            return actionsWithMaxValue.iterator().next();
        } else {
            final HashSet actionSetMaxValues = new HashSet();
            actionSetMaxValues.addAll(actionsWithMaxValue);
            return RandomActionSelector.selectAction(actionSetMaxValues);
        }
    }

    @Override
    public double getReward(@Nonnull State startingState, @Nonnull State endState, @Nonnull Action action) {
        //TODO implement
        return 0;
    }

    @Override
    public double saveQValue(@Nonnull State startingState, @Nonnull State endState, @Nonnull Action action, double reward) {
        //TODO implement
        return 0;
    }
}
