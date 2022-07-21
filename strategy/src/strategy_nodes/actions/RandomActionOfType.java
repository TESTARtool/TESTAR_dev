package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

import java.util.Set;

public class RandomActionOfType extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomActionOfType(int weight, ActionType actionType)
    {
        this.name = "random-action-of-type";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Integer GetResult(){return null;}
    
//    public Integer GetResult(State state, Set<Action> actions)
//    {
//        for(Action action : actions)
//        {
//            if(action.get(Tags.Role, ActionRoles.LeftClick) == ActionRoles.LeftClick)
//        }
//    }
}