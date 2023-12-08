package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;

import java.util.*;

public class ListNode extends BaseNode<Action>
{
    private final List<ActionNode> actionList = new ArrayList<>();
    
    public ListNode(List<ActionNode> actionStrategies)
    {this.actionList.addAll(actionStrategies);}
    
    private int getTotalWeights()
    {
        int totalWeights = 0;
        for(ActionNode action : this.actionList)
            totalWeights += action.GetWeight();
        return totalWeights;
    }

    @Override
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if(actionList.size() == 1)
            return actionList.get(0).getResult(state, actions, actionsExecuted, operatingSystems);
        else
        {
            Random r = new Random();
            int tracker = r.nextInt(getTotalWeights());
            
            for(ActionNode currentAction : this.actionList)
            {
                tracker -= currentAction.GetWeight();
                if(tracker <= 0) //once the tracker reaches zero, that action gets picked
                    return currentAction.getResult(state, actions, actionsExecuted, operatingSystems);
            }
        }
        return null; //something went wrong
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        for(ActionNode actionStrategy : this.actionList)
            joiner.add(actionStrategy.toString());
        return joiner.toString();
    }
}
