package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;

import java.util.Set;

public class TotalNumberOfActions extends BaseIntegerNode
{
    private String name = "total-number-of-actions";
    
    @Override
    public Integer GetResult(State state, Set<Action> actions)
    {
        return actions.size();
    }
    
    @Override
    public String toString() {return name;}
}