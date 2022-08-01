package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class PreviousAction extends BaseActionNode
{
    public PreviousAction(int weight)
    {
        this.name = "previous-action";
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works correctly
    {
        String previousAction = state.get(Tags.PreviousActionUsed, null);
        
        if(previousAction == null)
        {
            System.out.println("Previous action: " + previousAction);
            return selectRandomAction(actions);
        }
        
        for(Action action : actions)
        {
            if(action.get(Tags.AbstractIDCustom).equals(previousAction))
                return action;
        }
        return selectRandomAction(actions); // if the previous action isn't available, pick randomly
    }
}