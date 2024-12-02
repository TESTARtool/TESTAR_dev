package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public interface StrategyNode<T>
{
    T getResult(State state, Set<Action> actions);
    String toString();
}
