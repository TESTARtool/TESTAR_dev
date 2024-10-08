package strategynodes;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.Set;

public interface StrategyNode<T>
{
    T getResult(State state, Set<Action> actions);
    String toString();
}
