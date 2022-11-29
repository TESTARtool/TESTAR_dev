package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class BoolOprNode extends BaseStrategyNode<Boolean>
{
    private BaseStrategyNode<Boolean> left; //can be null if operator is NOT
    private BooleanOperator           operator;
    private BaseStrategyNode<Boolean> right;
    
    
    public BoolOprNode(BaseStrategyNode left, BooleanOperator operator, BaseStrategyNode right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(operator == BooleanOperator.NOT)
            return operator.getResult(null, right.getResult(state, actions, actionsExecuted));
        else
            return operator.getResult(left.getResult(state, actions, actionsExecuted), right.getResult(state, actions, actionsExecuted));
    }
    
    @Override
    public String toString()
    {
        if(operator == BooleanOperator.NOT)
            return "(" + operator + " " + right.toString() + ")";
        else
            return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }
}
