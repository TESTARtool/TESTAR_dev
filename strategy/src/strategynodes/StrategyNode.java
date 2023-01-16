package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.action_expr.ActionListNode;

import java.util.Map;
import java.util.Set;

public class StrategyNode extends BaseStrategyNode<Action>
{
    private IfThenElseNode ifThenElse;
    private ActionListNode actionList;

    public StrategyNode(ActionListNode actionList)
    {this.actionList = actionList;}
    public StrategyNode(IfThenElseNode ifThenElse)
    {this.ifThenElse = ifThenElse;}
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(ifThenElse != null)
            return ifThenElse.getResult(state, actions, actionsExecuted);
        else
            return actionList.getResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString()
    {
        if(ifThenElse != null)
            return ifThenElse.toString();
        else
            return actionList.toString();
    }
}