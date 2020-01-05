package nl.ou.testar.ReinforcementLearning.ActionSelectors;

import com.google.common.collect.Multimap;
import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.GuiStateGraphForQlearning;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.ReinforcementLearning.Policies.QLearningPolicy;
import org.apache.commons.collections.CollectionUtils;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Implementation of an action selector for a reinforcement learning algorithm
 */
public class ReinforcementLearningActionSelector implements ActionSelector {
    private final GuiStateGraphForQlearning graph;
    // TODO implement factory
    private final static Policy policy = new QLearningPolicy();

    /**
     * Constructor
     * @param guiStateGraphForQlearning The graph used for database access
     */
    public ReinforcementLearningActionSelector(@Nonnull final GuiStateGraphForQlearning guiStateGraphForQlearning){
        graph = guiStateGraphForQlearning;
    }

    /**
     * Selects an action from a list based on a policy.
     * Returns null if the arguments state or actions is null or empty.
     * @param state The current state
     * @param actions Set with available actions
     * @return The action selected by a policy. Null if no Action can be found.
     * When multiple actions are found then a random action is returned.
     */
    @Nullable
    @Override
    public Action selectAction(@Nullable final State state, @Nullable final Set<Action> actions) {
        // validate arguments
        if (state == null || CollectionUtils.isEmpty(actions)) {
            return null;
        }

        // retrieve Q-values
        final Multimap<Double, Action> map = graph.getActionQValues(state, actions);
        final Collection<Action> actionsSelected = policy.applyPolicy(map);

        // determine return value
        if (actionsSelected.isEmpty()) {
            return null;
        } else if (actionsSelected.size() == 1) {
            return actionsSelected.iterator().next();
        } else {
            final HashSet<Action> actionSetMaxValues = new HashSet<>(actionsSelected);
            return RandomActionSelector.selectAction(actionSetMaxValues);
        }
    }
}
