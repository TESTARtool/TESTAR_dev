package oracle_objects;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;

import java.util.ArrayList;
import java.util.HashMap;
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
        predicateMap.put("list", PredicateFactory::stateHasWidgetWithStringFromList);
    }
    
    private PredicateFactory() {}
    
    public static GrammarPredicate createPredicate(String name, Map<String, String> args)
    {
        if(args.isEmpty())
            throw new IllegalArgumentException("Argument list of predicate " + name + " is empty");
        
        PredicateFunction function = predicateMap.get(name);
        if(function == null)
            throw new IllegalArgumentException("Unknown predicate function: " + name);
        
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
    
    // helper methods
    
    private static boolean widgetHasTag(Widget widget, String tagName)
    {
        for(Tag<?> tag : widget.tags())
        {
            if(tagNameMatchesString(tag, tagName))
                return true;
        }
        return false;
    }
    
    private static boolean widgetHasValue(Widget widget, String value)
    {
        for(Tag<?> tag : widget.tags())
        {
            if(widget.get(tag, null).equals(value))
                return true;
        }
        return false;
    }
    
    private static boolean tagNameMatchesString(Tag<?> tag, String string)
    {
        return tag.name().equals(string);
    }
    
    private static boolean widgetHasTagValuePair(Widget widget, String tagName, String value)
    {
        for(Tag<?> tag : widget.tags())
        {
            if(tagNameMatchesString(tag, tagName) && widget.get(tag).equals(value))
                return true;
        }
        return false;
    }
    
    // predicate methods
    
    private static Boolean stateHasWidgetWithTag(State state, Map<String, String> args)
    {
        String tagName = args.getOrDefault("key", null);
        if(tagName == null)
            throw new IllegalArgumentException();
        
        for(Widget widget : state)
        {
            if(widgetHasTag(widget, tagName))
                return true;
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithValue(State state, Map<String, String> args)
    {
        String value = args.getOrDefault("value", null);
        if(value == null)
            throw new IllegalArgumentException();
        
        for(Widget widget : state)
        {
            for(Tag<?> tag : widget.tags())
            {
                if(widget.get(tag).equals(value))
                    return true;
            }
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithTagAndValue(State state, Map<String, String> args)
    {
        String tagName = args.getOrDefault("key", null);
        String value   = args.getOrDefault("value", null);
        
        if(tagName == null || value == null)
            throw new IllegalArgumentException();
    
        for(Widget widget : state)
        {
            if(widgetHasTagValuePair(widget, tagName, value))
                return true;
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithTagOrValue(State state, Map<String, String> args)
    {
        String string = args.getOrDefault("any", null);
    
        if(string == null)
            throw new IllegalArgumentException();
    
        for(Widget widget : state)
        {
            if(widgetHasTag(widget, string) || widgetHasValue(widget, string))
                return true;
        }
        return false;
    }
    
    private static Boolean stateHasWidgetWithStringFromList(State state, Map<String,String> args)
    {
        String            type      = args.getOrDefault("list", null); // key, value, or any
        ArrayList<String> options   = new ArrayList<>(args.values());
    
        if(type == null || args.size() <= 1)
            throw new IllegalArgumentException();
    
        options.remove(type); // remove the keyword as an option
    
        for(Widget widget : state)
        {
            for(String option : options)
            {
                if(
                        (type.equals("key") && widgetHasTag(widget, option)) ||
                        (type.equals("value") && widgetHasValue(widget, option)) ||
                        (type.equals("any") && (widgetHasTag(widget, option) || widgetHasValue(widget, option)))
                )
                    return true;
            }
        }
        return false;
    }
}