package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.List;

@FunctionalInterface
public interface PredicateFunction
{
    boolean test(State state, List<String> args);
}
