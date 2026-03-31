package org.testar.action.selector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testar.ActionSelectorProxy;
import org.testar.PrioritizeNewActionsSelector;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.simplestategraph.GuiStateGraphWithVisitedActions;
import org.testar.simplestategraph.QLearningActionSelector;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestActionSelector {

	@Rule
	public RepeatRule repeatRule = new RepeatRule();

	private static StateStub state;
	private static Set<Action> originalActions;
	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	private static List<Action> firstSelectedActionFromPrioritizeSelector = new ArrayList<>();
	private static List<Action> firstSelectedActionFromGuiGraphSelector = new ArrayList<>();
	private static List<Action> firstSelectedActionFromQLearningSelector = new ArrayList<>();

	@BeforeClass
	public static void setup() {
		state = new StateStub();
		state.set(Tags.AbstractID, "stateAbstractID");
		originalActions = new HashSet<>();

		// First action-widget
		WidgetStub firstWidget = new WidgetStub();
		state.addChild(firstWidget);
		firstWidget.setParent(state);

		firstWidget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		firstWidget.set(Tags.Role, Roles.Button);
		firstWidget.set(Tags.Path, "[0,0,1]");
		firstWidget.set(Tags.Desc, "firstWidget");
		firstWidget.set(Tags.ConcreteID, "firstWidgetConcreteID");
		firstWidget.set(Tags.AbstractID, "firstWidgetAbstractID");

		Action firstAction = ac.leftClickAt(firstWidget);
		firstAction.set(Tags.ConcreteID, "firstActionConcreteID");
		firstAction.set(Tags.AbstractID, "firstActionAbstractID");
		originalActions.add(firstAction);

		// Second action-widget
		WidgetStub secondWidget = new WidgetStub();
		state.addChild(secondWidget);
		secondWidget.setParent(state);

		secondWidget.set(Tags.Shape, Rect.fromCoordinates(2, 2, 2, 2));
		secondWidget.set(Tags.Role, Roles.Button);
		secondWidget.set(Tags.Path, "[0,0,2]");
		secondWidget.set(Tags.Desc, "secondWidget");
		secondWidget.set(Tags.ConcreteID, "secondWidgetConcreteID");
		secondWidget.set(Tags.AbstractID, "secondWidgetAbstractID");

		Action secondAction = ac.leftClickAt(secondWidget);
		secondAction.set(Tags.ConcreteID, "secondActionConcreteID");
		secondAction.set(Tags.AbstractID, "secondActionAbstractID");
		originalActions.add(secondAction);
	}

	@Test
	@Repeat( times = 100 ) // Repeat to deal with action selection randomness
	public void testPrioritizeNewActionsSelector() {
		ActionSelectorProxy prioritizeSelector = new ActionSelectorProxy(new PrioritizeNewActionsSelector());

		// First derivation must return both actions
		Set<Action> firstDerivedActions = prioritizeSelector.deriveActions(originalActions);
		Assert.isTrue(firstDerivedActions.size() == 2);
		Action firstAction = prioritizeSelector.selectAction(state, firstDerivedActions);
		Assert.isTrue(firstAction != null);
		firstSelectedActionFromPrioritizeSelector.add(firstAction);
		prioritizeSelector.executeAction(firstAction);

		// Second derivation must return only the non-executed action
		Set<Action> secondDerivedActions = prioritizeSelector.deriveActions(originalActions);
		Assert.isTrue(secondDerivedActions.size() == 1);
		Action secondAction = prioritizeSelector.selectAction(state, secondDerivedActions);
		Assert.isTrue(secondAction != null);
		prioritizeSelector.executeAction(secondAction);

		// Check both selected actions were not the same ones
		Assert.isTrue(firstAction.get(Tags.Desc, "NonDesc") != secondAction.get(Tags.Desc, "NonDesc"));

		// Finally, third derivation must reset the actions and return both ones again
		Set<Action> thirdDerivedActions = prioritizeSelector.deriveActions(originalActions);
		Assert.isTrue(thirdDerivedActions.size() == 2);
	}

	@Test
	@Repeat( times = 100 ) // Repeat to deal with action selection randomness
	public void testGuiStateGraphWithVisitedActions() {
		ActionSelectorProxy guiGraphSelector = new ActionSelectorProxy(new GuiStateGraphWithVisitedActions());

		// First action derivation must return both actions
		Set<Action> firstDerivedActions = guiGraphSelector.deriveActions(originalActions);
		Assert.isTrue(firstDerivedActions.size() == 2);
		Action firstAction = guiGraphSelector.selectAction(state, firstDerivedActions);
		Assert.isTrue(firstAction != null);
		firstSelectedActionFromGuiGraphSelector.add(firstAction);
		guiGraphSelector.executeAction(firstAction);

		// Second derivation must return both actions
		Set<Action> secondDerivedActions = guiGraphSelector.deriveActions(originalActions);
		Assert.isTrue(secondDerivedActions.size() == 2);
		Action secondAction = guiGraphSelector.selectAction(state, secondDerivedActions);
		Assert.isTrue(secondAction != null);
		guiGraphSelector.executeAction(secondAction);

		// Check both selected actions were not the same ones
		Assert.isTrue(firstAction.get(Tags.Desc, "NonDesc") != secondAction.get(Tags.Desc, "NonDesc"));
	}

	@Test
	@Repeat( times = 100 ) // Repeat to deal with action selection randomness
	public void testQLearningActionSelector() {
		ActionSelectorProxy qLearningSelector = new ActionSelectorProxy(new QLearningActionSelector(99, 0.5));

		// First action derivation must return both actions
		Set<Action> firstDerivedActions = qLearningSelector.deriveActions(originalActions);
		Assert.isTrue(firstDerivedActions.size() == 2);
		Action firstAction = qLearningSelector.selectAction(state, firstDerivedActions);
		Assert.isTrue(firstAction != null);
		firstSelectedActionFromQLearningSelector.add(firstAction);
		qLearningSelector.executeAction(firstAction);

		// Second derivation must return both actions
		Set<Action> secondDerivedActions = qLearningSelector.deriveActions(originalActions);
		Assert.isTrue(secondDerivedActions.size() == 2);
		Action secondAction = qLearningSelector.selectAction(state, secondDerivedActions);
		Assert.isTrue(secondAction != null);
		qLearningSelector.executeAction(secondAction);

		// Check both selected actions were not the same ones
		Assert.isTrue(firstAction.get(Tags.Desc, "NonDesc") != secondAction.get(Tags.Desc, "NonDesc"));
	}

	@AfterClass
	public static void testFirstActionWereRandom() {
		// TESTAR used an internal System.currentTimeMillis method to determine the next random action to select. 
		// This is not truly random...

		// Invoking this System.currentTimeMillis during GUI testing if OK, because the interval of time is not small...
		// BUT invoking this System.currentTimeMillis method multiple times within a small interval of time can provoke obtain the same action repeatedly. 

		// So we need to test that "random" is effectively random :)
		// At least in these JUnit tests

		Map<String, Integer> countPrioritizeSelector = actionListCounter(firstSelectedActionFromPrioritizeSelector, "PrioritizeSelector");
		Assert.isTrue(countPrioritizeSelector.size() == 2);

		Map<String, Integer> countGuiGraphSelector = actionListCounter(firstSelectedActionFromGuiGraphSelector, "GuiGraphSelector");
		Assert.isTrue(countGuiGraphSelector.size() == 2);

		Map<String, Integer> countQLearningSelector = actionListCounter(firstSelectedActionFromQLearningSelector, "QLearningSelector");
		Assert.isTrue(countQLearningSelector.size() == 2);
	}

	private static Map<String, Integer> actionListCounter(List<Action> actionList, String actionListName) {
		// Create a HashMap to store the counts
		Map<String, Integer> countMap = new HashMap<>();
		// Loop through each element in the list and update the count in the HashMap
		for (Action action : actionList) {
			Integer count = countMap.get(action.get(Tags.Desc, "NonDesc"));
			if (count == null) {
				count = 0;
			}
			countMap.put(action.get(Tags.Desc, "NonDesc"), count + 1);
		}

		// Print the counts
		System.out.println(actionListName);
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		return countMap;
	}
}

//https://gist.github.com/fappel/8bcb2aea4b39ff9cfb6e
//JUnit 4 TestRule to run a test repeatedly for a specified amount of repetitions
//TODO: When migrate to JUnit 5 use the @RepeatedTest annotation

@Retention( java.lang.annotation.RetentionPolicy.RUNTIME )
@Target( { java.lang.annotation.ElementType.METHOD } )
@interface Repeat {
	public abstract int times();
}

class RepeatRule implements TestRule {

	private static class RepeatStatement extends Statement {

		private final int times;
		private final Statement statement;

		private RepeatStatement( int times, Statement statement ) {
			this.times = times;
			this.statement = statement;
		}

		@Override
		public void evaluate() throws Throwable {
			for( int i = 0; i < times; i++ ) {
				statement.evaluate();
			}
		}
	}

	@Override
	public Statement apply( Statement statement, Description description ) {
		Statement result = statement;
		Repeat repeat = description.getAnnotation( Repeat.class );
		if( repeat != null ) {
			int times = repeat.times();
			result = new RepeatStatement( times, statement );
		}
		return result;
	}
}
