package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;

import java.util.ArrayList;
import java.util.Set;

public class PlainBooleanNode extends BaseNode<Boolean>
{
    private final boolean VALUE;

    public PlainBooleanNode(boolean value)
    { this.VALUE = value; }

    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Boolean.toString(VALUE);
    }
}
