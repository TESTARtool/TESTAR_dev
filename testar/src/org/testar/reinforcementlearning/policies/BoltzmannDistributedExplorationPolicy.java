package org.testar.reinforcementlearning.policies;

import org.testar.RandomActionSelector;
import org.testar.reinforcementlearning.*;
import org.testar.statemodel.AbstractAction;
import org.apache.commons.lang.Validate;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class BoltzmannDistributedExplorationPolicy implements Policy {

    /**
     * Defines with how much the temperature declines every time the policy is used
     */
    private final float decayRate;

    /**
     * The boltzmann temperature, can not be zero
     */
    float boltzmannTemperature;

    public BoltzmannDistributedExplorationPolicy(final float decayRate, final float boltzmannTemperature) {
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
                    .mapToDouble(this::getActionUtility)
                    .sum();
            Validate.isTrue(sumOfUtilityForAllActions != 0.0f, "Utility or q-function for all actions results in zero");

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

    private double getProbabilityForAction(AbstractAction abstractAction, double sumOfUtilityForAllActions) {
        return getActionUtility(abstractAction) / sumOfUtilityForAllActions;
    }

    private double getActionUtility(AbstractAction abstractAction) {
        float qValue = abstractAction.getAttributes().get(RLTags.SarsaValue, 0f);
        Validate.isTrue(boltzmannTemperature != 0.0f, "The BoltzmannTemperature is now zero");
        return Math.exp(qValue / boltzmannTemperature);
    }

    private void  updateTemperature () {
        boltzmannTemperature = boltzmannTemperature * decayRate;
    }
}
