package nl.ou.testar.ReinforcementLearning;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ActionSelector {

    /**
     * Based on a list of actions and the state selects an action to execute
     * @param state The current state
     * @param actions Set with available actions
     * @return Selected action to execute
     */
    @Nonnull
    public Action selectAction(@Nonnull State state, @Nonnull Set<Action> actions);

    /**
     * Determine the reward for the current state action combination
     * @return the reward, the default is 0
     */
    public double getReward(@Nonnull State startingState, @Nonnull State endState, @Nonnull Action action);

    /**
     * Gets the q-value based on the reward, the default is 0
     */
    public double saveQValue(@Nonnull State startingState, @Nonnull State endState, @Nonnull Action action, double reward);
}
