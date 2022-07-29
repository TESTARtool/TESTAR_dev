package strategy_nodes.state_bools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import strategy_nodes.terminals.SutType;

import java.util.Map;
import java.util.Set;

public class SutTypeIsNode extends BaseBooleanNode
{
    private SutType sutType;
    
    public SutTypeIsNode(SutType operatingSystem)
    {
        this.sutType = operatingSystem;
    }
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
//        state.get(Tag.)
        return null; //todo
    }
    
    @Override
    public String toString() {return "sut-type-is " + sutType.toString();}
}
