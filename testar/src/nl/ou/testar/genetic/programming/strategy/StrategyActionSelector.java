package nl.ou.testar.genetic.programming.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Verdict;

import java.util.Map;
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
    void printMetrics();

    /**
     * Get the gathered metrics of a sequence
     */
    Metric getMetrics();

    /**
     * Set tag to identify states and actions. In case no attributes are set, the state uses the ConcreteId
     * to identify states and actions are identified by Desc
     *
     * @param stateTag  - tag to identify states
     */
    void setTags(final Tag<String> stateTag);

    /**
     * Reset the metrics
     */
    void clear();

    /**
     * Print sequence execution duration
     */
    void printSequenceExecutionDuration();

    /**
     * Prepare for new sequence
     */
    void prepareForSequence();

    /**
     * Post sequence actions
     */
    void postSequence();

    /**
     * Get current sequence no
     */
    int getCurrentSequence();

    /**
     * Set Verdict
     */
    void setVerdict(final Verdict verdict);

    void postExecuteAction();

    Map<Integer, Integer> getActionMetrics();
}
