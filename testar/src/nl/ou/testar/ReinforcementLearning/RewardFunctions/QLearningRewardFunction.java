package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;

public class QLearningRewardFunction implements RewardFunction {
    @Override
    public double getReward(@Nonnull State state, @Nonnull Action action) {
        return 0;
    }
}
