package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.*;

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
    
    protected Action selectRandomAction(List<Action> actions)
    {
        Random rnd = new Random();
        return actions.get(rnd.nextInt(actions.size())); //return a random action
    }
}
