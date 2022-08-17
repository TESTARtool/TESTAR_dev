package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseActionNode;
import strategynodes.terminals.ActionType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class R_ActionNotTypeNode extends BaseActionNode
{
    private ActionType actionType;
    
    public R_ActionNotTypeNode(int weight, String name, ActionType actionType)
    {
        this.name = name;
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(actions.size() == 1)
            return new ArrayList<>(actions).get(0);
        
        ArrayList<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        {
            if(ActionType.RoleMatchesType(action, actionType))
                filteredActions.add(action);
        }
        
        if(filteredActions.size() == 0)
            return selectRandomAction(actions);
        else if (filteredActions.size() == 1)
            return new ArrayList<>(actions).get(0);
        else
            return selectRandomAction(filteredActions);
    }
    
    @Override
    public String toString() {return String.valueOf(WEIGHT) + " " + name + " " + actionType.toString();}
}