package org.testar.reinforcementlearning.policies;

import org.testar.RandomActionSelector;
import org.testar.statemodel.AbstractAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class DecayedEpsilonGreedyPolicy implements Policy {
    private static final Logger logger = LogManager.getLogger(DecayedEpsilonGreedyPolicy.class);

    private final float min_epsilon; //The epsilon parameter should be between 0 and 1

    private final float max_epsilon; //The epsilon parameter should be between 0 and 1

    private final GreedyPolicy greedyPolicy;

    private final int N;

    private int current;

    public DecayedEpsilonGreedyPolicy(final GreedyPolicy greedyPolicy, final float min_epsilon, final float max_epsilon, final int total) {
        logger.info("EpsilonGreedyPolicy initialized with epsilon='{}'", min_epsilon);
        this.greedyPolicy = greedyPolicy;
        this.min_epsilon = min_epsilon;
        this.max_epsilon = max_epsilon;
        System.out.println("min_epsilon EPSILON: " + min_epsilon);
        System.out.println("max_epsilon EPSILON: " + max_epsilon);
        this.N = total;
        this.current = 0;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(Set<AbstractAction> actions) {
        this.current++;
        float epsilon = getDecayedEpsilon();
        System.out.println("CURRENT EPSILON: " + epsilon);
        System.out.println("N CURRENT: " + current);
        if (getRandomValue() < 1 - epsilon) {
            return greedyPolicy.applyPolicy(actions);
        }

        return RandomActionSelector.selectAbstractAction(actions);
    }

    float getDecayedEpsilon(){
        return max_epsilon - ((max_epsilon - min_epsilon) * (current / (float)N) ) ;
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
