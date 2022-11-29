package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class BaseActionNode extends BaseStrategyNode<Action>
{
    protected int WEIGHT;
    
    @Override
    public abstract Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
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
