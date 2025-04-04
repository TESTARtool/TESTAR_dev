package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FunctionFactory
{
    private FunctionFactory() {}
    
    public static Predicate<State> createPredicate
            (SearchLocation location, SearchComparator comparator, SearchTerm searchTerm)
    {
        validateSearchTerm(location, comparator, searchTerm);
        
        switch (location)
        {
            case KEY:
                return createKeyPredicate(comparator, searchTerm);
            case VALUE:
                return createValuePredicate(comparator, searchTerm);
            case ANY:
                return createAnyPredicate(comparator, searchTerm);
            case PAIR:
                return createPairPredicate(comparator, searchTerm);
            default:
                throw new IllegalArgumentException("Unsupported search location: " + location);
        }
    }
    
    // make sure that the location-comparator-term(s) combination is allowed
    private static void validateSearchTerm(SearchLocation location, SearchComparator comparator, SearchTerm searchTerm)
    {
        // PAIR searches must have exactly two values (key, value)
        if (location == SearchLocation.PAIR && !searchTerm.isPair())
            throw new IllegalArgumentException("PAIR search requires exactly two values: a key and a value.");
        
        // Lists are allowed for everything except PAIR
        if (searchTerm.isList() && location == SearchLocation.PAIR)
            throw new IllegalArgumentException("Lists are not allowed for PAIR searches.");
        
        // Booleans, integers, lists, and ranges can only use IS
        if ((searchTerm.isTypeBoolean() || searchTerm.isTypeInteger() || searchTerm.isRange() || searchTerm.isList())
            && comparator != SearchComparator.IS)
            throw new IllegalArgumentException("Only IS comparator is allowed for booleans, integers, and ranges.");
    }
    
    private static Predicate<State> createKeyPredicate(SearchComparator comparator, SearchTerm searchTerm)
    {
        return state -> GrammarHelperWidget.getWidgetsOfState(state).stream()
                .anyMatch(widget -> compare(comparator, GrammarHelperWidget.getWidgetKeys(widget), searchTerm));
    }
    
    private static Predicate<State> createValuePredicate(SearchComparator comparator, SearchTerm searchTerm)
    {
        return state -> GrammarHelperWidget.getWidgetsOfState(state).stream()
                .anyMatch(widget -> compare(comparator, GrammarHelperWidget.getWidgetValues(widget), searchTerm));
    }
    
    private static Predicate<State> createAnyPredicate(SearchComparator comparator, SearchTerm searchTerm)
    {
        return state -> GrammarHelperWidget.getWidgetsOfState(state).stream()
                .anyMatch(widget -> compare(comparator, GrammarHelperWidget.getWidgetKeys(widget), searchTerm) ||
                                    compare(comparator, GrammarHelperWidget.getWidgetValues(widget), searchTerm));
    }
    
    private static Predicate<State> createPairPredicate(SearchComparator comparator, SearchTerm searchTerm)
    {
        String keyTerm = searchTerm.getKey();
        String valueTerm = searchTerm.getValue();

        BiPredicate<String, String> pairPredicate = createPairPredicate(comparator, keyTerm, valueTerm);

        return state -> GrammarHelperWidget.getWidgetsOfState(state).stream()
                .anyMatch(widget -> GrammarHelperWidget.getWidgetPairs(widget).stream()
                        .anyMatch(pair -> pairPredicate.test(pair.left(), pair.right())));
    }
    
    private static BiPredicate<String, String> createPairPredicate(SearchComparator comparator, String keyTerm, String valueTerm)
    {
        switch (comparator)
        {
            case IS:
            case EQUALS:
                return (key, value) -> key.equals(keyTerm) && value.equals(valueTerm);
            case MATCHES:
                return (key, value) -> Pattern.matches(keyTerm, key) && Pattern.matches(valueTerm, value);
            case CONTAINS:
                return (key, value) -> key.contains(keyTerm) && value.contains(valueTerm);
            case STARTS_WITH:
                return (key, value) -> key.startsWith(keyTerm) && value.startsWith(valueTerm);
            case ENDS_WITH:
                return (key, value) -> key.endsWith(keyTerm) && value.endsWith(valueTerm);
            default:
                throw new IllegalArgumentException("Unknown comparator: " + comparator);
        }
    }
    
    private static boolean compare(SearchComparator comparator, List<String> stringsToCheck, SearchTerm searchTerm)
    {
        if (searchTerm.isSingle())
        {
            String term = searchTerm.getSingleValueAsString();
            switch (comparator)
            {
                case IS:
                case EQUALS:
                    return stringsToCheck.contains(term);
                case MATCHES:
                    return stringsToCheck.stream().anyMatch(input -> Pattern.matches(term, input));
                case CONTAINS:
                    return stringsToCheck.stream().anyMatch(input -> input.contains(term));
                case STARTS_WITH:
                    return stringsToCheck.stream().anyMatch(input -> input.startsWith(term));
                case ENDS_WITH:
                    return stringsToCheck.stream().anyMatch(input -> input.endsWith(term));
                default:
                    throw new IllegalArgumentException("Unknown comparator: " + comparator);
            }
        }
        else if (searchTerm.isList())
            return stringsToCheck.stream().anyMatch(searchTerm::isInList);
        if (searchTerm.isRange())
            return stringsToCheck.stream().anyMatch(searchTerm::isInRange);
        throw new IllegalArgumentException("Invalid search term format.");
    }
}


