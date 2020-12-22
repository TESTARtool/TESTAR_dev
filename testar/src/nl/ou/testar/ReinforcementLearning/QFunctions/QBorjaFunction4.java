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
public class QBorjaFunction4 implements QFunction {

    private final float defaultQValue;

    /**
     * Constructor
     * @param defaultQValue
     */
    public QBorjaFunction4(final float defaultQValue) {
        this.defaultQValue = defaultQValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previousActionUnderExecution, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions) {
        float oldQValue = 0f;
        if (previousActionUnderExecution != null) {
            System.out.println("previouslyExecutedAction != null ---> True");
            System.out.println("rl_tag ---> " + rl_tag.toString());
            oldQValue = previousActionUnderExecution.getAttributes().get(rl_tag, defaultQValue);
            System.out.println("oldQValue ---> " + Float.toString(oldQValue));
        }
        return oldQValue + reward;
    }
}

