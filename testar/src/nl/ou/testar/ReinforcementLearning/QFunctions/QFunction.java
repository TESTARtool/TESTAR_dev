package nl.ou.testar.ReinforcementLearning.QFunctions;


import nl.ou.testar.StateModel.AbstractAction;

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
    float getQValue(final AbstractAction previousActionUnderExecution, final AbstractAction actionUnderExecution, final float reward);
}
