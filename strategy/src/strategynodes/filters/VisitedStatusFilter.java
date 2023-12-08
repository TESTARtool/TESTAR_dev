package strategynodes.filters;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.VisitStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VisitedStatusFilter
{
    private VisitStatus VISIT_STATUS;
    private ArrayList filteredActions;
    private MultiMap<String, Object> filteredPastActions;
    public VisitedStatusFilter(VisitStatus visitStatus)
    {
        this.VISIT_STATUS = visitStatus;
        filteredActions = new ArrayList<Action>();
        filteredPastActions = new MultiMap<>();
    }
    public ArrayList<Action> filter(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        filteredActions.clear();

        if(actions.isEmpty())
            return filteredActions;

        switch(VISIT_STATUS)
        {
            case UNVISITED: return filterUnvisited(actions, actionsExecuted);
            case MOST_VISITED: return filterMostVisited(actions, actionsExecuted);
            case LEAST_VISITED: return filterLeastVisited(actions, actionsExecuted);

//            case VISITED: return validVisitedExists(actions, actionsExecuted);
//            todo: add visited variations
        };
        return filteredActions;
    }
    public MultiMap<String, Object> filter(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByAvailability)
    {
        filteredPastActions.clear();

        if(actionsExecuted.isEmpty())
            return actionsExecuted;

        switch(VISIT_STATUS)
        {
            case UNVISITED: return filterUnvisited(actions, actionsExecuted, filterByAvailability); //all past actions are already visited
            case MOST_VISITED: return filterMostVisited(actions, actionsExecuted, filterByAvailability);
            case LEAST_VISITED: return filterLeastVisited(actions, actionsExecuted, filterByAvailability);

//            case VISITED: return validVisitedExists(actions, actionsExecuted);
//            todo: add visited variations
        };
        return filteredPastActions;
    }

    private ArrayList<Action> filterUnvisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        for(Action action : actions)
        {
            // if the action is unvisited, add to list
            if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)))
                filteredActions.add(action);
        }
        return filteredActions;
    }

    private ArrayList<Action> filterMostVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        int targetCount = 0;
        for(Action action : actions)
        {
            //get usage count of action
            List<Object> entry = actionsExecuted.get(action); //should return empty collection if nonexistent
            int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

            if (count == targetCount)
                filteredActions.add(action);
            else if (count > targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return filteredActions;
    }

    private ArrayList<Action> filterLeastVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        int targetCount = Integer.MAX_VALUE;
        for(Action action : actions)
        {
            //get usage count of action
            List<Object> entry = actionsExecuted.get(action); //should return empty collection if nonexistent
            int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

            if (count == targetCount)
                filteredActions.add(action);
            else if (count < targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return filteredActions;
    }

    private MultiMap<String, Object> filterUnvisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByAvailability)
    {
        //all past actions are already visited by default
        if(!filterByAvailability)
            return actionsExecuted;

        //if past actions need to be available now, create list of available actions
        ArrayList<String> availableActions = (filterByAvailability) ? actionsCurrentlyAvailable(actions, actionsExecuted) : new ArrayList<>();

        for(String pastActionID : actionsExecuted.keySet())
        { //only check against list if filtering is needed
            if(availableActions.contains(pastActionID))
            {
                List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
                ArrayList<Object> copiedEntry = new ArrayList<Object>();
                copiedEntry.add(entry.get(0));
                copiedEntry.add(entry.get(1));

                filteredPastActions.put(pastActionID, copiedEntry);
            }
        }
        return filteredPastActions;
    }

    private MultiMap<String, Object> filterMostVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByAvailability)
    {
        //if past actions need to be available now, create list of available actions
        ArrayList<String> availableActions = (filterByAvailability) ? actionsCurrentlyAvailable(actions, actionsExecuted) : new ArrayList<>();

        int targetCount = 0;
        for(String pastActionID : actionsExecuted.keySet())
        { //only check against list if filtering is needed
            if(!filterByAvailability || availableActions.contains(pastActionID))
            {
                //get usage count of action
                List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
                int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

                ArrayList<Object> copiedEntry = new ArrayList<Object>();
                copiedEntry.add(count);
                copiedEntry.add(entry.get(1));

                if (count == targetCount)
                    filteredPastActions.put(pastActionID, copiedEntry);
                else if (count > targetCount)
                {
                    targetCount = count;
                    filteredPastActions.clear();
                    filteredPastActions.put(pastActionID, copiedEntry);
                }
            }
        }
        return filteredPastActions;
    }

    private MultiMap<String, Object> filterLeastVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByAvailability)
    {
        //if past actions need to be available now, create list of available actions
        ArrayList<String> availableActions = (filterByAvailability) ? actionsCurrentlyAvailable(actions, actionsExecuted) : new ArrayList<>();

        int targetCount = Integer.MAX_VALUE;
        for(String pastActionID : actionsExecuted.keySet())
        {//only check against list if filtering is needed
            if (!filterByAvailability || availableActions.contains(pastActionID))
            {
                //get usage count of action
                List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
                int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

                ArrayList<Object> copiedEntry = new ArrayList<Object>();
                copiedEntry.add(count);
                copiedEntry.add(entry.get(1));

                if (count == targetCount)
                    filteredPastActions.put(pastActionID, copiedEntry);
                else if (count < targetCount)
                {
                    targetCount = count;
                    filteredPastActions.clear();
                    filteredPastActions.put(pastActionID, copiedEntry);
                }
            }
        }
        return filteredPastActions;
    }

    private ArrayList<String> actionsCurrentlyAvailable(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        ArrayList<String> actionIDsCurrentlyAvailable = new ArrayList<>();
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractIDCustom);
            if(actionsExecuted.containsKey(actionID))
                actionIDsCurrentlyAvailable.add(actionID);
        }
        return actionIDsCurrentlyAvailable;
    }

    @Override
    public String toString()
    { return VISIT_STATUS.toString(); }
}
