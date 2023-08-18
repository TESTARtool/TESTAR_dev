package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class PlainBooleanNode extends BaseBooleanNode
{
    private final boolean VALUE;

    public PlainBooleanNode(boolean value) { VALUE = value; }

    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted,
                             ArrayList<String> operatingSystems)
    { return VALUE; }

    @Override
    public String toString()
    {
        return Boolean.toString(VALUE);
    }
}
