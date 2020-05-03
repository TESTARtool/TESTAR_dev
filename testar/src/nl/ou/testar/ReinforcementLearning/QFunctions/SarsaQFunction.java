package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetTreeBasedRewardFunction;
import nl.ou.testar.StateModel.AbstractAction;

/**
 * Implements the default Q-function of Sarsa
 */
public class SarsaQFunction implements QFunction {

    private final double alphaDiscount;
    private final double gammaDiscount;
    private final double defaultQValue;

    /**
     * Constructor
     * @param alphaDiscount
     * @param gammaDiscount
     * @param defaultQValue
     */
    public SarsaQFunction(double alphaDiscount, final double gammaDiscount, final double defaultQValue) {
        this.alphaDiscount = alphaDiscount;
        this.gammaDiscount = gammaDiscount;
        this.defaultQValue = defaultQValue;
    }
/**
     * {@inheritDoc}
     */
    @Override
    public double getQValue(final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final double reward) {
        double oldQValue = 0.0d;
        if (previouslyExecutedAction != null) {
            oldQValue = previouslyExecutedAction.getAttributes().get(RLTags.SarsaValue, defaultQValue);
        }
        double newQValue = actionUnderExecution.getAttributes().get(RLTags.SarsaValue, defaultQValue);

        return oldQValue + alphaDiscount * (reward + gammaDiscount * (newQValue) - oldQValue);
    }
}

