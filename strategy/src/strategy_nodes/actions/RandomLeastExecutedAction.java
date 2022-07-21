package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;

public class RandomLeastExecutedAction extends BaseActionNode
{
    public RandomLeastExecutedAction(int weight)
    {
        this.name = "random-least-executed-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Integer GetResult(){return null;}
}