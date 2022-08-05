package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class Total_N_PrevExActionsNode extends BaseIntegerNode
{
    private String name;
    
    public Total_N_PrevExActionsNode(String name)
    {
        this.name = name;
    }
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int count = 0;
        for(Action action : actions)
        {
            if(actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)))
                count++;
        }
        return count;
    }
    
    @Override
    public String toString() {return name;}
}