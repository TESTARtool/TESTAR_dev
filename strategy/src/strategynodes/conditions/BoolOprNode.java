package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;
import strategynodes.enums.BooleanOperator;

import java.util.Set;
import java.util.StringJoiner;

public class BoolOprNode extends BaseNode implements BooleanNode
{
    private final BooleanNode left; //can be null if operator is NOT
    private final BooleanOperator operator;
    private final BooleanNode right;

    public BoolOprNode(BooleanNode left, BooleanOperator operator, BooleanNode right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions)
    {
        if(operator != BooleanOperator.NOT)
            return operator.getResult(left.getResult(state, actions), right.getResult(state, actions));
        else
            return operator.getResult(null, right.getResult(state, actions));
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("(");
        if(operator != BooleanOperator.NOT)
            joiner.add(left.toString());
        joiner.add(operator.toString());
        joiner.add(right.toString());
        joiner.add(")");
        return joiner.toString();
    }
}
