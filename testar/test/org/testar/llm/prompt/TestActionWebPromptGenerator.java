package org.testar.llm.prompt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.action.priorization.llm.ActionHistory;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdCloseTabAction;
import org.testar.monkey.alayer.actions.WdHistoryBackAction;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestActionWebPromptGenerator {

	private static StateStub state;
	private static Set<Action> derivedActions;
	private static ActionHistory actionHistory;
	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	@BeforeClass
	public static void setup() {
		state = new StateStub();
		state.set(WdTags.WebTitle, "Page Title | State");
		derivedActions = new HashSet<>();
		actionHistory = new ActionHistory(1);

		// Derive a click action
		WidgetStub clickable_widget = new WidgetStub();
		state.addChild(clickable_widget);
		clickable_widget.setParent(state);
		clickable_widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		clickable_widget.set(Tags.Role, WdRoles.WdA);
		clickable_widget.set(Tags.Path, "[0,0,1]");
		clickable_widget.set(WdTags.WebId, "clickable_widget_web_id");
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
		typeable_widget.set(Tags.Role, WdRoles.WdTEXTAREA);
		typeable_widget.set(Tags.Path, "[0,0,1]");
		typeable_widget.set(WdTags.WebId, "typeable_widget_web_id");
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

		// Derive a select combobox action
		WidgetStub combobox_widget = new WidgetStub();
		state.addChild(combobox_widget);
		combobox_widget.setParent(state);
		combobox_widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		combobox_widget.set(Tags.Role, WdRoles.WdSELECT);
		combobox_widget.set(WdTags.WebTagName, "select");
		combobox_widget.set(Tags.Path, "[0,0,1]");
		combobox_widget.set(WdTags.WebId, "combobox_widget_web_id");
		combobox_widget.set(Tags.Desc, "combobox_widget_desc");
		combobox_widget.set(WdTags.WebInnerHTML, "<option value=\"volvo\">Volvo</option><option value=\"saab\">Saab</option>");
		combobox_widget.set(Tags.ConcreteID, "CID_combobox_widget");
		combobox_widget.set(Tags.AbstractID, "AID_combobox_widget");
		Action select_action = new WdSelectListAction("combobox_widget_web_id", "Saab", combobox_widget, WdSelectListAction.JsTargetMethod.ID);
		select_action.set(Tags.ConcreteID, "CID_select");
		select_action.set(Tags.AbstractID, "AID_select");
		derivedActions.add(select_action);
		actionHistory.addToHistory(select_action);
	}

	@Test
	public void test_prompt_generator_default_empty_history() {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator();

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				derivedActions, 
				state, 
				new ActionHistory(5), 
				"GUI_app", 
				"achieve this web goal", 
				""
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" web application."));
		Assert.isTrue(!prompt.contains("The following objective was previously achieved")); // Empty previous goal
		Assert.isTrue(prompt.contains("The objective of the test is: achieve this web goal."));
		Assert.isTrue(prompt.contains("We are currently on the following page: Page Title | State."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_type: Type in TextField 'typeable_widget_desc'"));
		Assert.isTrue(prompt.contains("AID_click: Click on 'clickable_widget_desc'"));
		Assert.isTrue(prompt.contains("AID_select: Set ComboBox 'combobox_widget_desc' to one of the following values: Volvo,Saab,"));
		Assert.isTrue(!prompt.contains("This is the last action we executed")); // Empty ActionHistory
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));
	}

	@Test
	public void test_prompt_generator_webid_with_history() {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator(WdTags.WebId);

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				derivedActions, 
				state, 
				actionHistory, 
				"GUI_app", 
				"achieve this web goal", 
				"this web goal was completed"
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" web application."));
		Assert.isTrue(prompt.contains("The following objective was previously achieved: this web goal was completed."));
		Assert.isTrue(prompt.contains("The current objective of the test is: achieve this web goal."));
		Assert.isTrue(prompt.contains("We are currently on the following page: Page Title | State."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_type: Type in TextField 'typeable_widget_web_id'"));
		Assert.isTrue(prompt.contains("AID_click: Click on 'clickable_widget_web_id'"));
		Assert.isTrue(prompt.contains("AID_select: Set ComboBox 'combobox_widget_web_id' to one of the following values: Volvo,Saab,"));
		Assert.isTrue(prompt.contains("This is the last action we executed: AID_select: Set value of ComboBox 'combobox_widget_web_id' to 'Saab'"));
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));		
	}

	@Test
	public void test_prompt_generator_history_back() {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator();

		Action actionHistoryBack = new WdHistoryBackAction(state);
		actionHistoryBack.set(Tags.ConcreteID, "CID_history");
		actionHistoryBack.set(Tags.AbstractID, "AID_history");

		Action closeTab = new WdCloseTabAction(state);
		closeTab.set(Tags.ConcreteID, "CID_closetab");
		closeTab.set(Tags.AbstractID, "AID_closetab");

		ActionHistory executedActions = new ActionHistory(1);
		executedActions.addToHistory(closeTab);

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				new HashSet<>(Collections.singletonList(actionHistoryBack)), 
				state, 
				executedActions, 
				"GUI_app", 
				"achieve this web goal", 
				"this web goal was completed"
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" web application."));
		Assert.isTrue(prompt.contains("The following objective was previously achieved: this web goal was completed."));
		Assert.isTrue(prompt.contains("The current objective of the test is: achieve this web goal."));
		Assert.isTrue(prompt.contains("We are currently on the following page: Page Title | State."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_history: Go History back in the browser"));
		Assert.isTrue(prompt.contains("This is the last action we executed: AID_closetab: Close current browser tab"));
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));		
	}

	@Test
	public void test_prompt_generator_close_tab() {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator();

		Action actionHistoryBack = new WdHistoryBackAction(state);
		actionHistoryBack.set(Tags.ConcreteID, "CID_history");
		actionHistoryBack.set(Tags.AbstractID, "AID_history");

		Action closeTab = new WdCloseTabAction(state);
		closeTab.set(Tags.ConcreteID, "CID_closetab");
		closeTab.set(Tags.AbstractID, "AID_closetab");

		ActionHistory executedActions = new ActionHistory(1);
		executedActions.addToHistory(actionHistoryBack);

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				new HashSet<>(Collections.singletonList(closeTab)), 
				state, 
				executedActions, 
				"GUI_app", 
				"achieve this web goal", 
				"this web goal was completed"
				);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("We are testing the \"GUI_app\" web application."));
		Assert.isTrue(prompt.contains("The following objective was previously achieved: this web goal was completed."));
		Assert.isTrue(prompt.contains("The current objective of the test is: achieve this web goal."));
		Assert.isTrue(prompt.contains("We are currently on the following page: Page Title | State."));
		Assert.isTrue(prompt.contains("The following actions are available:"));
		Assert.isTrue(prompt.contains("AID_closetab: Close current browser tab"));
		Assert.isTrue(prompt.contains("This is the last action we executed: AID_history: Go History back in the browser"));
		Assert.isTrue(prompt.contains("Which action should be executed to accomplish the test goal?"));		
	}
}
