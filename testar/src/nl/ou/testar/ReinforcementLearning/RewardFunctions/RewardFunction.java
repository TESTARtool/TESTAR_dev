package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RewardFunction {

    /**
     * Get the reward for a given action
     * @return The reward
     */
    public double getReward (int executionCounter);
}
