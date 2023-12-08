package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;

import java.util.*;

public class RepeatPreviousNode extends BaseNode<Action> implements ActionNode
{
    public final Weight weight;

    public RepeatPreviousNode(Integer weight)
    { this.weight = new Weight(weight); }
    
    @Override
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems) //todo: check if it works correctly
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
    public int GetWeight()
    {
        return weight.GetWeight();
    }

    @Override
    public String toString()
    {
        return weight.toString() + " repeat-previous";
    }
}