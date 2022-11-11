package strategynodes.operators.integeroperators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;
import strategynodes.basenodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class LessEqualThanOprNode extends BaseBooleanNode
{
    protected BaseIntegerNode  left;
    protected BaseIntegerNode right;
    
    public LessEqualThanOprNode(BaseIntegerNode left, BaseIntegerNode right) {this.left = left;this.right = right;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return (int)left.GetResult(state, actions, actionsExecuted) <= (int)right.GetResult(state, actions,
                                                                                            actionsExecuted);
    }
    
    @Override
    public String toString() {return left.toString() + " <= " + right.toString();}
}