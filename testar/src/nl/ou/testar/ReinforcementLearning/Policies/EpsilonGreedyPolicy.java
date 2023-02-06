package nl.ou.testar.ReinforcementLearning.Policies;

import org.testar.RandomActionSelector;
import org.testar.statemodel.AbstractAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.statemodel.AbstractState;

import java.util.Set;

public class EpsilonGreedyPolicy implements Policy {
    private static final Logger logger = LogManager.getLogger(EpsilonGreedyPolicy.class);

    protected final float epsilon; //The epsilon parameter should be between 0 and 1

    protected final GreedyPolicy greedyPolicy;

    private RandomActionSelector<AbstractState, AbstractAction> randomActionSelector = new RandomActionSelector<>();

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
    public AbstractAction applyPolicy(AbstractState state, Set<AbstractAction> actions) {
        if (getRandomValue() < 1 - epsilon) {
            return greedyPolicy.applyPolicy(state, actions);
        }

        return randomActionSelector.selectAction(state, actions);
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
