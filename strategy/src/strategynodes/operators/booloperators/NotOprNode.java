package strategynodes.operators.booloperators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class NotOprNode extends BaseBooleanNode
{
    protected BaseBooleanNode expr;
    
    public NotOprNode(BaseBooleanNode right) {this.expr = right;}
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) { return !expr.GetResult(state, actions, actionsExecuted);}
    
    @Override
    public String toString() {return "(NOT " + expr.toString() + ")";}
}