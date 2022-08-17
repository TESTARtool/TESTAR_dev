package strategynodes.terminals;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class BooleanNode extends BaseBooleanNode
{
    private final boolean BOOL;
    
    public BooleanNode(boolean bool) {this.BOOL = bool;}
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) { return BOOL; }
    
    @Override
    public String toString() {return Boolean.toString(BOOL);}
}