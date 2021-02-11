package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;

/**
 * Implements the default Q-function of Sarsa
 */
public class SarsaQFunction implements QFunction {

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
        
        float newQValue = actionUnderExecution.getAttributes().get(rl_tag, defaultQValue);

        return oldQValue + alphaDiscount * (reward + gammaDiscount * newQValue - oldQValue);
    }

}


