package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.Multimap;
import org.fruit.alayer.Action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of a policy for Q-learning
 */
public class QLearningPolicy implements Policy {

    @Nonnull
    @Override
    public Collection<Action> applyPolicy(@Nonnull Multimap<Double, Action> actionQValues) {
        final Double maxValue = Collections.max(actionQValues.keySet());
        return actionQValues.get(maxValue);
    }
}
