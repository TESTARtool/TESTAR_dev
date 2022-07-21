package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

public class RandomUnexecutedActionOfType extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomUnexecutedActionOfType(int weight, ActionType actionType)
    {
        this.name = "random-unexecuted-action-of-type";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Integer GetResult(){return null;}
}