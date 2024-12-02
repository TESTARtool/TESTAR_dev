package strategynodes.filters;

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
    public static ArrayList<Action> filter(VisitStatus visitStatus, Collection<Action> actions)
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
