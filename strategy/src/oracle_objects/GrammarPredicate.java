package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.List;
import java.util.function.Predicate;

public class GrammarPredicate implements Predicate<State>
{
    final PredicateFunction predicateFunction;
    final List<String>            args;
//    final String            description;
    public GrammarPredicate(PredicateFunction predicateFunction, List<String> args)
    {
        this.predicateFunction = predicateFunction;
        this.args = args;
//        this.description = description;
    }
    
    @Override
    public boolean test(State state)
    {
        return predicateFunction.test(state, args);
    }

//    public Predicate<Boolean> getGrammarPredicate() { return predicate; }
//    @Override
//    public String toString() { return description; }

}