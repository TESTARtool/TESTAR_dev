package strategy_nodes.terminals;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import java.util.Set;

public class BooleanNode extends BaseBooleanNode
{
    private final boolean BOOL;
    
    public BooleanNode(boolean bool) {this.BOOL = bool;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions) { return BOOL; }
    
    @Override
    public String toString() {return Boolean.toString(BOOL);}
}