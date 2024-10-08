package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import java.util.Set;

public class StateChangedNode extends BaseNode implements BooleanNode
{
    @Override
    public Boolean getResult(State state, Set<Action> actions)
    {
        return state.get(Tags.StateChanged);
    }
    
    @Override
    public String toString() { return "state-changed"; }
}
