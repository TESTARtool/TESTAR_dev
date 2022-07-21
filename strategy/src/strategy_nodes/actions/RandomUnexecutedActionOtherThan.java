package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

public class RandomUnexecutedActionOtherThan extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomUnexecutedActionOtherThan(int weight, ActionType actionType)
    {
        this.name = "random-unexecuted-action-other-than";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Integer GetResult(){return null;}
}