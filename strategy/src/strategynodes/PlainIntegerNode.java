package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class PlainIntegerNode extends BaseIntegerNode
{
    private final int VALUE;

    public PlainIntegerNode(int value) { VALUE = value; }

    @Override
    public Integer getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Integer.toString(VALUE);
    }
}
