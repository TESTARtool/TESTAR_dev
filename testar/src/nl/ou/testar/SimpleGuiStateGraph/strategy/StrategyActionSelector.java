package nl.ou.testar.SimpleGuiStateGraph.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

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

    void resetGraphForNewTestSequence();

    /**
     * Print the strategy tree
     */
    public void print();
}
