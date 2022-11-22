package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class NumberOfActions_Node extends BaseStrategyNode<Action>
{
    private Visited    VISITED;
    private Filter     FILTER;
    private ActionType ACTIONTYPE;
    
    public NumberOfActions_Node(Visited visited, Filter filter, ActionType actionType)
    {
        this.VISITED = visited;
        this.FILTER = filter;
        this.ACTIONTYPE = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: redo code
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        String string = "n-actions ";
        if(VISITED != null) string += VISITED.toString();
        if(FILTER != null) string += FILTER.toString() + " " + ACTIONTYPE.toString();
        return string;
    }
}