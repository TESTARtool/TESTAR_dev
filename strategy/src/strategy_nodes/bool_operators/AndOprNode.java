package strategy_nodes.bool_operators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import java.util.Set;

public class AndOprNode extends BaseBooleanNode
{
    protected BaseBooleanNode  left;
    protected BaseBooleanNode right;
    
    public AndOprNode(BaseBooleanNode left, BaseBooleanNode right) {this.left = left; this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions)
    {
        return left.GetResult(state, actions) && right.GetResult(state, actions);
    }
    
    @Override
    public String toString()
    {
        return left.toString() + " AND " + right.toString();
    }
}
