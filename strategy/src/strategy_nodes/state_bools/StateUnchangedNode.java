package strategy_nodes.state_bools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.BaseStrategyNode;


import java.util.Set;

public class StateUnchangedNode extends BaseStrategyNode
{
    @Override
    public Boolean GetResult(){return null;}
    
    public Boolean GetResult(State state, Set<Action> actions)
    {
        return (state.get(Tags.AbstractIDCustom).equals(state.parent().get(Tags.AbstractIDCustom))); //todo: check if correct
    }
    
    @Override
    public String toString() {return "state-unchanged";}
}
