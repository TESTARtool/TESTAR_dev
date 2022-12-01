package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class BoolOprNode extends BaseStrategyNode<Boolean>
{
    private BaseStrategyNode<Boolean>   left; //can be null if operator is NOT
    private Boolean                     leftBool;
    private BooleanOperator             operator;
    private BaseStrategyNode<Boolean>   right;
    private Boolean                     rightBool;
    
    private boolean leftIsBool = false;
    private boolean rightIsBool = false;
    
    public BoolOprNode(Object left, BooleanOperator operator, Object right)    {
        if(left instanceof BaseStrategyNode)
            this.left = (BaseStrategyNode<Boolean>) left;
        else if (left instanceof Boolean)
        {
            this.leftBool = (Boolean) left;
            this.leftIsBool = true;
        }
        this.operator = operator;
        if(right instanceof BaseStrategyNode)
            this.right = (BaseStrategyNode<Boolean>) right;
        else if (right instanceof Boolean)
        {
            this.rightBool = (Boolean) right;
            this.rightIsBool = true;
        }
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Boolean rightResult = (rightIsBool) ? rightBool : right.getResult(state, actions, actionsExecuted);
        if(operator == BooleanOperator.NOT)
        {
            Boolean leftResult = (leftIsBool) ? leftBool : left.getResult(state, actions, actionsExecuted);
            return operator.getResult(leftResult, rightResult);
        }
        else
            return operator.getResult(null, rightResult);
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
