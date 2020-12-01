package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtils;
import nl.ou.testar.StateModel.AbstractAction;

import java.util.Collections;
import java.util.Set;

public class EpsilonGreedyPolicy implements Policy {
    private final float epsilon; //The epsilon parameter should be between 0 and 1

    private final GreedyPolicy<?> greedyPolicy;

    public EpsilonGreedyPolicy(final GreedyPolicy<?> greedyPolicy, final float epsilon) {
        this.greedyPolicy = greedyPolicy;
        this.epsilon = epsilon;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(Set<AbstractAction> actions) {
        if (getRandomValue() < 1 - epsilon) {
            return greedyPolicy.applyPolicy(actions);
        }

        final Set<AbstractAction> actionsSelected = Collections.singleton(RandomActionSelector.selectAbstractAction(actions));
        return ReinforcementLearningUtils.selectAction(actionsSelected);
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
