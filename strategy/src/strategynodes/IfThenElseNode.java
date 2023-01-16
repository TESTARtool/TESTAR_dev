package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.action_expr.ActionListNode;

import java.util.Map;
import java.util.Set;

public class IfThenElseNode extends BaseStrategyNode<Action>
{
    private BaseStrategyNode<Boolean> ifChild;
    private BaseStrategyNode<Action> thenChild; //StrategyNode or ActionListNode
    private BaseStrategyNode<Action> elseChild; //StrategyNode or ActionListNode

    public IfThenElseNode(BaseStrategyNode ifChild, BaseStrategyNode thenChild, BaseStrategyNode elseChild)
    {
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    elseChild;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
            if (ifChild.getResult(state, actions, actionsExecuted))
                return thenChild.getResult(state, actions, actionsExecuted);
            else
                return elseChild.getResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString()
    {
        return
                "IF "    +   ifChild.toString()      + "\n" +
                "THEN "  +   thenChild.toString()    + "\n" +
                "ELSE "  +   elseChild.toString();
    }
}