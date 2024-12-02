package strategynodes.filters;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;
import strategynodes.data.ActionStatus;
import strategynodes.enums.ActionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionTypeFilter
{
    private ActionTypeFilter() {} //ensure it can't be instantiated

    public static ArrayList<Action> filter(ActionStatus actionStatus, Collection<Action> actions)
    {
        ArrayList<Action> filteredActions = new ArrayList<>();
        for (Action action : actions)
        {
            if(actionStatus.getActionType().actionIsThisType(action))
                filteredActions.add(action);
        }
        return filteredActions;
    }


    //todo: test if it works
    public static MultiMap<String, Object> filter(ActionStatus actionStatus, Collection<Action> actions, boolean filterByAvailability)
    {
        ArrayList<String> availableActions = new ArrayList<>();
        if(filterByAvailability)
        {
            for (Action action : actions)
            {
                String actionID = action.get(Tags.AbstractID);
                if(StrategyManager.actionsExecutedContainsKey(actionID))
                    availableActions.add(actionID);
            }
        }

        MultiMap<String, Object> filteredPastActions = new MultiMap<>();

        for (String pastActionID : StrategyManager.getActionsExecutedIDs())
        {
            //only check against list if filtering is needed
            if (!filterByAvailability || availableActions.contains(pastActionID))
            {
                List<Object> entry = StrategyManager.getEntryCopy(pastActionID);
                ActionType entryActionType = (ActionType) entry.get(1);
                if(actionStatus.actionIsAllowed(entryActionType))
                {
                    List<Object> copiedEntry = StrategyManager.getEntryCopy(pastActionID);
                    filteredPastActions.put(pastActionID, copiedEntry);
                }
            }
        }
        return filteredPastActions;
    }
}
