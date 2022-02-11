package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements the default Q-function of Sarsa
 */
public class SarsaQFunction implements QFunction {

    private static final Logger logger = LogManager.getLogger(SarsaQFunction.class);

    private final float alphaDiscount;
    private final float gammaDiscount;
    private final float defaultQValue;

    /**
     * Constructor
     * @param alphaDiscount
     * @param gammaDiscount
     * @param defaultQValue
     */
    public SarsaQFunction(float alphaDiscount, final float gammaDiscount, final float defaultQValue) {
        logger.info("SarsaQFunction initialized with alpha='{} gamma='{}' and defaultQValue='{}'", alphaDiscount, gammaDiscount, defaultQValue);
        this.alphaDiscount = alphaDiscount;
        this.gammaDiscount = gammaDiscount;
        this.defaultQValue = defaultQValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final float reward) {
        float oldQValue = 0f;
        if (previouslyExecutedAction != null) {
            oldQValue = previouslyExecutedAction.getAttributes().get(RLTags.SarsaValue, defaultQValue);
        }
        float newQValue = defaultQValue;
        if (actionUnderExecution != null) {
            newQValue = actionUnderExecution.getAttributes().get(RLTags.SarsaValue, defaultQValue);
        }
        return oldQValue + alphaDiscount * (reward + gammaDiscount * newQValue - oldQValue);
    }
}

