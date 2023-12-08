package strategynodes;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

import java.util.*;

public abstract class BaseNode<T>
{
    public abstract T getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems);

    protected Action selectRandomAction(Collection<Action> actions)
    {
        return (Action) actions.toArray()[new Random().nextInt(actions.size())]; //return a random action
    }

    protected Action selectRandomPastAction(Set<Action> actions, MultiMap<String, Object> pastActions)
    {
        Object[] keys = pastActions.keySet().toArray();
        String key = (String) keys[new Random().nextInt(keys.length)];
        for(Action action : actions)
        {
            if(key.equals(action.get(Tags.AbstractIDCustom, null))) //return the action selected
                return action;
        }
        return null; //shouldn't ever happen
    }

    @Override
    public abstract String toString();
}