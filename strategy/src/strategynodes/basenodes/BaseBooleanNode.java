package strategynodes.basenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public abstract class BaseBooleanNode extends BaseStrategyNode<Boolean>
{
    @Override
    public abstract Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
    @Override
    public abstract String toString();
}
