package oracle_objects;

import java.util.function.Predicate;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestPredicateFactory {

	/**
	 * WIDGET HAS PROP 'WebId'
	 */
	@Test
	public void test_HasFormButton_MatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		GrammarPredicate predicate = PredicateFactory.createPredicate("HAS", "WebId", null);
		Assertions.assertTrue(predicate.test(state));
	}
	@Test
	public void test_HasFormButton_UnmatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		GrammarPredicate predicate = PredicateFactory.createPredicate("HAS", "WeId", null);
		Assertions.assertFalse(predicate.test(state));
	}


	/**
	 * WIDGET IS PROP 'formButton'
	 */
	@Test
	public void test_IsFormButton_MatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		GrammarPredicate predicate = PredicateFactory.createPredicate("IS", "formButton", null);
		Assertions.assertTrue(predicate.test(state));
	}
	@Test
	public void test_IsFormButton_UnmatchPredicate() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");

		GrammarPredicate predicate = PredicateFactory.createPredicate("IS", "formBtton", null);
		Assertions.assertFalse(predicate.test(state));
	}

	/**
	 * WIDGET
	 *     PROP 'WebId' IS 'formButton',
	 *     PROP 'WebTitle' IS 'Send payment'
	 */
	@Test
	public void test_joint_property_values() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(WdTags.WebId, "formButton");
		widget.set(WdTags.WebTitle, "Send payment");

		GrammarPredicate isFormButtonPredicate = PredicateFactory.createPredicate("IS", "WebId", "formButton");
		GrammarPredicate isSendPaymentPredicate = PredicateFactory.createPredicate("IS", "WebTitle", "Send payment");

		Predicate<State> isJointPredicate = PredicateFactory.andPredicates(isFormButtonPredicate, isSendPaymentPredicate);

		Assertions.assertTrue(isJointPredicate.test(state));
	}

	/**
	 * WIDGET
	 *     PROP IS 'formButton',
	 *     PROP IS 'Send payment'
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

		GrammarPredicate isFormButtonPredicate = PredicateFactory.createPredicate("IS", "formButton", null);
		GrammarPredicate isSendPaymentPredicate = PredicateFactory.createPredicate("IS", "Send payment", null);

		Predicate<State> isJointPredicate = PredicateFactory.andPredicates(isFormButtonPredicate, isSendPaymentPredicate);

		Assertions.assertTrue(isJointPredicate.test(state));
	}
}
