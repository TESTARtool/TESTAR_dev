package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AnyActionsExistNode extends BaseStrategyNode<Boolean>
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;
    
    public AnyActionsExistNode(VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.VISITED_MODIFIER = visitedModifier;
        this.FILTER      = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        boolean filterVisited       = (VISITED_MODIFIER != null);
        boolean filterActionTypes   = (FILTER != null && ACTION_TYPE != null);
        
        if((!filterVisited) && (!filterActionTypes)) //if no filtering is necessary
            return (actions.size() > 0);
        if(VISITED_MODIFIER == VisitedModifier.LEAST_VISITED || VISITED_MODIFIER == VisitedModifier.MOST_VISITED)
        {
            int targetCount = (VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ?
                              Collections.min(actionsExecuted.values()) :
                              Collections.max(actionsExecuted.values());
            
            boolean minActionFound = false;
            
            for(Action action : actions)
            {
                int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
                
                if(filterActionTypes && !actionAllowedByFilter(action, FILTER, ACTION_TYPE)) //check if action is rejected by filter
                    break; //if yes, move on to the next action
                
                if(count == 0 && VISITED_MODIFIER == VisitedModifier.LEAST_VISITED)
                    return true;
                else if(count == targetCount)
                {
                    if(VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) //unvisited actions may still be present
                        minActionFound = true;
                    else // found a most-visited action
                        return true;
                }
            }
            return minActionFound; //defaults to false for most-visited, may be true for least-visited
        }
        else // visited, unvisited, or no modifier
        {
            for(Action action : actions)
            {
                boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
                
                if(filterActionTypes && (!actionAllowedByFilter(action, FILTER, ACTION_TYPE))) //check if action is rejected by filter
                    break; //if yes, move on to the next action
                
                if(filterVisited &&
                   ((VISITED_MODIFIER == VisitedModifier.VISITED && (!actionIsVisited)) ||
                    (VISITED_MODIFIER == VisitedModifier.UNVISITED && actionIsVisited)))
                    break; //move on to the next action
                
                return true; //found an action that fits the parameters
            }
        }
        return false; //found no action that fits the parameters
    }
    
    @Override
    public String toString()
    {
        String string = " any-actions ";
        if(VISITED_MODIFIER != null) string += VISITED_MODIFIER.toString();
        if(FILTER != null) string += FILTER.toString() + " " + ACTION_TYPE.toString();
        string += " exist";
        return string;
    }
}