package org.testar.statemodel;

import org.testar.statemodel.util.ActionHelper;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

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
