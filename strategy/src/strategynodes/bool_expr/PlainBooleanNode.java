package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class PlainBooleanNode extends BaseStrategyNode<Boolean>
{
    private final boolean VALUE;

    public PlainBooleanNode(boolean value)
    { VALUE = value; }

    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Boolean.toString(VALUE);
    }
}
