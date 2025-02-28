package oracle_objects;

import org.junit.Ignore;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestPredicateFactory {

	/**
	 * WIDGET PROP KEY IS 'WebId'
	 */
	@Test
	public void test_HasFormButton_MatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
        
        List<String> args = new ArrayList<>();
        args.add("WebId");

		GrammarPredicate predicate = PredicateFactory.createPredicate("key", args);
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void test_HasFormButton_UnmatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
        
        List<String> args = new ArrayList<>();
        args.add("WeId"); //misspelled

		GrammarPredicate predicate = PredicateFactory.createPredicate("key", args);
		Assert.isFalse(predicate.test(state));
	}


	/**
	 * WIDGET PROP VALUE IS 'formButton'
	 */
	@Test
	public void test_IsFormButton_MatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
        
        List<String> args = new ArrayList<>();
        args.add("formButton");
        
		GrammarPredicate predicate = PredicateFactory.createPredicate("value", args);
		Assert.isTrue(predicate.test(state));
	}
	@Test
	public void test_IsFormButton_UnmatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
        
        List<String> args = new ArrayList<>();
        args.add("formBtton");//misspelled
        
        GrammarPredicate predicate = PredicateFactory.createPredicate("value", args);
		Assert.isFalse(predicate.test(state));
	}

	/**
	 * WIDGET
	 *     PROP (KEY,VALUE) IS ('WebId','formButton'),
	 *     PROP (KEY,VALUE) IS ('WebTitle','Send payment')
	 */
	@Ignore @Test
	public void test_joint_property_values() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");
        
        List<String> args1 = new ArrayList<>();
        args1.add("WebId");
        args1.add("formButton");
        List<String> args2 = new ArrayList<>();
        args2.add("WebTitle");
        args2.add("Send payment");

		GrammarPredicate isFormButtonPredicate = PredicateFactory.createPredicate("pair", args1);
		GrammarPredicate isSendPaymentPredicate = PredicateFactory.createPredicate("pair", args2);

		Predicate<State> isJointPredicate = PredicateFactory.andPredicates(isFormButtonPredicate, isSendPaymentPredicate);

		Assert.isTrue(isJointPredicate.test(state)); //todo: fix function
	}

	/**
	 * WIDGET
	 *     PROP VALUE IS 'formButton',
	 *     PROP VALUE IS 'Send payment'
	 */
	@Test
	public void test_joint_values() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");
        
        List<String> args1 = new ArrayList<>();
        args1.add("formButton");
        List<String> args2 = new ArrayList<>();
        args2.add("Send payment");
        
		GrammarPredicate isFormButtonPredicate = PredicateFactory.createPredicate("value", args1);
		GrammarPredicate isSendPaymentPredicate = PredicateFactory.createPredicate("value", args2);

		Predicate<State> isJointPredicate = PredicateFactory.andPredicates(isFormButtonPredicate, isSendPaymentPredicate);

		Assert.isTrue(isJointPredicate.test(state));
	}

	/**
	 * ORACLE
     *      PROP (KEY,VALUE) IS ('WebId','formButton'),
     * 	    PROP (KEY,VALUE) IS ('WebTitle','Send payment')
	 */
	@Test
	public void test_basic_oracle_property_values()
	{
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");
        
        List<String> args1 = new ArrayList<>();
        args1.add("WebId");
        args1.add("formButton");
        List<String> args2 = new ArrayList<>();
        args2.add("WebTitle");
        args2.add("Send payment");

		GrammarOracle oracle = new GrammarOracle(1, "test oracle");
        
        GrammarPredicate isFormButtonPredicate = PredicateFactory.createPredicate("pair", args1);
        GrammarPredicate isSendPaymentPredicate = PredicateFactory.createPredicate("pair", args2);

		Predicate<State> jointPredicate = PredicateFactory.andPredicates(isFormButtonPredicate, isSendPaymentPredicate);
		oracle.setCheckLogic(jointPredicate);

		Assert.isTrue(oracle.verdict(state));
	}

}
