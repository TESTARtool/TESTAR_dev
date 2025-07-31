package org.testar.statemodel.analysis.condition;

import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestCheckConditionEvaluator {

	@Test
	public void test_empty_check_condition() {
		String testGoal = "Perform this action";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 0, "The number of identified check conditions should be zero");

		Assert.isTrue(checkEvaluator.evaluateConditions(new StateStub()), "The state evaluator must be true for empty check conditions");
	}

	@Test
	public void test_single_check_condition_loaded_message() {
		String testGoal = "Perform this action \n Check: this message is shown";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 1, "The number of identified check conditions is not correct");

		Assert.isTrue(checkEvaluator.getConditions().get(0) instanceof StateCondition, "The StateCondition check object is not correct");

		Assert.isEquals(((StateCondition)checkEvaluator.getConditions().get(0)).getSearchMessage(), 
				"this message is shown", 
				"The identified check condition message is not correct");
	}

	@Test
	public void test_single_check_condition_match() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		widget.set(Tags.Title, "this message is shown to the user");
		state.addChild(widget);
		widget.setParent(state);

		String testGoal = "Perform this action \n Check: this message is shown";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isTrue(checkEvaluator.evaluateConditions(state), "The state evaluator must match the single check condition");
	}

	@Test
	public void test_single_check_condition_unmatch() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		widget.set(Tags.Title, "this is not a matching title");
		state.addChild(widget);
		widget.setParent(state);

		String testGoal = "Perform this action \n Check: this message is shown";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isTrue(!checkEvaluator.evaluateConditions(state), "The state evaluator must unmatch different check condition");
	}

	@Test
	public void test_single_check_condition_unmatch_nonvisible() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		widget.set(WdTags.WebTitle, "this message is shown but the widget is non visible");
		widget.set(WdTags.WebIsFullOnScreen, false);
		state.addChild(widget);
		widget.setParent(state);

		String testGoal = "Perform this action \n Check: this message is shown";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(WdTags.WebTitle, testGoal);

		Assert.isTrue(!checkEvaluator.evaluateConditions(state), "The state evaluator must unmatch for non visible widgets");
	}

	@Test
	public void test_multiple_check_conditions_loaded_messages() {
		String testGoal = "Perform this action \n Check: this message is shown \n Check: also a second message";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(WdTags.WebInnerHTML, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 2, "The number of identified check conditions is not correct");

		Assert.isTrue(checkEvaluator.getConditions().get(0) instanceof StateCondition, "The StateCondition check object is not correct");
		Assert.isTrue(checkEvaluator.getConditions().get(1) instanceof StateCondition, "The StateCondition check object is not correct");

		Assert.isEquals(((StateCondition)checkEvaluator.getConditions().get(0)).getSearchMessage(), 
				"this message is shown", 
				"The first identified check condition message is not correct");

		Assert.isEquals(((StateCondition)checkEvaluator.getConditions().get(1)).getSearchMessage(), 
				"also a second message", 
				"The second identified check condition message is not correct");
	}

	@Test
	public void test_multiple_check_conditions_match() {
		StateStub state = new StateStub();
		WidgetStub first_widget = new WidgetStub();
		first_widget.set(WdTags.WebInnerHTML, "this message is shown in the html tag");
		first_widget.set(WdTags.WebIsFullOnScreen, true);
		state.addChild(first_widget);
		first_widget.setParent(state);

		WidgetStub second_widget = new WidgetStub();
		second_widget.set(WdTags.WebInnerHTML, "this is also a second message");
		second_widget.set(WdTags.WebIsFullOnScreen, true);
		state.addChild(second_widget);
		second_widget.setParent(state);

		String testGoal = "Perform this action \n Check: this message is shown \n Check: also a second message";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(WdTags.WebInnerHTML, testGoal);

		Assert.isTrue(checkEvaluator.evaluateConditions(state), "The state evaluator must match the multiple check conditions");
	}

	@Test
	public void test_multiple_check_conditions_unmatch() {
		StateStub state = new StateStub();
		WidgetStub first_widget = new WidgetStub();
		first_widget.set(WdTags.WebInnerHTML, "this message is shown in the html tag");
		state.addChild(first_widget);
		first_widget.setParent(state);

		WidgetStub second_widget = new WidgetStub();
		second_widget.set(WdTags.WebInnerHTML, "this is not a matching message");
		state.addChild(second_widget);
		second_widget.setParent(state);

		String testGoal = "Perform this action \n Check: this message is shown \n Check: also a second message";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(WdTags.WebInnerHTML, testGoal);

		Assert.isTrue(!checkEvaluator.evaluateConditions(state), "The state evaluator must unmatch one of the different multiple check conditions");
	}

	@Test
	public void test_empty_test_goal_check_condition() {
		String testGoal = "";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 0, "The number of identified check conditions should be zero");

		Assert.isTrue(checkEvaluator.evaluateConditions(new StateStub()), "The state evaluator must be true for empty check conditions");
	}

	@Test
	public void test_null_test_goal_check_condition() {
		String testGoal = null;
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(Tags.Title, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 0, "The number of identified check conditions should be zero");

		Assert.isTrue(checkEvaluator.evaluateConditions(new StateStub()), "The state evaluator must be true for empty check conditions");
	}

	@Test
	public void test_null_evaluatorTag_check_condition() {
		String testGoal = "Perform this action \n Check: this message is shown";
		CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(null, testGoal);

		Assert.isEquals(checkEvaluator.getConditions().size(), 0, "The number of identified check conditions should be zero");

		Assert.isTrue(checkEvaluator.evaluateConditions(new StateStub()), "The state evaluator must be true for empty check conditions");
	}
}
