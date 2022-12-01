package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class StrategyNode extends BaseStrategyNode<Action>
{
    private BaseStrategyNode<Boolean> ifChild;
    private BaseStrategyNode<Action> thenChild; //StrategyNode or ActionListNode
    private BaseStrategyNode<Action> elseChild; //StrategyNode or ActionListNode
    
    public StrategyNode(BaseStrategyNode ifChild, BaseStrategyNode thenChild, BaseStrategyNode elseChild)
    {
        this.ifChild = ifChild;
        this.thenChild = thenChild;
        this.elseChild = elseChild;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(ifChild.getResult(state, actions, actionsExecuted))
            return (Action) thenChild.getResult(state, actions, actionsExecuted);
        else
            return (Action) elseChild.getResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString()
    {
        return "IF "    +   ifChild.toString()      + "\n" +
               "THEN "  +   thenChild.toString()    + "\n" +
               "ELSE "  +   elseChild.toString();
    }
}