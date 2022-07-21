package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;

public class RandomUnexecutedAction extends BaseActionNode
{
    public RandomUnexecutedAction(int weight)
    {
        this.name = "random-unexecuted-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Integer GetResult(){return null;}
}