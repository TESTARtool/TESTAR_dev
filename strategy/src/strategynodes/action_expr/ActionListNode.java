package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.*;

public class ActionListNode extends BaseStrategyNode<Action>
{
    private List<BaseActionNode> actionStrategies = new ArrayList<BaseActionNode>();
    
    public ActionListNode(List<BaseActionNode> actionStrategies) {this.actionStrategies.addAll(actionStrategies);}
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(actionStrategies.size() == 1)
            return actionStrategies.get(0).getResult(state, actions, actionsExecuted);
        else
        {
            int totalWeights = 0;
            for(BaseActionNode action : this.actionStrategies)
                totalWeights += action.GetWeight();
            Random r = new Random();
            int t = totalWeights;
            for(BaseActionNode actionStrategy : this.actionStrategies)
            {
                int i = r.nextInt(totalWeights);
                if(i >= actionStrategy.GetWeight())
                    t -= actionStrategy.GetWeight();
                else
                    return actionStrategy.getResult(state, actions, actionsExecuted);
            }
        }
        return null; //something went wrong
    }
    
    @Override
    public String toString()
    {
        StringJoiner actionJoiner = new StringJoiner(" ");
        for(BaseActionNode actionStrategy : this.actionStrategies)
            actionJoiner.add(actionStrategy.toString());
        return actionJoiner.toString();
    }
}