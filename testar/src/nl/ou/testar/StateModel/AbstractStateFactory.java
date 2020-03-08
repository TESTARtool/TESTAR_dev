package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.ActionHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

abstract class AbstractStateFactory {

    /**
     * This builder method will create a new abstract state class and populate it with the needed data
     * @param abstractStateId The abstract state id of the new abstract state
     * @param actions a set of Testar actions
     * @return the new abstract state
     */
    static AbstractState createAbstractState(String abstractStateId, Set<Action> actions) {
        return new AbstractState(abstractStateId, ActionHelper.convertActionsToAbstractActions(new AbstractState(abstractStateId, null), actions));
    }

}
