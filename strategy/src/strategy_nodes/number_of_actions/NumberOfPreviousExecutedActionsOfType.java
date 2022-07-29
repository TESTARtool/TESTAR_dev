package strategy_nodes.number_of_actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;
import strategy_nodes.terminals.ActionType;

import java.util.Map;
import java.util.Set;

public class NumberOfPreviousExecutedActionsOfType extends BaseIntegerNode
{
    private String name = "number-of-previous-executed-actions-of-type";
    private ActionType actionType;
    
    public NumberOfPreviousExecutedActionsOfType(ActionType actionType) {this.actionType = actionType;}
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return name + " " + actionType.toString();}
}