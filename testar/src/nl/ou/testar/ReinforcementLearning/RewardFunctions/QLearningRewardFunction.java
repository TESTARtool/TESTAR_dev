package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.GuiStateGraphForQlearning;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementation of the reward function for the Q-learning algorithm
 */
public class QLearningRewardFunction implements RewardFunction {

    private final GuiStateGraphForQlearning graph;

    /**
     * Contstructor
     * @param graph
     */
    public QLearningRewardFunction(@Nonnull final GuiStateGraphForQlearning graph) {
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getReward(AbstractState outgoingState, State incomingState, AbstractAction executedAction) {
        int executionCounter = graph.getExecutionCounter(outgoingState, incomingState, executedAction);
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
