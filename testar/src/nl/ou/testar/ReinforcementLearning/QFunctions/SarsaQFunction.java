package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.annotation.Nonnull;

public class SarsaQFunction implements QFunction {

    private double rMax = 0.00;
    private double gammaDiscount = 0.00;

    public SarsaQFunction(Settings settings) {
        this.rMax = settings.get(ConfigTags.MaxReward);
        this.gammaDiscount = settings.get(ConfigTags.Discount);
        System.out.println("DEBUG: creating Q-learning action selector, R-MAX="+ rMax +", gammaDiscount="+gammaDiscount);
    }

    @Override
    public double getQValue(double qValue, double maxQvalue, double reward) {
        return 0;
    }
}
