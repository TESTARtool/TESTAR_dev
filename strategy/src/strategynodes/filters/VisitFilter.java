package strategynodes.filters;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.VisitStatus;
import strategynodes.enums.VisitType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VisitFilter
{
    private final VisitStatus visitStatus;
    private ArrayList<Action> filteredActions;
    private MultiMap<String, Object> filteredPastActions;
    public VisitFilter(VisitStatus visitStatus)
    {
        this.visitStatus = visitStatus;
        this.filteredActions = new ArrayList<>();
        this.filteredPastActions = new MultiMap<>();
    }

    //present actions
    public ArrayList<Action> filter(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        filteredActions.clear();

        if(actions.isEmpty())
            return filteredActions;

        switch(visitStatus.getVisitType())
        {
            case MOST_VISITED: return filterMostVisited(actions, actionsExecuted);
            case LEAST_VISITED: return filterLeastVisited(actions, actionsExecuted);
            default:
                return filterPresentActions(actions, actionsExecuted);
        }
    }

    private ArrayList<Action> filterMostVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        int targetCount = 0;
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);

            //get usage count of action
            List<Object> entry = actionsExecuted.get(actionID); //should return empty collection if nonexistent
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
            String actionID = action.get(Tags.AbstractID);

            //get usage count of action
            List<Object> entry = actionsExecuted.get(actionID); //should return empty collection if nonexistent
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

    private ArrayList<Action> filterPresentActions(Collection<Action> actions, MultiMap<String, Object> actionsExecuted)
    {
        for(Action action : actions)
        { // if the action is unvisited, add to list
            if(visitStatus.actionMatchesVisitStatus(action, actionsExecuted))
                filteredActions.add(action);
        }
        return filteredActions;
    }

    //past actions
    public MultiMap<String, Object> filter(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean actionMustBeAvailable)
    {
        filteredPastActions.clear();

        if(actionsExecuted.isEmpty())
            return actionsExecuted;

        switch(visitStatus.getVisitType())
        {
            case MOST_VISITED: return filterPastMostVisited(actions, actionsExecuted, actionMustBeAvailable);
            case LEAST_VISITED: return filterPastLeastVisited(actions, actionsExecuted, actionMustBeAvailable);
            default:
                return filterPastActions(actions, actionsExecuted, actionMustBeAvailable);
        }
    }

    private MultiMap<String, Object> filterPastMostVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
    {
        int targetCount = 0;
        for(String pastActionID : actionsExecuted.keySet())
        { //only check against list if filtering is needed
            if(!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
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

    private MultiMap<String, Object> filterPastLeastVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
    {
        int targetCount = Integer.MAX_VALUE;
        for(String pastActionID : actionsExecuted.keySet())
        {//only check against list if filtering is needed
            if (!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
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


    private MultiMap<String, Object> filterPastActions(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
    {
        if (visitStatus.getVisitType() == VisitType.UNVISITED) //past actions are never unvisited
            return new MultiMap<>();

        for (String pastActionID : actionsExecuted.keySet())
        {
            if(!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
            {
                if (visitStatus.actionMatchesVisitStatus(pastActionID, actionsExecuted))
                {
                    List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
                    ArrayList<Object> copiedEntry = new ArrayList<>();
                    copiedEntry.add(entry.get(0));
                    copiedEntry.add(entry.get(1));
                    filteredPastActions.put(pastActionID, copiedEntry);
                }
            }
        }
        return filteredPastActions;
    }

    private boolean pastActionIsCurrentlyAvailable(String pastActionID, Collection<Action> actions)
    {
        for (Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);
            if(actionID.equals(pastActionID))
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return visitStatus.toString();
    }
}
