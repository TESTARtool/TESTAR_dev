package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class AnyActionsExist_Node extends BaseStrategyNode<Action>
{
    private Filter     FILTER;
    private ActionType ACTIONTYPE;
    
    public AnyActionsExist_Node(Filter filter, ActionType actionType)
    {
        this.FILTER = filter;
        this.ACTIONTYPE = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        //todo: redo code
        return null;
    }
    
    @Override
    public String toString()
    {
        String string = " any-actions ";
        if(FILTER != null) string += FILTER.toString() + " " + ACTIONTYPE.toString();
        string += "exist";
        return string;
    }
}