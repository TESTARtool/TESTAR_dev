package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.basenodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class R_LeastExActionNode extends BaseActionNode
{
    public R_LeastExActionNode(int weight, String name)
    {
        this.name = name;
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
            if(filteredActions.size() == 0)
                return selectRandomAction(actions);
            else if (filteredActions.size() == 1)
                return new ArrayList<>(actions).get(0);
            else
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
            if(filteredActions.size() == 0)
                return selectRandomAction(actions);
            else if (filteredActions.size() == 1)
                return new ArrayList<>(actions).get(0);
            else
                return selectRandomAction(filteredActions);
        }
    }
}