package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;

import java.util.ArrayList;
import java.util.Set;

public class StateChangedNode extends BaseNode<Boolean>
{
    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        return state.get(Tags.StateChanged);
    }
    
    @Override
    public String toString() { return "state-changed"; }
}
