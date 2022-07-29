package strategy_nodes.bool_operators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import strategy_nodes.base_nodes.BaseStrategyNode;

import java.util.Set;

public class OrOprNode extends BaseBooleanNode
{
    protected BaseStrategyNode<Boolean> left;
    protected BaseStrategyNode<Boolean> right;
    
    public OrOprNode(BaseStrategyNode left, BaseStrategyNode right) {this.left = left; this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions)
    {
        return left.GetResult(state, actions) || right.GetResult(state, actions);
    }
    
    @Override
    public String toString()
    {
        return left.toString() + " OR " + right.toString();
    }
}
