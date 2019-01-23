package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.HashSet;
import java.util.Set;

public class ActionHelper {

    /**
     * This helper method extracts the abstract ids for a set of actions
     * @param actions
     * @return
     */
    public static Set<String> getAbstractIds(Set<AbstractAction> actions) {
        Set<String> actionIds = new HashSet<>();
        for(AbstractAction action:actions) {
            actionIds.add(action.getActionId());
        }
        return actionIds;
    }

    /**
     * This helper method converts alayer actions to abstract actions for use in the state model
     * @param actions
     * @return
     */
    public static Set<AbstractAction> convertActionsToAbstractActions(Set<Action> actions) {
        Set<AbstractAction> abstractActions = new HashSet<>();
        for(Action action:actions) {
            AbstractAction abstractAction = new AbstractAction(action.get(Tags.AbstractID));
            abstractAction.addConcreteActionId(action.get(Tags.ConcreteID));
            abstractActions.add(abstractAction);
        }
        return abstractActions;
    }

}
