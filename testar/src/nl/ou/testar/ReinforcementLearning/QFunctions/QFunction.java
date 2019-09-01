package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;

public interface QFunction {

    /**
     * Gets the q-value based on the reward, the default is 0
     */
    public double getQValue(double qValue, double maxQvalue, double reward);

}
