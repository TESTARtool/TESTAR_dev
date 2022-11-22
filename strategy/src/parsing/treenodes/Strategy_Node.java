package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class Strategy_Node extends BaseStrategyNode<Action>
{
    private BaseBooleanNode  ifChild;
    private BaseStrategyNode thenChild; //StrategyNode or ActionListNode
    private BaseStrategyNode elseChild; //StrategyNode or ActionListNode
    
    public Strategy_Node(BaseBooleanNode ifChild, BaseStrategyNode thenChild, BaseStrategyNode elseChild)
    {
        this.ifChild = ifChild;
        this.thenChild = thenChild;
        this.elseChild = elseChild;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(ifChild.GetResult(state, actions, actionsExecuted))
            return (Action) thenChild.GetResult(state, actions, actionsExecuted);
        else
            return (Action) elseChild.GetResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString()
    {
        return "IF "    +   ifChild.toString()      + "\n" +
               "THEN "  +   thenChild.toString()    + "\n" +
               "ELSE "  +   elseChild.toString();
    }
}