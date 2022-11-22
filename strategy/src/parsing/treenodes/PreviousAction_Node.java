package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class PreviousAction_Node extends BaseAction_Node
{
    public PreviousAction_Node(Integer weight)
    {
        this.WEIGHT = (weight != null || weight > 0) ? weight : 1;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo
    {
//        Action previousAction = state.get(Tags.PreviousAction, null);
//
//        if(previousAction != null)
//        {
////            System.out.println("Previous action: " + previousAction);
//            for(Action action : actions)
//            {
//                if(action.get(Tags.AbstractIDCustom).equals(previousAction.get(Tags.AbstractIDCustom)))
//                    return action;
//            }
//        }
//        return selectRandomAction(actions); // if the previous action isn't available, pick randomly
        return null;
    }
    
    @Override
    public String toString()
    { return "select-previous-action"; }
}