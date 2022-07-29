package strategy_nodes.number_operators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import strategy_nodes.base_nodes.BaseIntegerNode;
import java.util.Set;

public class GreaterThanOprNode extends BaseBooleanNode
{
    protected BaseIntegerNode  left;
    protected BaseIntegerNode right;
    
    public GreaterThanOprNode(BaseIntegerNode left, BaseIntegerNode right) {this.left = left;this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions)
    {
        return left.GetResult(state, actions) > right.GetResult(state, actions);
    }
    
    @Override
    public String toString() {return left.toString() + " > " + right.toString();}
}
