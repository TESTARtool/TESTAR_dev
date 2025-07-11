package org.testar.statemodel.actionselector;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ModelManager;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.DummyManager;
import org.testar.statemodel.sequence.SequenceManager;

public class TestImprovedUnvisitedActionSelector {

	private AbstractAction firstAction = new AbstractAction("firstAction");
	private AbstractAction secondAction = new AbstractAction("secondAction");
	private AbstractAction thirdAction = new AbstractAction("thirdAction");

	private AbstractState firstState = new AbstractState("firstState", Collections.singleton(firstAction));
	private AbstractState secondState = new AbstractState("secondState", Collections.singleton(secondAction));
	private AbstractState thirdState = new AbstractState("thirdState", Collections.singleton(thirdAction));

	private AbstractStateModel abstractStateModel;
	private ActionSelector actionSelector;
	private ModelManager modelManager;

	@Before
	public void initialActionSelector() {
		// reset the action selector for each test
		actionSelector = new ImprovedUnvisitedActionSelector();
		// reset the abstract state model for each test
		abstractStateModel = new AbstractStateModel("firstModel", "", "", Collections.singleton(Tags.AbstractID));
		// reset the model manager for each test
		modelManager = new ModelManager(abstractStateModel, actionSelector, new DummyManager(), new SequenceManager(new HashSet<>(), "firstModel"), false);
	}

	@Test
	public void test_new_empty_state_model() {
		// We are in a new model without transitions
		// We need to test that firstAction is an Unvisited action and is obtains with the action selector

		Assert.assertTrue(firstState.getVisitedActions().isEmpty());

		Assert.assertTrue(firstState.getUnvisitedActions().size() == 1);
		Assert.assertTrue(firstState.getUnvisitedActions().iterator().next().equals(firstAction));

		AbstractAction selectedAction = null;
		try {
			selectedAction = actionSelector.selectAction(firstState, abstractStateModel);
		} catch (ActionNotFoundException e) {
			e.printStackTrace();
		}

		Assert.assertTrue(selectedAction != null);
		Assert.assertTrue(selectedAction.equals(firstAction));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_transition_path() {
		// Fake that two transition exists
		// S1 -> A1 -> S2 -> A2 -> S3
		// To test that first and second states do not contain unvisited actions
		// And that the action selector returns a path of actions
		try {
			abstractStateModel.addTransition(firstState, secondState, firstAction);
			abstractStateModel.addTransition(secondState, thirdState, secondAction);
		} catch (StateModelException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		Assert.assertTrue(!firstState.getVisitedActions().isEmpty());
		Assert.assertTrue(firstState.getVisitedActions().iterator().next().equals(firstAction));
		Assert.assertTrue(firstState.getUnvisitedActions().isEmpty());

		Assert.assertTrue(!secondState.getVisitedActions().isEmpty());
		Assert.assertTrue(secondState.getVisitedActions().iterator().next().equals(secondAction));
		Assert.assertTrue(secondState.getUnvisitedActions().isEmpty());

		Assert.assertTrue(thirdState.getUnvisitedActions().size() == 1);
		Assert.assertTrue(thirdState.getUnvisitedActions().iterator().next().equals(thirdAction));

		AbstractAction selectedAction = null;
		try {
			selectedAction = actionSelector.selectAction(firstState, abstractStateModel);
		} catch (ActionNotFoundException e) {
			e.printStackTrace();
		}

		// When first action is selected, it is also removed from the executionPath list
		Assert.assertTrue(selectedAction != null);
		Assert.assertTrue(selectedAction.equals(firstAction));

		LinkedList<AbstractAction> executionPath = new LinkedList<>();
		try {
			Field executionPathField = actionSelector.getClass().getDeclaredField("executionPath");
			executionPathField.setAccessible(true);
			executionPath = (LinkedList<AbstractAction>) executionPathField.get(actionSelector);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		// Verify that first action was removed and two next actions remain
		Assert.assertTrue(executionPath.size() == 2);

		Assert.assertTrue(executionPath.stream().noneMatch(action -> action.getActionId().equals(firstAction.getActionId())));

		Assert.assertTrue(executionPath.stream().anyMatch(action -> action.getActionId().equals(secondAction.getActionId())));
		Assert.assertTrue(executionPath.stream().anyMatch(action -> action.getActionId().equals(thirdAction.getActionId())));
	}

	@Test
	public void test_action_not_found() {
		// Fake a circular model with all actions executed
		// S1 -> A1 -> S2 -> A2 -> S1
		// To test that the action selector does not find any Unvisited action
		try {
			abstractStateModel.addTransition(firstState, secondState, firstAction);
			abstractStateModel.addTransition(secondState, firstState, secondAction);
		} catch (StateModelException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		AbstractAction selectedAction = null;
		Exception exception = null;
		try {
			selectedAction = actionSelector.selectAction(firstState, abstractStateModel);
		} catch (ActionNotFoundException e) {
			exception = e;
		}

		Assert.assertTrue(selectedAction == null);
		Assert.assertTrue(exception instanceof ActionNotFoundException);
	}

	@Test
	public void test_new_system_sequence() {
		// Fake that two transition exists
		// S1 -> A1 -> S2 -> A2 -> S3
		try {
			abstractStateModel.addTransition(firstState, secondState, firstAction);
			abstractStateModel.addTransition(secondState, thirdState, secondAction);
		} catch (StateModelException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		AbstractAction selectedAction = null;
		try {
			// We are in the main state and we calculate a path of actions
			selectedAction = actionSelector.selectAction(firstState, abstractStateModel);
			// But the SUT was restarted because TESTAR started a new sequence
			modelManager.notifyTestSequencedStarted();
			selectedAction = actionSelector.selectAction(firstState, abstractStateModel);
		} catch (ActionNotFoundException e) {
			e.printStackTrace();
		}

		// Check that first action was selected, but that the flow was not altered

		Assert.assertTrue(selectedAction.getActionId().equals(firstAction.getActionId()));

		int nrOfFlowAlterations = -1;
		try {
			Field nrOfFlowAlterationsField = actionSelector.getClass().getDeclaredField("nrOfFlowAlterations");
			nrOfFlowAlterationsField.setAccessible(true);
			nrOfFlowAlterations = (int) nrOfFlowAlterationsField.get(actionSelector);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Assert.assertTrue(nrOfFlowAlterations == 0);
	}

}
