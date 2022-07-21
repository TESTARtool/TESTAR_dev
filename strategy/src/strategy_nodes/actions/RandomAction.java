package strategy_nodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomAction extends BaseActionNode
{
    public RandomAction(int weight)
    {
        this.name = "random-action";
        this.WEIGHT = weight;
    }
    @Override
    public Action GetResult()//Set<Action> actions)
    {
        long   graphTime = System.currentTimeMillis();
        Random rnd       = new Random(graphTime);
//        return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size()));
        return null;
    }
    
    public Action GetResult(State state, Set<Action> actions)
    {
        long   graphTime = System.currentTimeMillis();
        Random rnd       = new Random(graphTime);
        return new ArrayList<>(actions).get(rnd.nextInt(actions.size()));
    }
}