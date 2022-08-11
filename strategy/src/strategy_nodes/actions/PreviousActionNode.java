package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class PreviousActionNode extends BaseActionNode
{
    public PreviousActionNode(int weight, String name)
    {
        this.name = name;
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works correctly
    {
        Action previousAction = state.get(Tags.PreviousAction, null);
        
        if(previousAction != null)
        {
//            System.out.println("Previous action: " + previousAction);
            for(Action action : actions)
            {
                if(action.get(Tags.AbstractIDCustom).equals(previousAction.get(Tags.AbstractIDCustom)))
                    return action;
            }
        }
        return selectRandomAction(actions); // if the previous action isn't available, pick randomly
    }
}