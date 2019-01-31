package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        // group the actions by the abstract action id
        Map<String, List<Action>> actionMap = actions.stream().collect(Collectors.groupingBy(a -> a.get(Tags.AbstractID)));
        // create the actions
        for (String abstractActionId : actionMap.keySet()) {
            AbstractAction abstractAction = new AbstractAction(abstractActionId);
            for (Action action : actionMap.get(abstractActionId)) {
                abstractAction.addConcreteActionId(action.get(Tags.ConcreteID));
            }
            abstractActions.add(abstractAction);
        }
        return abstractActions;
    }

}
