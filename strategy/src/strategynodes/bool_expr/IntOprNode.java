package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class IntOprNode extends BaseStrategyNode<Boolean>
{
    private BaseStrategyNode<Integer>   left;
    private IntegerOperator             operator;
    private BaseStrategyNode<Integer>   right;

    
    public IntOprNode(BaseStrategyNode<Integer>  left, IntegerOperator operator, BaseStrategyNode<Integer>  right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return operator.getResult(
                left.getResult(state, actions, actionsExecuted),
                right.getResult(state, actions, actionsExecuted));
    }
    
    @Override
    public String toString()
    {
        return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }
}
