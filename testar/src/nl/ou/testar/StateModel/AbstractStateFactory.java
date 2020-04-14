package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.ActionHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

abstract class AbstractStateFactory {

    /**
     * This builder method will create a new abstract state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @param actions a set of Testar actions
     * @return the new abstract state
     */
    static AbstractState createAbstractState(State newState, Set<Action> actions) {
        String abstractStateId = newState.get(Tags.AbstractIDCustom);
        return new AbstractState(abstractStateId, ActionHelper.convertActionsToAbstractActions(actions));
    }

}
