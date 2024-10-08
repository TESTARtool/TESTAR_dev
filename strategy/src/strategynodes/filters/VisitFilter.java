package strategynodes.filters;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;
import strategynodes.data.VisitStatus;

import java.util.ArrayList;
import java.util.Collection;

public final class VisitFilter
{
    private VisitFilter() {} //ensure it can't be instantiated

    //present actions
    public static ArrayList<Action> filterAvailableActions(VisitStatus visitStatus, Collection<Action> actions)
    {
        System.out.println("DEBUG visitfilter");
        if(!actions.isEmpty())
        System.out.println("DEBUG visitfilter actions is not empty");

        if(actions.isEmpty())
            return new ArrayList<>();

        switch(visitStatus.getVisitType())
        {
            case MOST_VISITED: return filterMostVisited(actions);
            case LEAST_VISITED: return filterLeastVisited(actions);
            default:
                return filterPresentActions(visitStatus, actions);
        }
    }

    private static ArrayList<Action> filterMostVisited(Collection<Action> actions)
    {
        ArrayList<Action> filteredActions = new ArrayList<>();
        int targetCount = 0;
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);
//
//            //get usage count of action
//            List<Object> entry = actionsExecuted.get(actionID); //should return empty collection if nonexistent
//            int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

            int count =  StrategyManager.getUsageCount(actionID);

            if (count == targetCount)
                filteredActions.add(action);
            else if (count > targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return new ArrayList<>(filteredActions);
    }

    private static ArrayList<Action> filterLeastVisited(Collection<Action> actions)
    {
        ArrayList<Action> filteredActions = new ArrayList<>();
        int targetCount = Integer.MAX_VALUE;
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);

//            //get usage count of action
//            List<Object> entry = actionsExecuted.get(actionID); //should return empty collection if nonexistent
//            int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty

            int count = StrategyManager.getUsageCount(actionID);

            if (count == targetCount)
                filteredActions.add(action);
            else if (count < targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return new ArrayList<>(filteredActions);
    }

    private static ArrayList<Action> filterPresentActions(VisitStatus visitStatus, Collection<Action> actions)
    {
        System.out.println("DEBUG visitfilter filter present actions");
        ArrayList<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        { // if the action is unvisited, add to list
            if(visitStatus.actionIsAllowed(action))
                filteredActions.add(action);
        }
        return new ArrayList<>(filteredActions);
    }

    //past actions
    public static MultiMap<String, Object> filterExecutedActions(VisitStatus visitStatus, Collection<Action> actions)
    {
        if(StrategyManager.actionsExecutedIsEmpty())
            return actionsExecuted; //if no actions have yet been executed, all current actions are allowed

        switch(visitStatus.getVisitType())
        {
            case MOST_VISITED: return filterPastMostVisited(actions, actionsExecuted, actionMustBeAvailable);
            case LEAST_VISITED: return filterPastLeastVisited(actions, actionsExecuted, actionMustBeAvailable);
            default:
                return filterPastActions(visitStatus, actions, actionsExecuted, actionMustBeAvailable);
        }
    }

//    private static MultiMap<String, Object> filterPastMostVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
//    {
//        MultiMap<String, Object> filteredPastActions = new MultiMap<>();
//        int targetCount = 0;
//        for(String pastActionID : actionsExecuted.keySet())
//        { //only check against list if filtering is needed
//            if(!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
//            {
//                //get usage count of action
//                List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
//                int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty
//
//                ArrayList<Object> copiedEntry = new ArrayList<Object>();
//                copiedEntry.add(count);
//                copiedEntry.add(entry.get(1));
//
//                if (count == targetCount)
//                    filteredPastActions.put(pastActionID, copiedEntry);
//                else if (count > targetCount)
//                {
//                    targetCount = count;
//                    filteredPastActions.clear();
//                    filteredPastActions.put(pastActionID, copiedEntry);
//                }
//            }
//        }
//        return filteredPastActions;
//    }
//
//    private static MultiMap<String, Object> filterPastLeastVisited(Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
//    {
//        MultiMap<String, Object> filteredPastActions = new MultiMap<>();
//        int targetCount = Integer.MAX_VALUE;
//        for(String pastActionID : actionsExecuted.keySet())
//        {//only check against list if filtering is needed
//            if (!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
//            {
//                //get usage count of action
//                List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
//                int count = entry.isEmpty() ? 0 : (Integer) entry.get(0); //default to zero if empty
//
//                ArrayList<Object> copiedEntry = new ArrayList<Object>();
//                copiedEntry.add(count);
//                copiedEntry.add(entry.get(1));
//
//                if (count == targetCount)
//                    filteredPastActions.put(pastActionID, copiedEntry);
//                else if (count < targetCount)
//                {
//                    targetCount = count;
//                    filteredPastActions.clear();
//                    filteredPastActions.put(pastActionID, copiedEntry);
//                }
//            }
//        }
//        return filteredPastActions;
//    }
//
//
//    private static MultiMap<String, Object> filterPastActions(VisitStatus visitStatus, Collection<Action> actions, MultiMap<String, Object> actionsExecuted, boolean filterByCurrentAvailability)
//    {
//        MultiMap<String, Object> filteredPastActions = new MultiMap<>();
//        if (visitStatus.getVisitType() == VisitType.UNVISITED) //past actions are never unvisited
//            return new MultiMap<>();
//
//        for (String pastActionID : actionsExecuted.keySet())
//        {
//            if(!filterByCurrentAvailability || pastActionIsCurrentlyAvailable(pastActionID, actions))
//            {
//                if (visitStatus.actionIsAllowed(pastActionID, actionsExecuted))
//                {
//                    List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
//                    ArrayList<Object> copiedEntry = new ArrayList<>();
//                    copiedEntry.add(entry.get(0));
//                    copiedEntry.add(entry.get(1));
//                    filteredPastActions.put(pastActionID, copiedEntry);
//                }
//            }
//        }
//        return filteredPastActions;
//    }

    private static boolean pastActionIsCurrentlyAvailable(String pastActionID, Collection<Action> actions)
    {
        for (Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);
            if(actionID.equals(pastActionID))
                return true;
        }
        return false;
    }
}
