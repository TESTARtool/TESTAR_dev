package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.StateModel.AbstractAction;
import java.util.Set;

public class EpsilonGreedyAndBoltzmannDistributedExplorationPolicy implements Policy {

    private final GreedyPolicy<?> greedyPolicy;
    private final BoltzmannDistributedExplorationPolicy boltzmannDistributedExplorationPolicy;
    private final float epsilon; //The epsilon parameter should be between 0 and 1

    public EpsilonGreedyAndBoltzmannDistributedExplorationPolicy(final GreedyPolicy<?> greedyPolicy, final BoltzmannDistributedExplorationPolicy boltzmannDistributedExplorationPolicy, final float epsilon) {
        this.greedyPolicy = greedyPolicy;
        this.boltzmannDistributedExplorationPolicy = boltzmannDistributedExplorationPolicy;
        this.epsilon = epsilon;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(Set<AbstractAction> actions) {
        if( getRandomValue() < 1 - epsilon) {
            return greedyPolicy.applyPolicy(actions);
        }

        return boltzmannDistributedExplorationPolicy.applyPolicy(actions);
    }

    float getRandomValue() {
        return (float) Math.random();
    }
}
