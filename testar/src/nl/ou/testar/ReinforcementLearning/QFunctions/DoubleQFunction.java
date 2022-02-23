package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import java.util.Set;

public class DoubleQFunction implements QFunction {
    private final float alphaDiscount;
    private final float gammaDiscount;
    private final float defaultQValue;

    /**
     * Constructor
     * @param alphaDiscount
     * @param gammaDiscount
     * @param defaultQValue
     */
    public DoubleQFunction(float alphaDiscount, final float gammaDiscount, final float defaultQValue) {
        this.alphaDiscount = alphaDiscount;
        this.gammaDiscount = gammaDiscount;
        this.defaultQValue = defaultQValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions, float vValue, Tag<Float> rl_tagB) {
        float oldQValue = 0f;
        if (previouslyExecutedAction != null) {
            oldQValue = previouslyExecutedAction.getAttributes().get(rl_tag, defaultQValue);
        }
        System.out.println("OLD QVALUE of: " + Float.toString(oldQValue));

        float otherQValue = 0f;
        if (previouslyExecutedAction != null) {
            otherQValue = previouslyExecutedAction.getAttributes().get(rl_tagB, defaultQValue);
        }
        System.out.println("OTHER QVALUE of: " + Float.toString(otherQValue));

        return oldQValue + alphaDiscount * (reward + gammaDiscount * otherQValue - oldQValue);
    }
}
