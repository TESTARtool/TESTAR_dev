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
    Action selectAction(State state, Set<Action> actions);

    /**
     * Next responder in chain
     */
    ActionResolver nextResolver();
    void setNextResolver(ActionResolver nextResolver);
}
