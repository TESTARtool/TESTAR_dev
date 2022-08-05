package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseIntegerNode;
import strategy_nodes.terminals.ActionType;

import java.util.Map;
import java.util.Set;

public class N_UnexActionsOfTypeNode extends BaseIntegerNode
{
    private String name = "number-of-unexecuted-actions-of-type";
    private ActionType actionType;
    
    public N_UnexActionsOfTypeNode(ActionType actionType) {this.actionType = actionType;}
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int count = 0;
        for(Action action : actions)
        {
            if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)) && !ActionType.RoleMatchesType(action, actionType))
                count++;
        }
        return count;
    }
    
    @Override
    public String toString() {return name + " " + actionType.toString();}
}