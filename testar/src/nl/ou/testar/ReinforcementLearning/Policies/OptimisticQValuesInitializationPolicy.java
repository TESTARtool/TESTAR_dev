package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.util.Set;

public class OptimisticQValuesInitializationPolicy implements Policy {
    private final GreedyPolicy greedyPolicy;
    private final float maxQValue;

    public OptimisticQValuesInitializationPolicy(final GreedyPolicy greedyPolicy, final float maxQValue) {
        this.greedyPolicy = greedyPolicy;
        this.maxQValue = maxQValue;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(final Set<AbstractAction> actions) {

        // set all actions to max if no Q-value is set
        actions.forEach(abstractAction -> setDefaultValueOnNewAbstractAction(abstractAction.getAttributes()));

        // returns max Q-value
        return greedyPolicy.applyPolicy(actions);
    }

    private void setDefaultValueOnNewAbstractAction(final TaggableBase attributes) {
        try {
            attributes.get(RLTags.SarsaValue);
        } catch (final NoSuchTagException e) {
            attributes.set(RLTags.SarsaValue, maxQValue);
        }
    }
}
