package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class VFunction {
    private final static float ALPHA_DISCOUNT = 1.0f;
    private static final float GAMMA_DISCOUNT = .99f;
    private static final float DEFAULT_VVALUE = 0.0f;

    final float gammaDiscount;
    final float defaultVValue;
    final float betaDiscount;

    /**
     * Constructor
     * @param settings
     */
    public VFunction(final Settings settings) {
        this.gammaDiscount = settings.get(ConfigTags.Gamma, DEFAULT_VVALUE);
        this.defaultVValue = settings.get(ConfigTags.DefaultVValue, GAMMA_DISCOUNT);
        this.betaDiscount = settings.get(ConfigTags.Beta, ALPHA_DISCOUNT);
    }

    public float getVValue(Tag<Float> rl_tag, final AbstractAction previouslyExecutedAction, final AbstractAction actionUnderExecution, final float reward) {
        float oldVValue = 0f;
        if (previouslyExecutedAction != null) {
            oldVValue = previouslyExecutedAction.getAttributes().get(rl_tag, defaultVValue);
        }
        System.out.println("OLD VVALUE of: " + Float.toString(oldVValue));

        // Calculate VValue, V (st) := V (st) + β(rt + γV (st+1) − V (st))
        float newVValue = defaultVValue;
        if (actionUnderExecution != null) {
            newVValue = actionUnderExecution.getAttributes().get(rl_tag, defaultVValue);
        }

        //Q(st, at) := Q(st, at) + α(rt + γV (st+1) − Q(st, at))
        return oldVValue + betaDiscount * (reward + gammaDiscount * newVValue - oldVValue);
    }
}