package oracle_objects;

import org.testar.monkey.alayer.State;

@FunctionalInterface
public interface PredicateFunction
{
    boolean test(State state, String arg1, String arg2);
}
