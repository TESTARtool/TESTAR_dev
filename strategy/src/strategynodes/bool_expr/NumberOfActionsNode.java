package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.*;

public class NumberOfActionsNode extends BaseStrategyNode<Integer>
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;
    
    public NumberOfActionsNode(VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.VISITED_MODIFIER   = visitedModifier;
        this.FILTER             = filter;
        this.ACTION_TYPE        = actionType;
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        boolean applyFilterVisited       = (VISITED_MODIFIER != null);
        boolean applyFilterActionTypes   = (FILTER != null && ACTION_TYPE != null);
        int numberOfActions = 0;
        
        if((!applyFilterVisited) && (!applyFilterActionTypes)) //if no filtering is necessary
            return actions.size();
        
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
                        numberOfActions++;
                    else if (((count > targetCount) && VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ||
                            (VISITED_MODIFIER == VisitedModifier.MOST_VISITED) && (count < targetCount))
                    {
                        targetCount = count;
                        numberOfActions = 0;
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

                if(!actionRejected) //if the loop has made it this far, count the action
                    numberOfActions++;
            }
        }
        return numberOfActions;
    }
    
    private boolean actionAllowedByFilter(Action action)
    {
        boolean actionIsOfType = (ACTION_TYPE.actionIsThisType(action));
        
        return (
                (FILTER == Filter.INCLUDE && actionIsOfType) ||
                (FILTER == Filter.EXCLUDE && (!actionIsOfType))
        );
    }
    
    @Override
    public String toString()
    {
        String string = "n-actions";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER.toString();
        if(FILTER != null) string += " " + FILTER.toString() + " " + ACTION_TYPE.toString();
        return string;
    }
}