package oracle_objects;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testar.monkey.Assert;

public class TestGrammarPredicates
{
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void true_test_search_term_class_valid_range()
    {
        SearchTerm validRange = SearchTerm.range("10", "20");
        Assert.isTrue(validRange.isRange());
    }
    
    @Test
    public void error_test_search_term_class_range_invalid()
    {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Range bounds must be valid integers.");
    
        SearchTerm invalidRange  = SearchTerm.range("abc", "20"); // error
    }
    
    @Test
    public void error_test_search_term_class_range_reversed()
    {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Lower bound cannot be greater than upper bound.");

        SearchTerm reversedRange = SearchTerm.range("30", "10"); // error
    }
    
    @Test
    public void true_test_search_term_class_is_single_value()
    {
        SearchTerm singleKey = SearchTerm.single("exampleKey");
        Assert.isTrue(singleKey.isSingle());
    }
    
    @Test
    public void true_test_search_term_class_key_value_pair()
    {
        SearchTerm pair = SearchTerm.pair("user", "admin");
        Assert.isTrue(pair.isPair());
    }
}
