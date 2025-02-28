package oracle_objects;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PredicateFactory
{
    private static final Map<String, PredicateFunction> predicateMap = new HashMap<>();
    
    static
    {
        predicateMap.put("key", PredicateFactory::stateHasWidgetWithTag);
        predicateMap.put("value", PredicateFactory::stateHasWidgetWithValue);
        predicateMap.put("any", PredicateFactory::stateHasWidgetWithTagOrValue);
        predicateMap.put("pair", PredicateFactory::stateHasWidgetWithTagAndValue);
        predicateMap.put("list", PredicateFactory::stateHasWidgetWithTagAndValue);
    }
    
    private PredicateFactory() {}
    
    public static GrammarPredicate createPredicate(String name, List<String> args)
    {
        if(args.isEmpty())
        {
            throw new IllegalArgumentException("Argument list of predicate " + name + " is empty");
        }
        PredicateFunction function = predicateMap.get(name);
        if (function == null)
        {
            throw new IllegalArgumentException("Unknown predicate function: " + name);
        }
        return new GrammarPredicate(function, args);
    }
    
    public static Predicate<State> andPredicates(Predicate<State> left, Predicate<State> right)
    {
        return left.and(right);
    }
    public static Predicate<State> orPredicates(Predicate<State> left, Predicate<State> right)
    {
        return left.or(right);
    }
    public static Predicate<State> xorPredicates(Predicate<State> left, Predicate<State> right)
    {
        return left.and(right.negate()).or(left.negate().and(right));
    }
    
    public static Predicate<State> equalPredicates(Predicate<State> left, Predicate<State> right)
    {
        return xorPredicates(left, right).negate(); // if both are true or both are false, return true
    }
    
    public static Predicate<State> negatePredicate(Predicate<State> predicate)
    {
        return predicate.negate();
    }
    
    
    private static boolean widgetHasTag(Widget widget, Tag<?> tag)
    {
        return widget.get(tag, null) != null;
    }
    
    private static boolean tagMatchesString(Tag<?> tag, String string)
    {
        return tag.name().equals(string);
    }
    
    private static boolean widgetHasTagWithThisValue(Widget widget, Tag<?> tag, String value)
    {
        return widget.get(tag).equals(value);
    }
    
    
    private static Boolean stateHasWidgetWithTag(State state, List<String> args)
    {
        String tagName = args.get(0);
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(widgetHasTag(widget, tag) && tagMatchesString(tag, tagName))
                    return true;
        
            }
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithValue(State state, List<String> args)
    {
        String value = args.get(0);
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(widgetHasTag(widget, tag) && widgetHasTagWithThisValue(widget, tag, value))
                    return true;
            }
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithTagAndValue(State state, List<String> args)
    {
        String tagName = args.get(0);
        String value = args.get(1);
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(widgetHasTag(widget, tag) && tag.name().equals(tagName) && widget.get(tag).equals(value))
                    return true;
            }
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithTagOrValue(State state, List<String> args)
    {
        String string = args.get(0);
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if((tag.name().equals(string) && widget.get(tag, null) != null) ||
                   (widget.get(tag, null) != null && widget.get(tag).equals(string)))
                    return true;
            }
        }
        return false;
    }
    
//    private static Boolean stateHasWidgetWithStringFromList(State state, List<String> args)
//    {
//        String type = args.get(0);
//        for(Widget widget : state)
//        {
//            for(Tag<?> tag : widget.tags())
//            {
//                switch(type)
//                {
//                    case "key":
//                        return widgetHasTag(widget, tag);
//                    case "value":
//                        return widgetHasValue(widget, args.get(1));
//                    case "any":
//                    default: return false;
//                }
//                if((tag.name().equals(string) && widget.get(tag, null) != null) ||
//                   (widget.get(tag, null) != null && widget.get(tag).equals(string)))
//            }
//        }
//    }
}

