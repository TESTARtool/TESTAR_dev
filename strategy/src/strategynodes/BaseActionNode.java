package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.*;

public abstract class BaseActionNode extends BaseNode<Action>
{
    protected int WEIGHT;
    
    public void assignWeight(int weight)
    {
        this.WEIGHT = (weight > 0) ? weight : 1;
    }
    
    @Override
    public abstract Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted,
                                     ArrayList<String> operatingSystems);
    
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
