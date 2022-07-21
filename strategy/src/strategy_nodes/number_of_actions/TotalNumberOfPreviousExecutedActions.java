package strategy_nodes.number_of_actions;

import strategy_nodes.BaseStrategyNode;

public class TotalNumberOfPreviousExecutedActions extends BaseStrategyNode
{
    private String name = "total-number-of-previous-executed-actions";
    
    @Override
    public Integer GetResult(){return null;}
    
    @Override
    public String toString() {return name;}
}