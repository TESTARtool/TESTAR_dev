package strategy_nodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public class StrategyNode extends BaseStrategyNode
{
    private BaseStrategyNode ifChild;
    private BaseStrategyNode thenChild; //StrategyNode or ActionListNode
    private BaseStrategyNode elseChild; //StrategyNode or ActionListNode
    
    public StrategyNode(BaseStrategyNode ifChild, BaseStrategyNode thenChild, BaseStrategyNode elseChild)
    {
        this.ifChild = ifChild;
        this.thenChild = thenChild;
        this.elseChild = elseChild;
    }
    
    @Override
    public Object GetResult()
    {
        if((boolean)ifChild.GetResult())    return thenChild.GetResult();
        else                                return elseChild.GetResult();
    }
    public Action GetResult(State state, Set<Action> actions)
    {
        if((boolean)ifChild.GetResult())    return (Action) thenChild.GetResult();
        else                                return (Action) elseChild.GetResult();
    }
    
    @Override
    public String toString()
    {
        return "IF " +      ifChild.toString() +
               " THEN " +   thenChild.toString() +
               " ELSE " +   elseChild.toString();
    }
}