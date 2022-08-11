package strategy_nodes.base_nodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class BaseActionNode extends BaseStrategyNode<Action>
{
    protected String name;
    protected int WEIGHT;
    
    @Override
    public abstract Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
    @Override
    public String toString() {return WEIGHT + " " + name;}
    
    public int GetWeight() {return WEIGHT;}
    
    protected Action selectRandomAction(Set<Action> actions)
    {
        Random rnd = new Random();
        return new ArrayList<>(actions).get(rnd.nextInt(actions.size())); //return a random action
    }
    
    protected Action selectRandomAction(ArrayList<Action> actions)
    {
        Random rnd = new Random();
        return actions.get(rnd.nextInt(actions.size())); //return a random action
    }
}
