package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;
import strategy_nodes.terminals.ActionType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class RandomUnexecutedActionOtherThan extends BaseActionNode
{
    private ActionType actionType;
    
    public RandomUnexecutedActionOtherThan(int weight, ActionType actionType)
    {
        this.name = "random-unexecuted-action-other-than";
        this.WEIGHT = weight;
        this.actionType = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        System.out.println("Map: " + actionsExecuted.toString());
        
        if(actions.size() == 1)
            return new ArrayList<>(actions).get(0);
        
        ArrayList filteredActions = new ArrayList();
        if(actions.size() > actionsExecuted.size()) // there are some unexecuted actions
        {
            for(Action action : actions)
            {
                if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)) && !ActionType.RoleMatchesType(action, actionType))
                    filteredActions.add(action);
            }
        }
        return selectRandomAction(filteredActions);
    }
    
    @Override
    public String toString() {return String.valueOf(WEIGHT) + " " + name + " " + actionType.toString();}
}