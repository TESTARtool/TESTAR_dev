package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.annotation.Nonnull;

public class QLearningQFunction implements QFunction {
    private final double rMax;
    private final double gammaDiscount;

    public QLearningQFunction(Settings settings) {
        this.rMax = settings.get(ConfigTags.MaxReward);
        this.gammaDiscount = settings.get(ConfigTags.Discount);
        System.out.println("DEBUG: creating Q-learning action selector, R-MAX=" + rMax + ", gammaDiscount="+ gammaDiscount);
    }

    @Override
    public double getQValue(double qValue, double maxQvalue, double reward) {
        return qValue + gammaDiscount * (reward * maxQvalue - qValue);
    }
}

