package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SelectRandomActionNode extends BaseActionNode
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;

    public SelectRandomActionNode(Integer weight, VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.WEIGHT             = (weight != null || weight > 0) ? weight : 1;
        this.VISITED_MODIFIER   = visitedModifier;
        this.FILTER             = filter;
        this.ACTION_TYPE        = actionType;
    }

    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        boolean applyFilterVisited       = (VISITED_MODIFIER != null);
        boolean applyFilterActionTypes   = (FILTER != null && ACTION_TYPE != null);

        if((!applyFilterVisited) && (!applyFilterActionTypes)) //if no filtering is necessary
            return selectRandomAction(actions);

        ArrayList filteredActions = new ArrayList();

        if(VISITED_MODIFIER == VisitedModifier.LEAST_VISITED || VISITED_MODIFIER == VisitedModifier.MOST_VISITED)
        {
            int targetCount = (VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ? Integer.MAX_VALUE : 0; //max value for least, zero for most

            for(Action action : actions)
            {
                boolean actionRejected = false;
                int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);

                if (applyFilterActionTypes)
                    actionRejected = !actionAllowedByFilter(action, FILTER, ACTION_TYPE); //check if action is rejected by filter

                if (!actionRejected)
                {
                    if (count == targetCount) //both least and most
                        filteredActions.add(action);
                    else if (((count > targetCount) && VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ||
                            (VISITED_MODIFIER == VisitedModifier.MOST_VISITED) && (count < targetCount))
                    {
                        targetCount = count;
                        filteredActions.clear();
                    }
                }
            }
        }
        else // visited, unvisited, or no modifier
        {
            for(Action action : actions)
            {
                boolean actionRejected = false;

                if (applyFilterVisited)
                    actionRejected = !actionMatchesVisitorModifier(action, VISITED_MODIFIER, actionsExecuted);

                if(!actionRejected && applyFilterActionTypes)
                    actionRejected = !actionAllowedByFilter(action, FILTER, ACTION_TYPE); //check if action is rejected by filter

                if(!actionRejected)
                    filteredActions.add(action); //if the loop has made it this far, add the action to the list
            }
        }
        if(filteredActions.size() == 0) //if nothing has made it through the filter
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(actions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public String toString()
    {
        String string = WEIGHT + " select-random";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER.toString();
        if(FILTER != null) string += " " + FILTER.toString() + " " + ACTION_TYPE.toString();
        return string;
    }
}