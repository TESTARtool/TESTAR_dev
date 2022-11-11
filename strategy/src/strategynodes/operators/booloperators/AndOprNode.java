package strategynodes.operators.booloperators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class AndOprNode extends BaseBooleanNode
{
    protected BaseBooleanNode  left;
    protected BaseBooleanNode right;
    
    public AndOprNode(BaseBooleanNode left, BaseBooleanNode right) {this.left = left; this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return left.GetResult(state, actions, actionsExecuted) && right.GetResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString()
    {
        return "(" + left.toString() + " AND " + right.toString() + ")";
    }
}
