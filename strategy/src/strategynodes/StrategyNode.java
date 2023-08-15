package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.*;

public class StrategyNode extends BaseNode<Action>
{
    private List<BaseActionNode> actionList = new ArrayList<BaseActionNode>();
    
    public StrategyNode(List<BaseActionNode> actionStrategies)
    {this.actionList.addAll(actionStrategies);}
    
    private int getTotalWeights()
    {
        int totalWeights = 0;
        for(BaseActionNode action : this.actionList)
            totalWeights += action.GetWeight();
        return totalWeights;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(actionList.size() == 1)
            return actionList.get(0).getResult(state, actions, actionsExecuted);
        else
        {
            Random r = new Random();
            int tracker = r.nextInt(getTotalWeights());
            
            for(BaseActionNode currentAction : this.actionList)
            {
                tracker -= currentAction.GetWeight();
                if(tracker <= 0) //once the tracker reaches zero, that action gets picked
                    return currentAction.getResult(state, actions, actionsExecuted);
            }
        }
        return null; //something went wrong
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        for(BaseActionNode actionStrategy : this.actionList)
            joiner.add(actionStrategy.toString());
        return joiner.toString();
    }
}
