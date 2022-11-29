package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class IntOprNode extends BaseStrategyNode<Boolean>
{
    private BaseStrategyNode<Integer>   left;
    private Integer                   leftInt;
    private IntegerOperator           operator;
    private BaseStrategyNode<Integer> right;
    private Integer                     rightInt;
    
    private boolean leftIsInt = false;
    private boolean rightIsInt = false;
    
    public IntOprNode(Object left, IntegerOperator operator, Object right)
    {
        if(left instanceof BaseStrategyNode)
            this.left = (BaseStrategyNode<Integer>) left;
        else if (left instanceof Integer)
        {
            this.leftInt = (Integer) left;
            this.leftIsInt = true;
        }
        this.operator = operator;
        if(right instanceof BaseStrategyNode)
            this.right = (BaseStrategyNode<Integer>) right;
        else if (right instanceof Integer)
        {
            this.rightInt = (Integer) right;
            this.rightIsInt = true;
        }
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Integer leftResult = (leftIsInt) ? leftInt : left.getResult(state, actions, actionsExecuted);
        Integer rightResult = (rightIsInt) ? rightInt : right.getResult(state, actions, actionsExecuted);
        
        return operator.getResult(leftResult, rightResult);
    }
    
    @Override
    public String toString()
    {
        return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }
}
