package org.testar.llm.prompt;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.action.priorization.llm.ActionHistory;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestActionStandardPromptGenerator {

	private static StateStub state;
	private static Set<Action> derivedActions;
	private static ActionHistory actionHistory;
	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	@BeforeClass
	public static void setup() {
		state = new StateStub();
		derivedActions = new HashSet<>();
		actionHistory = new ActionHistory(1, Tags.Title);

		// Derive a click action
		WidgetStub clickable_widget = new WidgetStub();
		state.addChild(clickable_widget);
		clickable_widget.setParent(state);
		clickable_widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		clickable_widget.set(Tags.Role, Roles.Button);
		clickable_widget.set(Tags.Path, "[0,0,1]");
		clickable_widget.set(Tags.Title, "clickable_widget_title");
		clickable_widget.set(Tags.Desc, "clickable_widget_desc");
		clickable_widget.set(Tags.ConcreteID, "CID_clickable_widget");
		clickable_widget.set(Tags.AbstractID, "AID_clickable_widget");
		Action click_action = ac.leftClickAt(clickable_widget);
		click_action.set(Tags.ConcreteID, "CID_click");
		click_action.set(Tags.AbstractID, "AID_click");
		derivedActions.add(click_action);
		actionHistory.addToHistory(click_action);

		// Derive a Type action
		WidgetStub typeable_widget = new WidgetStub();
		state.addChild(typeable_widget);
		typeable_widget.setParent(state);
		typeable_widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		typeable_widget.set(Tags.Role, Roles.Button);
		typeable_widget.set(Tags.Path, "[0,0,1]");
		typeable_widget.set(Tags.Title, "typeable_widget_title");
		typeable_widget.set(Tags.Desc, "typeable_widget_desc");
		typeable_widget.set(Tags.ConcreteID, "CID_typeable_widget");
		typeable_widget.set(Tags.AbstractID, "AID_typeable_widget");
		Action type_action = ac.clickTypeInto(typeable_widget, "input_text", false);
		type_action.set(Tags.ConcreteID, "CID_type");
		type_action.set(Tags.AbstractID, "AID_type");
		// Right now, TESTAR creates compound actions which contains Type actions
		for(Action innerAction : ((CompoundAction)type_action).getActions()) {
			if(innerAction instanceof Type) {
				((Type)innerAction).set(Tags.InputText, "LLM_text");
			}
		}

		derivedActions.add(type_action);
		actionHistory.addToHistory(type_action);
	}

	@Test
	public void test_prompt_generator_default_empty_history() {
		IPromptActionGenerator promptActionGenerator = new ActionStandardPromptGenerator();

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				derivedActions, 
				state, 
				new ActionHistory(5), 
				"GUI_app", 
				"achieve this goal", 
				""
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" GUI application."));
		Assert.isTrue(!prompt.contains("The following objective was previously achieved")); // Empty previous goal
		Assert.isTrue(prompt.contains("The objective of the test is: achieve this goal."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_type: Type in Field 'typeable_widget_desc'"));
		Assert.isTrue(prompt.contains("AID_click: Click on 'clickable_widget_desc'"));
		Assert.isTrue(!prompt.contains("This is the last action we executed")); // Empty ActionHistory
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));
	}

	@Test
	public void test_prompt_generator_title_with_history() {
		IPromptActionGenerator promptActionGenerator = new ActionStandardPromptGenerator(Tags.Title);

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				derivedActions, 
				state, 
				actionHistory, 
				"GUI_app", 
				"achieve this goal", 
				"this goal was completed"
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" GUI application."));
		Assert.isTrue(prompt.contains("The following objective was previously achieved: this goal was completed."));
		Assert.isTrue(prompt.contains("The current objective of the test is: achieve this goal."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_type: Type in Field 'typeable_widget_title'"));
		Assert.isTrue(prompt.contains("AID_click: Click on 'clickable_widget_title'"));
		Assert.isTrue(prompt.contains("This is the last action we executed: AID_type: Typed 'LLM_text' in TextField 'typeable_widget_title'"));
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));		
	}
}
