package strategy_nodes.actions;

import strategy_nodes.BaseActionNode;

public class PreviousAction extends BaseActionNode
{
    public PreviousAction(int weight)
    {
        this.name = "previous-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Integer GetResult(){return null;}
}