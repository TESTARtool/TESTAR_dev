package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.*;

public class AnyExistNode extends BaseStrategyNode<Boolean>
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;
    
    public AnyExistNode(VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.VISITED_MODIFIER = visitedModifier;
        this.FILTER = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        boolean validActionFound = (VISITED_MODIFIER == null && FILTER == null && ACTION_TYPE == null && !actions.isEmpty());
        
        if(VISITED_MODIFIER != null)
            validActionFound = validActionExists(VISITED_MODIFIER, actions, actionsExecuted);
        
        if(FILTER != null && ACTION_TYPE != null)
            validActionFound = validActionTypeExists(actions, FILTER, ACTION_TYPE);
        
        return validActionFound;
    }
    
    @Override
    public String toString()
    {
        String string = "any-exist";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER;
        if(FILTER != null) string += " " + FILTER + " " + ACTION_TYPE.toString();
        return string;
    }
}