package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QLearningRewardFunction implements RewardFunction {

    @Override
    public double getReward(int executionCounter) {
        double reward = 0.0;
        if(executionCounter == 0){
            System.out.println("ERROR - calculating Q value for unvisited action should not be needed!");
        }else{
            System.out.println("DEBUG: executionCounter=" + executionCounter);
            int divider = executionCounter + 1;
            reward = 1.0/(double) divider;
            System.out.println("DEBUG: reward=" + reward);
        }
        return reward;
    }
}
