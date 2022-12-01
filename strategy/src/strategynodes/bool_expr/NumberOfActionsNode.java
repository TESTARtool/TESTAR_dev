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
        boolean filterVisited       = (VISITED_MODIFIER != null);
        boolean filterActionTypes   = (FILTER != null && ACTION_TYPE != null);
        int numberOfActions = 0;
        
        if((!filterVisited) && (!filterActionTypes)) //if no filtering is necessary
            return actions.size();
        
        if(VISITED_MODIFIER == VisitedModifier.LEAST_VISITED || VISITED_MODIFIER == VisitedModifier.MOST_VISITED)
        {
            int targetCount = (VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ? Integer.MAX_VALUE : 0; //max value for least, zero for most
            
            for(Action action : actions)
            {
                int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
                
                if(filterActionTypes && !actionAllowedByFilter(action)) //check if action is rejected by filter
                    break; //if yes, move on to the next action
                
                if(count == targetCount) //both least and most
                    numberOfActions++;
                else if(((count > targetCount) && VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ||
                        (VISITED_MODIFIER == VisitedModifier.MOST_VISITED) && (count < targetCount))
                {
                    targetCount     = count;
                    numberOfActions = 0;
                }
            }
        }
        else // visited, unvisited, or no modifier
        {
            for(Action action : actions)
            {
                boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
                
                if(filterActionTypes && (!actionAllowedByFilter(action))) //check if action is rejected by filter
                    break; //if yes, move on to the next action
                
                if(filterVisited &&
                   ((VISITED_MODIFIER == VisitedModifier.VISITED && (!actionIsVisited)) ||
                    (VISITED_MODIFIER == VisitedModifier.UNVISITED && actionIsVisited)))
                    break; //move on to the next action
                
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
        string += " exist";
        return string;
    }
}