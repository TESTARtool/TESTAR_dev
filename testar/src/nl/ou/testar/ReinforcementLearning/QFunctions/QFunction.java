package nl.ou.testar.ReinforcementLearning.QFunctions;


import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;

/**
 * Interface for a Q-function
 */
public interface QFunction {

    /**
     * Gets the q-value based on the reward
     * @param previousActionUnderExecution, can be null when the application is starting
     * @param actionUnderExecution, is not null
     * @param reward
     */
    float getQValue(Tag<Float> rl_tag, final AbstractAction previousActionUnderExecution, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions, final float VValue);

}
