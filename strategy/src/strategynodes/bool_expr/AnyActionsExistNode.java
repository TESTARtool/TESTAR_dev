package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.Map;
import java.util.Set;

public class AnyActionsExistNode extends BaseStrategyNode<Boolean>
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTIONTYPE;
    
    public AnyActionsExistNode(VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.VISITED_MODIFIER = visitedModifier;
        this.FILTER           = filter;
        this.ACTIONTYPE       = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        boolean available = false;
        for(Action action : actions)
        {
            if(ACTIONTYPE.actionIsThisType(action))
            {
                available = true;
                break;
            }
        }
        return available;
    }
    
    @Override
    public String toString()
    {
        String string = " any-actions ";
        if(VISITED_MODIFIER != null) string += VISITED_MODIFIER.toString();
        if(FILTER != null) string += FILTER.toString() + " " + ACTIONTYPE.toString();
        string += " exist";
        return string;
    }
}