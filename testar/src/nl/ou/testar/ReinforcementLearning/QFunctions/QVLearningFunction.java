package nl.ou.testar.ReinforcementLearning.QFunctions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Collections;
import java.util.Set;

/**
 * Implements the default Q-function of QLearning
 */
public class QVLearningFunction implements QFunction {

    private final float alphaDiscount;
    private final float gammaDiscount;
    private final float defaultQValue;

    /**
     * Constructor
     * @param alphaDiscount
     * @param gammaDiscount
     * @param defaultQValue
     */
    public QVLearningFunction(float alphaDiscount, final float gammaDiscount, final float defaultQValue) {
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

        //Q(st, at) := Q(st, at) + α(rt + γV (st+1) − Q(st, at))
        return oldQValue + alphaDiscount * (reward + gammaDiscount * vValue - oldQValue);
    }
}

