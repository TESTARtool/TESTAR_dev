package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.*;

public abstract class BaseBooleanNode extends BaseNode<Boolean>
{
    @Override
    public abstract Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
}
