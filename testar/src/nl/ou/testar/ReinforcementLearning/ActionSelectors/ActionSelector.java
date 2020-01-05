package nl.ou.testar.ReinforcementLearning.ActionSelectors;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Interface for action selector implementations
 */
public interface ActionSelector {

    /**
     * Based on a list of actions and the state selects an action to execute
     * @param state The current state
     * @param actions Set with available actions
     * @return Selected action to execute
     */
    @Nullable
    Action selectAction(@Nonnull State state, @Nonnull Set<Action> actions);
}
