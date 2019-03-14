package nl.ou.testar.genetic.programming.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;

import java.util.List;
import java.util.Set;

public interface StrategyActionSelector {

    /**
     * Select the next action to execute
     *
     * @param state   - current state
     * @param actions - actions
     * @return action to execute
     */
    Action selectAction(final State state, final Set<Action> actions);

    /**
     * Print the strategy tree
     */
    void print();

    /**
     * Print the gathered metrics
     */
    void getMetrics();

    /**
     * Set tag to identify states and actions. In case no attributes are set, the state uses the ConcreteId
     * to identify states and actions are identified by Desc
     * @param stateTag - tag to identify states
     * @param actionTag - tag to identify actions
     */
    void setTags(final Tag<String> stateTag, final Tag<String> actionTag);

    /**
     * Reset the metrics
     */
    void clear();

    /**
     * Save the gathered metrics to a file
     */
    void saveMetrics();
}
