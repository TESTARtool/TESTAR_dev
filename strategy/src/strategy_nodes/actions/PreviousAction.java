package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.Set;

public class PreviousAction extends BaseActionNode
{
    public PreviousAction(int weight)
    {
        this.name = "previous-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions)
    {
        return null; //todo
    }
}