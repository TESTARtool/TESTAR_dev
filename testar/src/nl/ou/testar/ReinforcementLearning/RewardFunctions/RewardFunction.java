package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;

public interface RewardFunction {

    /**
     * Get the reward for a given action
     * @param action The action for which to get the Q-value
     * @return The reward
     */
    public double getReward (@Nonnull State state, @Nonnull Action action);
}
