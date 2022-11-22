package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import parsing.treenodes.BaseAction_Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class R_MostExActionNode extends BaseAction_Node
{
    public R_MostExActionNode(int weight, String name)
    {
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
            if(filteredActions.size() == 0)
                return selectRandomAction(actions);
            else if (filteredActions.size() == 1)
                return new ArrayList<>(actions).get(0);
            else
                return selectRandomAction(filteredActions);
        }
        else
            return selectRandomAction(actions);
    }
    
    @Override
    public String toString()
    {
        return null;
    }
}