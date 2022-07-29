package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;
import strategy_nodes.base_nodes.BaseStrategyNode;

import java.util.Set;

public class TotalNumberOfUnexecutedActions extends BaseIntegerNode
{
    private String name = "total-number-of-unexecuted-actions";
    
    @Override
    public Integer GetResult(State state, Set<Action> actions)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return name;}
}