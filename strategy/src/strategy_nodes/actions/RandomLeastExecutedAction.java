package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RandomLeastExecutedAction extends BaseActionNode
{
    public RandomLeastExecutedAction(int weight)
    {
        this.name = "random-least-executed-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if this works
    {
        ArrayList filteredActions = new ArrayList();
        if(actions.size() > actionsExecuted.size()) // there are some unexecuted actions
        {
            for(Action action : actions)
            {
                if(actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)))
                    filteredActions.add(action);
            }
            return selectRandomAction(filteredActions);
        }
        else // all actions have been used at least once
        {
            Integer minCount = Collections.min(actionsExecuted.values());
            ArrayList<String> actionIDs = new ArrayList<>();
            for(Map.Entry<String, Integer> entry : actionsExecuted.entrySet())
            {
                if(entry.getValue().equals(minCount))
                    actionIDs.add(entry.getKey());
            }
            for(Action action : actions)
            {
                if(actionIDs.contains(action.get(Tags.AbstractIDCustom)))
                    filteredActions.add(action);
            }
            return selectRandomAction(filteredActions);
        }
    }
}