package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class RandomActionNode extends BaseActionNode
{
    public RandomActionNode(int weight, String name)
    {
        this.name = name;
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
}