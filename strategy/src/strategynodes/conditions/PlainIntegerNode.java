package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;

import java.util.ArrayList;
import java.util.Set;

public class PlainIntegerNode extends BaseNode implements IntegerNode
{
    private final int VALUE;

    public PlainIntegerNode(int value)
    {  this.VALUE = Math.max(value, 0); }

    @Override
    public Integer getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Integer.toString(VALUE);
    }
}
