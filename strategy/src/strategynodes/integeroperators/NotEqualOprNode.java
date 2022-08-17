package strategynodes.integeroperators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;
import strategynodes.basenodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class NotEqualOprNode extends BaseBooleanNode
{
    protected BaseIntegerNode left;
    protected BaseIntegerNode right;
    
    public NotEqualOprNode(BaseIntegerNode left, BaseIntegerNode right) {this.left = left;this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return left.GetResult(state, actions, actionsExecuted) != right.GetResult(state, actions, actionsExecuted);
    }
    
    @Override
    public String toString() {return left.toString() + " != " + right.toString();}
}