package strategynodes.statebools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;
import parsing.treenodes.ActionType;

import java.util.Map;
import java.util.Set;

public class AvailableActionOfTypeNode extends BaseBooleanNode
{
    private String name;
    private ActionType actionType;
    
    public AvailableActionOfTypeNode(String name, ActionType actionType)
    {
        this.name = name;
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
        
        return available;
    }
    
    @Override
    public String toString() {return name + " " + actionType.toString();}
}
