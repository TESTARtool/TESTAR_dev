package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class Total_N_ActionsNode extends BaseIntegerNode
{
    private String name;
    
    public Total_N_ActionsNode(String name)
    {
        this.name = name;
    }
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return actions.size();
    }
    
    @Override
    public String toString() {return name;}
}