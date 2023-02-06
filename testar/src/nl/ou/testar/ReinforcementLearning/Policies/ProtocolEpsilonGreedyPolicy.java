package nl.ou.testar.ReinforcementLearning.Policies;

import org.testar.RandomActionSelector;
import org.testar.statemodel.AbstractAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.statemodel.AbstractState;

import java.util.Set;

public class ProtocolEpsilonGreedyPolicy implements Policy {
    private static final Logger logger = LogManager.getLogger(ProtocolEpsilonGreedyPolicy.class);

    private final float epsilon; //The epsilon parameter should be between 0 and 1

    private final GreedyPolicy greedyPolicy;

    private RandomActionSelector<AbstractState, AbstractAction> randomActionSelector = new RandomActionSelector<>();

  public ProtocolEpsilonGreedyPolicy(final GreedyPolicy greedyPolicy, final float epsilon) {
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
        float randomValue = getRandomValue();
        if (randomValue < 1 - epsilon) {
            return greedyPolicy.applyPolicy(state, actions);
        }
        if (randomValue < (1 - epsilon) + 0.25 * epsilon)
            return randomActionSelector.selectAction(state, actions);
        return new AbstractAction("ProtocolAction");
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
