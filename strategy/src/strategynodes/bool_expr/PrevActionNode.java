package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;

import java.util.Map;
import java.util.Set;

public class PrevActionNode extends BaseStrategyNode<Boolean>
{
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;

    public PrevActionNode(Filter filter, ActionType actionType)
    {
        this.FILTER = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return false;

        if(FILTER != null && ACTION_TYPE != null)
            return filterAllowsAction(prevAction, FILTER, ACTION_TYPE);

        return false; //default is false
    }
    
    @Override
    public String toString()
    {
        String string = "prev-action";
        if(FILTER != null) string += " " + FILTER + " " + ACTION_TYPE.toString();
        return string;
    }
}