package oracle_objects;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.function.Predicate;

public class TestGrammarOracles
{
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    /**
     * ORACLE
     *      PROP (KEY,VALUE) EQUALS ('WebId','formButton'),
     * 	    PROP (KEY,VALUE) EQUALS ('WebTitle','Send payment')
     */
    @Test
    public void true_test_basic_oracle_property_values()
    {
        StateStub  state  = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        
        widget.set(Tags.Role, Roles.Button);
        widget.set(WdTags.WebId, "formButton");
        widget.set(WdTags.WebTitle, "Send payment");
    
        Predicate<State> isFormButtonPredicate = FunctionFactory.createPredicate
                (SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("formButton"));
        Predicate<State> isSendPaymentPredicate = FunctionFactory.createPredicate
                (SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("Send payment"));
        
        GrammarOracle oracle = new GrammarOracle( "test oracle");

        Predicate<State> jointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);
        oracle.setCheckLogic(jointPredicate);
        
        Assert.isTrue(oracle.getVerdict(state));
    }
    @Test
    public void false_test_basic_oracle_property_values()
    {
        StateStub  state  = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        
        widget.set(Tags.Role, Roles.Button);
        widget.set(WdTags.WebId, "formButton");
        widget.set(WdTags.WebTitle, "Send payment");
        
        Predicate<State> isFormButtonPredicate = FunctionFactory.createPredicate
                (SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("formButton"));
        Predicate<State> isSendPaymentPredicate = FunctionFactory.createPredicate
                (SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("Send")); // incomplete
        
        GrammarOracle oracle = new GrammarOracle( "test oracle");
        
        Predicate<State> jointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);
        oracle.setCheckLogic(jointPredicate);
        
        Assert.isFalse(oracle.getVerdict(state));
    }
}
