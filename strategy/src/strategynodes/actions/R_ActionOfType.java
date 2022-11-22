package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import parsing.treenodes.BaseAction_Node;
import parsing.treenodes.ActionType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class R_ActionOfType extends BaseAction_Node
{
    private final ActionType actionType;
    
    public R_ActionOfType(int weight, ActionType actionType)
    {
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
    public String toString() {return WEIGHT + " " + actionType.toString();}
}