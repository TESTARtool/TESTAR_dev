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

public class AnyExistNode extends BaseStrategyNode<Boolean>
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;
    
    public AnyExistNode(VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.VISITED_MODIFIER = visitedModifier;
        this.FILTER      = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        boolean applyFilterVisited       = (VISITED_MODIFIER != null);
        boolean applyFilterActionTypes   = (FILTER != null && ACTION_TYPE != null);
        
        if((!applyFilterVisited) && (!applyFilterActionTypes)) //if no filtering is necessary
            return (actions.size() > 0);

        if(VISITED_MODIFIER == VisitedModifier.LEAST_VISITED || VISITED_MODIFIER == VisitedModifier.MOST_VISITED)
        {
            boolean unvisitedActionPreferred = (VISITED_MODIFIER == VisitedModifier.LEAST_VISITED);

            int targetCount;
            if(actionsExecuted.size() == 0)
                targetCount = 0;
            else
                targetCount = (VISITED_MODIFIER == VisitedModifier.LEAST_VISITED) ?
                        Collections.min(actionsExecuted.values()) :
                        Collections.max(actionsExecuted.values());

            for(Action action : actions)
            {
                boolean actionRejected = false;
                int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);

                if(applyFilterActionTypes)
                    actionRejected = !actionAllowedByFilter(action, FILTER, ACTION_TYPE); //check if action is rejected by filter

                if(!actionRejected)
                {
                    if ((count == 0 && unvisitedActionPreferred) || count == targetCount)
                        return true;
                }
            }
            return false;
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
                    return true; //found an action that fits the parameters
            }
        }
        return false; //found no action that fits the parameters
    }
    
    @Override
    public String toString()
    {
        String string = "any-actions";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER.toString();
        if(FILTER != null) string += " " + FILTER.toString() + " " + ACTION_TYPE.toString();
        string += " exist";
        return string;
    }
}