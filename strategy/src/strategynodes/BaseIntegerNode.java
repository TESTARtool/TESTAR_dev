package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public abstract class BaseIntegerNode extends BaseNode<Integer>
{
    @Override
    public abstract Integer getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted,
                                      ArrayList<String> operatingSystems);
}
