package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.alayer.Action;

import javax.annotation.Nonnull;

public interface QFunction {

    /**
     * Returns the Q-value for a given action
     * @param action The action for which to get the Q value
     * @return The Q-value
     */
    public double getQvalue(@Nonnull Action action, double reward);

    /**
     * Returns the default Q-value
     * @return The default Q-value
     */
    public double getDefaultQValue();

}
