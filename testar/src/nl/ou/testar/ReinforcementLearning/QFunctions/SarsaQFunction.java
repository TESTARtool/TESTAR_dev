package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;
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
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions) {
        float oldQValue = 0f;
        if (previouslyExecutedAction != null) {
            oldQValue = previouslyExecutedAction.getAttributes().get(rl_tag, defaultQValue);
        }

        float newQValue = defaultQValue;
        if (actionUnderExecution != null) {
            newQValue = actionUnderExecution.getAttributes().get(rl_tag, defaultQValue);
        }
        final float qValue = oldQValue + alphaDiscount * (reward + gammaDiscount * newQValue - oldQValue);
        logger.info("For abstract action with abstractID={} q-value={} was found", previouslyExecutedAction == null? null : previouslyExecutedAction.getId(), qValue);
        return qValue;
    }
}

