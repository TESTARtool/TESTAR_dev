package nl.ou.testar;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.exceptions.ActionBuildException;

import java.util.Set;

public interface ActionResolver {

    /**
     * This method is used by TESTAR to determine the set of currently available actions.
     * You can use the SUT's current state, analyze the widgets and their properties to create
     * a set of sensible actions, such as: "Click every Button which is enabled" etc.
     *
     * @param system the SUT
     * @param state the SUT's current state
     * @return  a set of actions
     */
    Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;

    /**
     * Select one of the available actions using the action selection algorithm of your choice (e.g. at random)
     *
     * @param state the SUT's current state
     * @param actions the set of derived actions
     * @return  the selected action
     */
    Action selectAction(SUT system, State state, Set<Action> actions);

    /**
     * StopCriteria for a sequence:
     *
     * TESTAR uses this method to determine when to stop the generation of actions for the
     * current sequence. You can stop deriving more actions after:
     * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
     * - after a specific time, that is set in the MaxTime setting
     *
     * @return  if <code>true</code> continue generation, else stop
     */
    boolean moreActions(State state);

    /**
     * StopCriteria for a test session:
     *
     * TESTAR uses this method to determine when to stop the entire test sequence
     * You could stop the test after:
     * - a specified amount of sequences, which is specified through the Sequences setting, or
     * - after a specific time, that is set in the MaxTime setting
     *
     * @return  if <code>true</code> continue test, else stop
     */
    boolean moreSequences();

    /**
     * Next responder in chain
     */
    ActionResolver nextResolver();
    void setNextResolver(ActionResolver nextResolver);
}