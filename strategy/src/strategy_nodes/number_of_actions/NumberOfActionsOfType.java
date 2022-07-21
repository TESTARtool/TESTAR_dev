package strategy_nodes.number_of_actions;

import strategy_nodes.BaseStrategyNode;
import strategy_nodes.terminals.ActionType;

public class NumberOfActionsOfType extends BaseStrategyNode
{
    private String name = "number-of-actions-of-type";
    private ActionType actionType;
    
    public NumberOfActionsOfType(ActionType actionType) {this.actionType = actionType;}
    
    @Override
    public Integer GetResult(){return null;}
    
    @Override
    public String toString() {return name;}
}