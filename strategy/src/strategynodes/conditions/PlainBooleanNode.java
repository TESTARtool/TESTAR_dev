package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;

import java.util.Set;

public class PlainBooleanNode extends BaseNode implements BooleanNode
{
    private final boolean VALUE;

    public PlainBooleanNode(boolean value)
    { this.VALUE = value; }

    @Override
    public Boolean getResult(State state, Set<Action> actions)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Boolean.toString(VALUE);
    }
}
