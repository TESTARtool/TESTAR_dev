package strategy_nodes.state_bools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseBooleanNode;
import strategy_nodes.terminals.ActionType;

import java.util.Map;
import java.util.Set;

public class AvailableActionOfTypeNode extends BaseBooleanNode
{
    private ActionType actionType;
    
    public AvailableActionOfTypeNode(ActionType actionType)
    {
        this.actionType = actionType;
    }
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        boolean available = false;
        for(Action action : actions)
        {
            if(ActionType.RoleMatchesType(action, actionType))
            {
                available = true;
                break;
            }
        }

//        System.out.println("actions of type " + actionType.toString() + "available: " + available);
        
        return available;
    }
    
    @Override
    public String toString() {return "available-actions-of-type" + actionType.toString();}
}
