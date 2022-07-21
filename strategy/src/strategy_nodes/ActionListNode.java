package strategy_nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class ActionListNode extends BaseStrategyNode
{
    private List<BaseActionNode> actions = new ArrayList<BaseActionNode>();
    
    public ActionListNode(List<BaseActionNode> actions) {this.actions.addAll(actions);}
    
    @Override
    public Object GetResult() //todo: deal with result
    {
        if(actions.size() == 1)
            return actions.get(0).GetResult();
        else
        {
            int totalWeights = 0;
            for(BaseActionNode action : this.actions)
                totalWeights += action.GetWeight();
            Random r = new Random();
            int t = totalWeights;
            for(BaseActionNode action : this.actions)
            {
                int i = r.nextInt(totalWeights);
                if(i >= action.GetWeight())
                    t -= action.GetWeight();
                else
                    return action.GetResult();
            }
        }
        return null;
    }
    
    @Override
    public String toString()
    {
        StringJoiner actionJoiner = new StringJoiner(" ");
        for(BaseActionNode action : this.actions)
            actionJoiner.add(action.toString());
        return actionJoiner.toString();
    }
}
