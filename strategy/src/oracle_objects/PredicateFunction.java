package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.Map;

@FunctionalInterface
public interface PredicateFunction
{
    boolean test(State state, Map<String, String> args);
}
