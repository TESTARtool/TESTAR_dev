package strategynodes.numberofactions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.basenodes.BaseIntegerNode;
import strategynodes.terminals.ActionType;

import java.util.Map;
import java.util.Set;

public class N_ExActionsOfTypeNode extends BaseIntegerNode
{
    private String name;
    private ActionType actionType;
    
    public N_ExActionsOfTypeNode(String name, ActionType actionType)
    {this.name = name; this.actionType = actionType;}
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int count = 0;
        for(Action action : actions)
        {
            if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)) && ActionType.RoleMatchesType(action, actionType))
                count++;
        }
        return count;
    }
    
    @Override
    public String toString() {return name + " " + actionType.toString();}
}