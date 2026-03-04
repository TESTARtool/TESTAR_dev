package org.testar.llm.prompt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import org.testar.monkey.alayer.actions.WdRemoteClickAction;
import org.testar.monkey.alayer.actions.WdRemoteScrollClickAction;
import org.testar.monkey.alayer.actions.WdRemoteScrollTypeAction;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.webdriver.stub.WdWidgetStub;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestActionWebPromptGenerator {

	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	@Test
	public void test_prompt_generator_default_empty_history() {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator();
		StateStub state = createState();
		Set<Action> derivedActions = createDefaultDerivedActions(state);

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
		StateStub state = createState();
		Set<Action> derivedActions = createDefaultDerivedActions(state);
		ActionHistory actionHistory = createDefaultActionHistory(state);

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
		StateStub state = createState();

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
		StateStub state = createState();

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

	@Test
	public void test_prompt_generator_supports_remote_actions() throws Exception {
		IPromptActionGenerator promptActionGenerator = new ActionWebPromptGenerator();
		StateStub state = createState();

		Set<Action> actions = new HashSet<>();

		Action remoteClickAction = createRemoteClickAction("remote_click_widget", "remote_click_id",
				"CID_remote_click", "AID_remote_click");
		actions.add(remoteClickAction);

		Action remoteTypeAction = createRemoteTypeAction("remote_type_widget", "remote_type_id",
				"CID_remote_type", "AID_remote_type", "remote_text");
		actions.add(remoteTypeAction);

		Action remoteScrollClickAction = createRemoteScrollClickAction("remote_scroll_click_widget", "remote_scroll_click_id",
				"CID_remote_scroll_click", "AID_remote_scroll_click");
		actions.add(remoteScrollClickAction);

		Action remoteScrollTypeAction = createRemoteScrollTypeAction("remote_scroll_type_widget", "remote_scroll_type_id",
				"CID_remote_scroll_type", "AID_remote_scroll_type", "remote_scroll_text");
		actions.add(remoteScrollTypeAction);

		String prompt = promptActionGenerator.generateActionSelectionPrompt(
				actions,
				state,
				new ActionHistory(5),
				"GUI_app",
				"achieve this web goal",
				""
		);

		Assert.isTrue(!prompt.isEmpty());
		Assert.isTrue(prompt.contains("AID_remote_click: Click on 'remote_click_widget'"));
		Assert.isTrue(prompt.contains("AID_remote_type: Type in TextField 'remote_type_widget'"));
		Assert.isTrue(prompt.contains("AID_remote_scroll_click: Click on 'remote_scroll_click_widget'"));
		Assert.isTrue(prompt.contains("AID_remote_scroll_type: Type in TextField 'remote_scroll_type_widget'"));
	}

	private static StateStub createState() {
		StateStub createdState = new StateStub();
		createdState.set(WdTags.WebTitle, "Page Title | State");
		return createdState;
	}

	private static Set<Action> createDefaultDerivedActions(StateStub state) {
		Set<Action> derivedActions = new HashSet<>();
		derivedActions.add(createClickAction(state, "clickable_widget_desc", "clickable_widget_web_id",
				"CID_clickable_widget", "AID_clickable_widget", "CID_click", "AID_click"));
		derivedActions.add(createTypeAction(state, "typeable_widget_desc", "typeable_widget_web_id",
				"CID_typeable_widget", "AID_typeable_widget", "CID_type", "AID_type", "input_text", "LLM_text"));
		derivedActions.add(createSelectAction(state, "combobox_widget_desc", "combobox_widget_web_id",
				"CID_combobox_widget", "AID_combobox_widget", "CID_select", "AID_select", "Saab"));
		return derivedActions;
	}

	private static ActionHistory createDefaultActionHistory(StateStub state) {
		ActionHistory actionHistory = new ActionHistory(1);
		actionHistory.addToHistory(createClickAction(state, "clickable_widget_desc", "clickable_widget_web_id",
				"CID_clickable_widget", "AID_clickable_widget", "CID_click", "AID_click"));
		actionHistory.addToHistory(createTypeAction(state, "typeable_widget_desc", "typeable_widget_web_id",
				"CID_typeable_widget", "AID_typeable_widget", "CID_type", "AID_type", "input_text", "LLM_text"));
		actionHistory.addToHistory(createSelectAction(state, "combobox_widget_desc", "combobox_widget_web_id",
				"CID_combobox_widget", "AID_combobox_widget", "CID_select", "AID_select", "Saab"));
		return actionHistory;
	}

	private static WidgetStub createWebWidget(StateStub parentState, org.testar.monkey.alayer.Role role, String description,
			String webId, String concreteId, String abstractId) {
		WidgetStub widget = new WidgetStub();
		parentState.addChild(widget);
		widget.setParent(parentState);
		widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		widget.set(Tags.Role, role);
		widget.set(Tags.Path, "[0,0,1]");
		widget.set(WdTags.WebId, webId);
		widget.set(Tags.Desc, description);
		widget.set(Tags.ConcreteID, concreteId);
		widget.set(Tags.AbstractID, abstractId);
		return widget;
	}

	private static Action createClickAction(StateStub parentState, String description, String webId, String widgetConcreteId,
			String widgetAbstractId, String actionConcreteId, String actionAbstractId) {
		Action action = ac.leftClickAt(createWebWidget(parentState, WdRoles.WdA, description, webId, widgetConcreteId, widgetAbstractId));
		action.set(Tags.ConcreteID, actionConcreteId);
		action.set(Tags.AbstractID, actionAbstractId);
		return action;
	}

	private static Action createTypeAction(StateStub parentState, String description, String webId, String widgetConcreteId,
			String widgetAbstractId, String actionConcreteId, String actionAbstractId, String inputText, String llmText) {
		Action action = ac.clickTypeInto(createWebWidget(parentState, WdRoles.WdTEXTAREA, description, webId, widgetConcreteId, widgetAbstractId),
				inputText, false);
		action.set(Tags.ConcreteID, actionConcreteId);
		action.set(Tags.AbstractID, actionAbstractId);
		for(Action innerAction : ((CompoundAction) action).getActions()) {
			if(innerAction instanceof Type) {
				((Type) innerAction).set(Tags.InputText, llmText);
			}
		}
		return action;
	}

	private static Action createSelectAction(StateStub parentState, String description, String webId, String widgetConcreteId,
			String widgetAbstractId, String actionConcreteId, String actionAbstractId, String value) {
		WidgetStub widget = createWebWidget(parentState, WdRoles.WdSELECT, description, webId, widgetConcreteId, widgetAbstractId);
		widget.set(WdTags.WebTagName, "select");
		widget.set(WdTags.WebInnerHTML, "<option value=\"volvo\">Volvo</option><option value=\"saab\">Saab</option>");
		Action action = new WdSelectListAction(webId, value, widget, WdSelectListAction.JsTargetMethod.ID);
		action.set(Tags.ConcreteID, actionConcreteId);
		action.set(Tags.AbstractID, actionAbstractId);
		return action;
	}

	private static Action createRemoteClickAction(String description, String remoteId, String concreteId, String abstractId) {
		Action action = new WdRemoteClickAction(new WdWidgetStub(description, remoteId, WdRoles.WdA, "a"));
		action.set(Tags.ConcreteID, concreteId);
		action.set(Tags.AbstractID, abstractId);
		return action;
	}

	private static Action createRemoteScrollClickAction(String description, String remoteId, String concreteId, String abstractId) {
		Action action = new WdRemoteScrollClickAction(new WdWidgetStub(description, remoteId, WdRoles.WdA, "a"));
		action.set(Tags.ConcreteID, concreteId);
		action.set(Tags.AbstractID, abstractId);
		return action;
	}

	private static Action createRemoteTypeAction(String description, String remoteId, String concreteId, String abstractId, String text) {
		Action action = new WdRemoteTypeAction(new WdWidgetStub(description, remoteId, WdRoles.WdTEXTAREA, "textarea"), text);
		action.set(Tags.ConcreteID, concreteId);
		action.set(Tags.AbstractID, abstractId);
		return action;
	}

	private static Action createRemoteScrollTypeAction(String description, String remoteId, String concreteId, String abstractId, String text) {
		Action action = new WdRemoteScrollTypeAction(new WdWidgetStub(description, remoteId, WdRoles.WdTEXTAREA, "textarea"), text);
		action.set(Tags.ConcreteID, concreteId);
		action.set(Tags.AbstractID, abstractId);
		return action;
	}
}
