package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class SelectPreviousAction_Node extends BaseStrategyNode<Action>
{
    private Integer WEIGHT;
    
    public SelectPreviousAction_Node(Integer weight)
    { this.WEIGHT = (weight != null || weight > 0) ? weight : 1; }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works correctly
    {
        Action previousAction = state.get(Tags.PreviousAction, null);
        
        if(previousAction != null)
        {
            for(Action action : actions)
            {
                if(action.get(Tags.AbstractIDCustom).equals(previousAction.get(Tags.AbstractIDCustom)))
                    return action;
            }
        }
        //todo: redo code
//        return selectRandomAction(actions); // if the previous action isn't available, pick randomly
        return null;
    }
    
    @Override
    public String toString()
    {
        return WEIGHT + " select-previous-action";
    }
}