package org.testar.reinforcementlearning.policies;

import org.testar.RandomActionSelector;
import org.testar.statemodel.AbstractAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class EpsilonGreedyPolicy implements Policy {
    private static final Logger logger = LogManager.getLogger(EpsilonGreedyPolicy.class);

    protected final float epsilon; //The epsilon parameter should be between 0 and 1

    protected final GreedyPolicy greedyPolicy;

    public EpsilonGreedyPolicy(final GreedyPolicy greedyPolicy, final float epsilon) {
        logger.info("EpsilonGreedyPolicy initialized with epsilon='{}'", epsilon);
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

        return RandomActionSelector.selectAbstractAction(actions);
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
