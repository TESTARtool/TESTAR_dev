package strategynodes.instruction;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class SelectPreviousNode extends BaseActionNode
{
    public SelectPreviousNode(Integer weight)
    { this.WEIGHT = weight; }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works correctly
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
        return selectRandomAction(actions); // if the previous action isn't available, pick randomly
    }
    
    @Override
    public String toString()
    {
        return WEIGHT + " select-previous";
    }
}