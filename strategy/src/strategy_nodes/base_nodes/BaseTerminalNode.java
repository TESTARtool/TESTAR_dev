package strategy_nodes.base_nodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public abstract class BaseTerminalNode<T> extends BaseStrategyNode<T>
{
    @Override
    public abstract T GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
    @Override
    public abstract String toString();
    
    public abstract int GetValue();
}
