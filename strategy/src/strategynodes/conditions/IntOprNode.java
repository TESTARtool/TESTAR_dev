package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;
import strategynodes.enums.IntegerOperator;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class IntOprNode extends BaseNode implements BooleanNode
{
    private final IntegerNode left;
    private final IntegerOperator operator;
    private final IntegerNode right;

    
    public IntOprNode(IntegerNode left, IntegerOperator operator, IntegerNode right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        return operator.getResult
                (left.getResult(state, actions, actionsExecuted, operatingSystems),
                right.getResult(state, actions, actionsExecuted, operatingSystems));
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
