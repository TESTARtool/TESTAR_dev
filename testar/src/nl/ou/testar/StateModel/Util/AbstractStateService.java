package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;

import java.util.Set;

public abstract class AbstractStateService {

    /**
     * This method updates the list of actions on an abstract state.
     * @param abstractState
     * @param actions
     */
    public static void updateAbstractStateActions(AbstractState abstractState, Set<Action> actions) {
        // we only add actions to the abstract state. We do not delete.
        for (AbstractAction action : ActionHelper.convertActionsToAbstractActions(abstractState, actions)) {
            abstractState.addNewAction(action);
        }
    }

}
