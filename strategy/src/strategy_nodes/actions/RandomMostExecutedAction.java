package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RandomMostExecutedAction extends BaseActionNode
{
    public RandomMostExecutedAction(int weight)
    {
        this.name = "random-most-executed-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
//        System.out.println("Map: " + actionsExecuted.toString());
        
        if(actions.size() == 1)
            return new ArrayList<>(actions).get(0);
        
        ArrayList filteredActions = new ArrayList();
        if(actionsExecuted.size() > 0) // there is at least one executed action
        {
            Integer maxCount = Collections.max(actionsExecuted.values());
            ArrayList<String> actionIDs = new ArrayList<>();
            for(Map.Entry<String, Integer> entry : actionsExecuted.entrySet())
            {
                if(entry.getValue().equals(maxCount))
                    actionIDs.add(entry.getKey());
            }
            for(Action action : actions)
            {
                if(actionIDs.contains(action.get(Tags.AbstractIDCustom)))
                    filteredActions.add(action);
            }
            return selectRandomAction(filteredActions);
        }
        else
            return selectRandomAction(actions);
    }
}