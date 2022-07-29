package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

import java.util.Set;

public class RandomUnexecutedActionOfType extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomUnexecutedActionOfType(int weight, ActionType actionType)
    {
        this.name = "random-unexecuted-action-of-type";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return String.valueOf(WEIGHT) + " " + name + " " + actionType.toString();}
}