package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;

public class RandomMostExecutedAction extends BaseActionNode
{
    public RandomMostExecutedAction(int weight)
    {
        this.name = "random-most-executed-action";
        this.WEIGHT = weight;
    }
    @Override
    public Integer GetResult(){return null;}
}