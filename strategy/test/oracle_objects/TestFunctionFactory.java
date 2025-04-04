package oracle_objects;

import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.function.Predicate;

public class TestFunctionFactory
{

	/**
	 * WIDGET PROP KEY EQUALS 'WebId'
	 */
	@Test
	public void true_test_key_equals_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
        
       Predicate<State> predicate = FunctionFactory.createPredicate
			   (SearchLocation.KEY, SearchComparator.EQUALS, SearchTerm.single("WebId"));
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void false_test_key_equals_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.KEY, SearchComparator.EQUALS, SearchTerm.single("WeId")); //misspelled
		Assert.isFalse(predicate.test(state));
	}

	/**
	 * WIDGET PROP VALUE EQUALS 'formButton'
	 */
	@Test
	public void true_test_value_equals_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("formButton"));
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void false_test_value_equals_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.VALUE, SearchComparator.EQUALS, SearchTerm.single("formBtton")); //misspelled
		Assert.isFalse(predicate.test(state));
	}
	
	/**
	 * WIDGET PROP ANY CONTAINS 'button'
	 */
	@Test
	public void true_test_any_contains_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.ANY, SearchComparator.CONTAINS, SearchTerm.single("ton"));
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void false_test_any_contains_string()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.ANY, SearchComparator.CONTAINS, SearchTerm.single("name")); //not present
		Assert.isFalse(predicate.test(state));
	}
	
	/**
	 * PROP (KEY,VALUE) STARTS_WITH ('WebTitle','Send payment')
	 */
	@Test
	public void true_test_pair_starts_with()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebTitle, "Send payment");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.STARTS_WITH, SearchTerm.pair("Web", "Send"));
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void false_test_pair_starts_with()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebTitle, "Send payment");
		
		Predicate<State> predicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.STARTS_WITH, SearchTerm.pair("Web", "payment")); //no match for both
		Assert.isFalse(predicate.test(state));
	}
	
	/**
	 * WIDGET
	 *     PROP (KEY,VALUE) EQUALS ('WebId','formButton'),
	 *     PROP (KEY,VALUE) EQUALS ('WebTitle','Send payment')
	 */
	@Test
	public void true_test_joint_pair_equals()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");
		
		Predicate<State> isFormButtonPredicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.EQUALS, SearchTerm.pair("WebId", "formButton"));
		Predicate<State> isSendPaymentPredicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.EQUALS, SearchTerm.pair("WebTitle", "Send payment"));

		Predicate<State> isJointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);

		Assert.isTrue(isJointPredicate.test(state));
	}
	@Test
	public void false_test_joint_pair_equals()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		
		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");
		
		Predicate<State> isFormButtonPredicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.EQUALS, SearchTerm.pair("WebId", "formButton"));
		Predicate<State> isSendPaymentPredicate = FunctionFactory.createPredicate
				(SearchLocation.PAIR, SearchComparator.EQUALS, SearchTerm.pair("WebTitle", "Send")); //incomplete
		
		Predicate<State> isJointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);
		
		Assert.isFalse(isJointPredicate.test(state));
	}

	/**
	 * WIDGET
	 *     PROP VALUE EQUALS 'formButton',
	 *     PROP VALUE EQUALS 'Send payment'
	 */
	@Test
	public void true_test_joint_value_equals()
	{
		StateStub state = new StateStub();
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

		Predicate<State> isJointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);
		
		Assert.isTrue(isJointPredicate.test(state));
	}
	@Test
	public void false_test_joint_value_equals()
	{
		StateStub state = new StateStub();
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
		
		Predicate<State> isJointPredicate = isFormButtonPredicate.and(isSendPaymentPredicate);
		
		Assert.isFalse(isJointPredicate.test(state));
	}
}
