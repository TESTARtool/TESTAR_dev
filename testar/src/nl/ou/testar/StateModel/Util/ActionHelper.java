package nl.ou.testar.StateModel.Util;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;

import java.util.HashSet;
import java.util.Set;

public class ActionHelper {

    /**
     * This helper method extracts the abstract ids for a set of actions
     * @param actions
     * @return
     */
    public static Set<String> getAbstractIds(Set<Action> actions) {
        Set<String> actionIds = new HashSet<>();
        for(Action action:actions) {
            actionIds.add(action.get(Tags.AbstractID));
        }
        return actionIds;
    }

}
