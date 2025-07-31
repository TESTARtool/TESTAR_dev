package org.testar.statemodel.analysis.condition;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.stub.StateStub;

import static org.junit.Assert.*;

public class TransitionConditionEvaluatorTest {

    @Test
    public void test_empty_transition_condition() {
        String testGoal = "Perform this action";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_missing_fields_transition_condition() {
        String testGoal = "Origin: StateA\nAction: ClickButton"; // No Dest State
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("The number of transition conditions should be zero if one field is missing.", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_valid_transition_condition_loaded() {
        String testGoal = "Origin: StateA\nAction: ClickButton\nDest: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("Exactly one transition condition should be loaded from the goal string.", 
                1, evaluator.getConditions().size());

        assertTrue(evaluator.getConditions().get(0) instanceof StateTransitionCondition);

        StateTransitionCondition stateTransitionCondition = ((StateTransitionCondition)evaluator.getConditions().get(0));

        assertEquals("StateA", stateTransitionCondition.getOriginStateMessage());
        assertEquals("ClickButton", stateTransitionCondition.getActionMessage());
        assertEquals("StateB", stateTransitionCondition.getDestStateMessage());

        assertFalse("The state evaluator must be false due pending transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_multiple_origin_action_dest_only_first_used() {
        String testGoal = "Origin: StateX\nAction: ClickButton\nDest: StateY\n" +
                "Origin: StateA\nAction: OtherAction\nDest: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("Only the first complete Origin/Action/Dest block should be used.", 
                1, evaluator.getConditions().size());

        assertTrue(evaluator.getConditions().get(0) instanceof StateTransitionCondition);

        StateTransitionCondition stateTransitionCondition = ((StateTransitionCondition)evaluator.getConditions().get(0));

        assertEquals("StateX", stateTransitionCondition.getOriginStateMessage());
        assertEquals("ClickButton", stateTransitionCondition.getActionMessage());
        assertEquals("StateY", stateTransitionCondition.getDestStateMessage());

        assertFalse("The state evaluator must be false due pending transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_empty_goal_transition_condition() {
        String testGoal = "";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_null_goal_transition_condition() {
        String testGoal = null;
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, Tags.Title, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_null_originTag_transition_condition() {
        String testGoal = "Origin: StateA\nAction: ClickButton\nDest: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                null, Tags.Title, Tags.Title, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_null_actionTag_transition_condition() {
        String testGoal = "Origin: StateA\nAction: ClickButton\nDest: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, null, Tags.Title, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }

    @Test
    public void test_null_destTag_transition_condition() {
        String testGoal = "Origin: StateA\nAction: ClickButton\nDest: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(
                Tags.Title, Tags.Title, null, testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());

        assertTrue("The state evaluator must be true for empty transition conditions", 
                evaluator.evaluateConditions(new StateStub()));
    }
}
