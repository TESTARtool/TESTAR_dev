package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class StateChangedNode extends BaseBooleanNode
{
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return state.get(Tags.StateChanged);
    }
    
    @Override
    public String toString() { return "state-changed"; }
}
