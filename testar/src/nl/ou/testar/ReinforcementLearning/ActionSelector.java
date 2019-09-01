package nl.ou.testar.ReinforcementLearning;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface ActionSelector {

    /**
     * Based on a list of actions and the state selects an action to execute
     * @param state The current state
     * @param actions Set with available actions
     * @return Selected action to execute
     */
    @Nullable
    public Action selectAction(@Nonnull State state, @Nonnull Set<Action> actions);

    /**
     * Upates the q-value
     */
    public void updateQValue(@Nullable State beginState, @Nullable State endState, @Nullable Action action);
}
