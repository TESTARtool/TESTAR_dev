package strategynodes.filters;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;

import java.util.*;

public class ActionTypeFilter
{
    private final boolean include;
    private final ActionType ACTION_TYPE;
    private ArrayList<Action> filteredActions;
    private MultiMap<String, Object> filteredPastActions;
    public ActionTypeFilter(Filter filter, ActionType actionType)
    {
        include = (filter == Filter.INCLUDE);
        this.ACTION_TYPE = actionType;
        filteredActions = new ArrayList<Action>();
        filteredPastActions = new MultiMap<>();
    }

    public ArrayList<Action> filter(Collection<Action> actions)
    {
        filteredActions.clear();
        for (Action action : actions)
        {
            if (include == ACTION_TYPE.actionIsThisType(action))
                filteredActions.add(action);
        }
        return filteredActions;
    }

    //todo: test if it works
    public MultiMap<String, Object> filter(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByAvailability)
    {
        filteredPastActions.clear();

        ArrayList<String> availableActions = new ArrayList<>();
        if(filterByAvailability)
        {
            for (Action action : actions)
            {
                String actionID = action.get(Tags.AbstractID);
                if (actionsExecuted.containsKey(actionID))
                    availableActions.add(actionID);
            }
        }

        for (String pastActionID : actionsExecuted.keySet())
        {
            //only check against list if filtering is needed
            if (!filterByAvailability || availableActions.contains(pastActionID))
            {
                List<Object> entry = actionsExecuted.get(pastActionID);
                ActionType actionType = (ActionType) entry.get(1);
                if (include == (ACTION_TYPE == actionType))
                {
                    ArrayList<Object> copiedEntry = new ArrayList<Object>();
                    copiedEntry.add(entry.get(0));
                    copiedEntry.add(actionType);
                    filteredPastActions.put(pastActionID, copiedEntry);
                }
            }
        }
        return filteredPastActions;
    }
}
