package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.function.Predicate;

public class GrammarPredicate implements Predicate<State>
{
    final PredicateFunction predicateFunction;
    final String            arg1;
    final String            arg2;
//    final String            description;
    public GrammarPredicate(PredicateFunction predicateFunction, String arg1, String arg2)
    {
        this.predicateFunction = predicateFunction;
        this.arg1 = arg1;
        this.arg2 = arg2;
//        this.description = description;
    }
    
    @Override
    public boolean test(State state)
    {
        return predicateFunction.test(state, arg1, arg2);
    }

//    public Predicate<Boolean> getGrammarPredicate() { return predicate; }
//    @Override
//    public String toString() { return description; }

}