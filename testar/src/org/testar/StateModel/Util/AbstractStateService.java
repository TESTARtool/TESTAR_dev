package org.testar.statemodel.util;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.monkey.alayer.Action;

import java.util.Set;

public abstract class AbstractStateService {

    /**
     * This method updates the list of actions on an abstract state.
     * @param abstractState
     * @param actions
     */
    public static void updateAbstractStateActions(AbstractState abstractState, Set<Action> actions) {
        // we only add actions to the abstract state. We do not delete.
        for (AbstractAction action : ActionHelper.convertActionsToAbstractActions(actions)) {
            abstractState.addNewAction(action);
        }
    }

}
