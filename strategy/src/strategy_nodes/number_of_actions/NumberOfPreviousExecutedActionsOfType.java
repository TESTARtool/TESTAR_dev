package strategy_nodes.number_of_actions;

import strategy_nodes.BaseStrategyNode;
import strategy_nodes.terminals.ActionType;

public class NumberOfPreviousExecutedActionsOfType extends BaseStrategyNode
{
    private String name = "number-of-previous-executed-actions-of-type";
    private ActionType actionType;
    
    public NumberOfPreviousExecutedActionsOfType(ActionType actionType) {this.actionType = actionType;}
    
    @Override
    public Integer GetResult(){return null;}
    
    @Override
    public String toString() {return name;}
}