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
        List<Action> filteredActions = new ArrayList(actions);
        
        if(VISITED_MODIFIER != null)
            filteredActions = filterByVisitedModifier(VISITED_MODIFIER, filteredActions, actionsExecuted);
        
        if(FILTER != null && ACTION_TYPE != null)
            filteredActions = filterByActionType(filteredActions, FILTER, ACTION_TYPE);
        
        return filteredActions.size();
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