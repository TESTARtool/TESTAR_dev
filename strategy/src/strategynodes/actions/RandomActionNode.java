package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import parsing.treenodes.BaseAction_Node;

import java.util.Map;
import java.util.Set;

public class RandomActionNode extends BaseAction_Node
{
    public RandomActionNode(int weight)
    {
        this.WEIGHT = weight;
    }
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
//        long   graphTime = System.currentTimeMillis();
//        Random rnd       = new Random(graphTime);
//        return new ArrayList<>(actions).get(rnd.nextInt(actions.size())); //return a random action
        return selectRandomAction(actions);
    }
    
    @Override
    public String toString()
    {
        return null;
    }
}