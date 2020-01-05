package nl.ou.testar.ReinforcementLearning.QFunctions;


import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.State;

import javax.annotation.Nullable;

/**
 * Interface for a Q-function
 */
public interface QFunction {

    /**
     * Gets the q-value based on the reward
     */
    double getQValue(@Nullable final AbstractState outgoingState, @Nullable final State incomingState, @Nullable final AbstractAction executedAction);

}
