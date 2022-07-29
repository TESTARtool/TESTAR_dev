package strategy_nodes.bool_operators;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import java.util.Set;

public class NotOprNode extends BaseBooleanNode
{
    protected BaseBooleanNode expr;
    
    public NotOprNode(BaseBooleanNode right) {this.expr = right;}
    @Override
    public Boolean GetResult(State state, Set<Action> actions) { return !expr.GetResult(state, actions);}
    
    @Override
    public String toString() {return "NOT " + expr.toString();}
}