package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RandomUnexecutedAction extends BaseActionNode
{
    public RandomUnexecutedAction(int weight)
    {
        this.name = "random-unexecuted-action";
        this.WEIGHT = weight;
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
                if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)))
                    filteredActions.add(action);
            }
        }
        return selectRandomAction(filteredActions);
    }
}