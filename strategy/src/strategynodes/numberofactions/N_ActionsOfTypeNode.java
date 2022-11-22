package strategynodes.numberofactions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseIntegerNode;
import parsing.treenodes.ActionType;

import java.util.Map;
import java.util.Set;

public class N_ActionsOfTypeNode extends BaseIntegerNode
{
    private String name;
    private ActionType actionType;
    
    public N_ActionsOfTypeNode(String name, ActionType actionType)
    {this.name = name; this.actionType = actionType;}
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int numberOfActions = 0;
        for(Action action : actions)
        {
            if(ActionType.RoleMatchesType(action, actionType))
                numberOfActions++;
        }
        
        return numberOfActions;
    }
    
    @Override
    public String toString() {return name + " " + actionType.toString();}
}