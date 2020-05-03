package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.apache.commons.lang.Validate;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class BoltzmannDistributedExplorationPolicy implements Policy {

    /**
     * Defines with how much the temperature declines every time the policy is used
     */
    private final double decayRate;

    /**
     * The boltzmann temperature, can not be zero
     */
    double boltzmannTemperature;

    public BoltzmannDistributedExplorationPolicy(final double decayRate, final double boltzmannTemperature) {
        this.decayRate = decayRate;
        this.boltzmannTemperature = boltzmannTemperature;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(Set<AbstractAction> actions) {
        final List<Pair<AbstractAction,Double>> probabilityOfActions = new ArrayList<>();

        try {
            // get sum of utility values for all actions
            double sumOfUtilityForAllActions = actions.stream()
                    .map(this::getActionUtility)
                    .mapToDouble(Double::doubleValue).sum();
            Validate.isTrue(sumOfUtilityForAllActions != 0.0d, "Utility or q-function for all actions results in zero");

            // get probability for every action
            actions.forEach(abstractAction -> probabilityOfActions.add(new Pair<>(abstractAction, getProbabilityForAction(abstractAction, sumOfUtilityForAllActions))));

            // select action based on probability
            AbstractAction selectedAbstractAction = selectActionBasedOnProbability(probabilityOfActions);

            // update temperature
            updateTemperature ();

            // return
            return selectedAbstractAction;
        } catch (final Exception e) {
            // fallback to random action selection
            return RandomActionSelector.selectAbstractAction(actions);
        }
    }

    private AbstractAction selectActionBasedOnProbability(List<Pair<AbstractAction, Double>> probabilityOfActions) {
        return new EnumeratedDistribution<>(probabilityOfActions).sample();
    }

    private Double getProbabilityForAction(AbstractAction abstractAction, double sumOfUtilityForAllActions) {
        return getActionUtility(abstractAction) / sumOfUtilityForAllActions;
    }

    private double getActionUtility(AbstractAction abstractAction) {
        Double qValue = abstractAction.getAttributes().get(RLTags.SarsaValue, 0d);
        Validate.isTrue(boltzmannTemperature != 0.0d, "The BoltzmannTemperature is now zero");
        return Math.exp(qValue / boltzmannTemperature);
    }

    private void  updateTemperature () {
        boltzmannTemperature = boltzmannTemperature * decayRate;
    }
}
