package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class TotalNumberOfPreviousExecutedActions extends BaseIntegerNode
{
    private String name = "total-number-of-previous-executed-actions";
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return name;}
}