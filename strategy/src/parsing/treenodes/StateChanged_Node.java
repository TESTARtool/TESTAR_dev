package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class StateChanged_Node extends BaseBooleanNode
{
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return state.get(Tags.StateChanged);
    }
    
    @Override
    public String toString() { return "state-changed"; }
}
