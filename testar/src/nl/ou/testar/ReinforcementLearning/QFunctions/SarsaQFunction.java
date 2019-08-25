package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.alayer.Action;

import javax.annotation.Nonnull;

public class SarsaQFunction implements QFunction {

    @Override
    public double getQvalue(@Nonnull Action action, double reward) {
        return 0;
    }

    @Override
    public double getDefaultQValue() {
        return 0;
    }
}
