package strategy_nodes.number_of_actions;

import strategy_nodes.BaseStrategyNode;
import strategy_nodes.terminals.ActionType;

public class NumberOfUnexecutedActionsOfType extends BaseStrategyNode
{
    private String name = "number-of-unexecuted-actions-of-type";
    private ActionType actionType;
    
    public NumberOfUnexecutedActionsOfType(ActionType actionType) {this.actionType = actionType;}
    
    @Override
    public Integer GetResult(){return null;}
    
    @Override
    public String toString() {return name;}
}