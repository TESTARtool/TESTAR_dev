package oracle_objects;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PredicateFactory
{
    private static final Map<String, PredicateFunction> predicateMap = new HashMap<>();
    
    static
    {
        predicateMap.put("HAS", (state, arg1, arg2) -> stateHasWidgetWithTag(state, arg1));
        predicateMap.put("IS", (state, arg1, arg2) -> stateHasWidgetWithValue(state, arg1));
    }
    
    private PredicateFactory() {}
    
    public static GrammarPredicate createPredicate(String name, String arg1, String arg2)
    {
        PredicateFunction function = predicateMap.get(name);
        if (function == null)
        {
            throw new IllegalArgumentException("Unknown predicate function: " + name);
        }
        return new GrammarPredicate(function, arg1, arg2);
    }
    
    public static Predicate<State> andPredicates(Predicate<State> left, Predicate<State> right)
    {
        return left.and(right);
    }
    public static Predicate<State> orPredicate(Predicate<State> left, Predicate<State> right)
    {
        return left.or(right);
    }
    public static Predicate<State> xorPredicates(Predicate<State> left, Predicate<State> right)
    {
        return left.and(right.negate()).or(left.negate().and(right));
    }
    
    public static Predicate<State> negatePredicate(Predicate<State> predicate)
    {
        return predicate.negate();
    }
    
    
    private static Boolean stateHasWidgetWithTag(State state, String tagName)
    {
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(tag.name().equals(tagName) && widget.get(tag, null) != null)
                    return true;
        
            }
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithValue(State state, Object value)
    {
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(widget.get(tag, null) != null && widget.get(tag).equals(value))
                {
                    return true;
                }
            }
        }
        return false;
    }
}

