package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
import strategy_nodes.base_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

import java.util.ArrayList;
import java.util.Set;

public class RandomActionOtherThan extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomActionOtherThan(int weight, ActionType actionType)
    {
        this.name = "random-action-other-than";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions)
    {
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