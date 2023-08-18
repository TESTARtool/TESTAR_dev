package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseBooleanNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class BoolOprNode extends BaseBooleanNode
{
    private BaseBooleanNode left; //can be null if operator is NOT
    private BooleanOperator operator;
    private BaseBooleanNode right;

    public BoolOprNode(BaseBooleanNode left, BooleanOperator operator, BaseBooleanNode right)    {

        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if(operator != BooleanOperator.NOT)
            return operator.getResult(left.getResult(state, actions, actionsExecuted, operatingSystems), right.getResult(state, actions, actionsExecuted, operatingSystems));
        else
            return operator.getResult(null, right.getResult(state, actions, actionsExecuted, operatingSystems));
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
