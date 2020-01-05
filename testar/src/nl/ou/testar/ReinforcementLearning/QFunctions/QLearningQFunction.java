package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.GuiStateGraphForQlearning;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of the Q-function specific for the Q-leaning algorithm
 */
public class QLearningQFunction implements QFunction {
    private static final double RMAX = 1d;
    private static final double GAMMA_DISCOUNT = .99d;
    private final GuiStateGraphForQlearning graph;
    private final RewardFunction rewardFunction;

    /**
     * Constructor
     * @param graph
     * @param rewardFunction
     */
    public QLearningQFunction(@Nonnull final GuiStateGraphForQlearning graph, @Nonnull final RewardFunction rewardFunction) {
        this.graph = graph;
        this.rewardFunction = rewardFunction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getQValue(AbstractState outgoingState, State incomingState, AbstractAction executedAction) {
        double oldQValue = graph.getQValue(outgoingState, executedAction);
        final Collection<Double> qValues = graph.getQvaluesForAState(outgoingState);
        final double maxQValue = Collections.max(qValues);
        double reward = rewardFunction.getReward(outgoingState, incomingState, executedAction);

        // -1 implies the state action combination has't been executed before
        if (oldQValue == -1) {
            return RMAX;
        }

        return oldQValue + (GAMMA_DISCOUNT * ((reward * maxQValue) - oldQValue));
    }
}

