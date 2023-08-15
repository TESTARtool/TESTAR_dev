package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseBooleanNode;
import strategynodes.BaseNode;
import strategynodes.condition.IntegerOperator;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class IntOprNode extends BaseBooleanNode
{
    private BaseNode<Integer> left;
    private IntegerOperator   operator;
    private BaseNode<Integer> right;

    
    public IntOprNode(BaseNode<Integer> left, IntegerOperator operator, BaseNode<Integer> right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return operator.getResult
                (left.getResult(state, actions, actionsExecuted),
                right.getResult(state, actions, actionsExecuted));
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("(");
        joiner.add(left.toString());
        joiner.add(operator.toString());
        joiner.add(right.toString());
        joiner.add(")");
        return joiner.toString();
    }
}
