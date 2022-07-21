package strategy_nodes.state_bools;

import strategy_nodes.BaseStrategyNode;
import strategy_nodes.terminals.ActionType;

public class AvailableActionOfTypeNode extends BaseStrategyNode
{
    private ActionType actionType;
    
    public AvailableActionOfTypeNode(ActionType actionType)
    {
        this.actionType = actionType;
    }
    
    @Override
    public Boolean GetResult(){return null;}
    
    @Override
    public String toString() {return "available-actions-of-type" + actionType.toString();}
}
