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
    private final float defaultVValue;
    private final float betaDiscount;

    /**
     * Constructor
     * @param alphaDiscount
     * @param gammaDiscount
     * @param defaultQValue
     */
    public QVLearningFunction(float alphaDiscount, final float gammaDiscount, final float defaultQValue, final float defaultVValue, final float betaDiscount) {
        this.alphaDiscount = alphaDiscount;
        this.gammaDiscount = gammaDiscount;
        this.defaultQValue = defaultQValue;
        this.defaultVValue = defaultVValue;
        this.betaDiscount = betaDiscount;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions, Tag<Float> rl_v_tag) {
        float oldQValue = 0f;
        float oldVValue = 0f;
        if (previouslyExecutedAction != null) {
            oldQValue = previouslyExecutedAction.getAttributes().get(rl_tag, defaultQValue);
            oldVValue = previouslyExecutedAction.getAttributes().get(rl_v_tag, defaultVValue);
        }
        System.out.println("OLD QVALUE of: " + Float.toString(oldQValue));
        System.out.println("OLD VVALUE of: " + Float.toString(oldVValue));

        // Calculate VValue, V (st) := V (st) + β(rt + γV (st+1) − V (st))
        float newVValue = defaultVValue;
        if (actionUnderExecution != null) {
            newVValue = actionUnderExecution.getAttributes().get(rl_v_tag, defaultVValue);
            newVValue =  oldVValue + betaDiscount * (reward + gammaDiscount * newVValue - oldVValue);
        }


        System.out.println("NEW VVALUE of: " + Float.toString(newVValue));


        //Q(st, at) := Q(st, at) + α(rt + γV (st+1) − Q(st, at))
        return oldQValue + alphaDiscount * (reward + gammaDiscount * newVValue - oldQValue);
    }
}

