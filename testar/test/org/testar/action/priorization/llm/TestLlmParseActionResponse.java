package org.testar.action.priorization.llm;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdRemoteScrollTypeAction;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.android.actions.AndroidActionType;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.TextVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.webdriver.stub.WdWidgetStub;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestLlmParseActionResponse {

	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	@Test
	public void test_llm_selects_click_action() {
		String llmResponse = "{\"actionId\":\"AID_click\",\"input\":\"\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_click"));
	}

	@Test
	public void test_parsing_click_action() {
		String llmResponse = "```json{\"actionId\":\"AID_click\",\"input\":\"\"}```";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_click"));
	}

	@Test
	public void test_llm_selects_type_action() {
		String llmResponse = "{\"actionId\":\"AID_type\",\"input\":\"testar\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_type"));

		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer, null) != null);
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer) instanceof TextVisualizer);
		Assert.isTrue(((TextVisualizer)llmParseResult.getActionToExecute().get(Tags.Visualizer)).getText().equals("testar"));

		// We need to iterate through the compound actions to extract the input text to type
		String innerText = "undefined";
		for(Action innerAction : ((CompoundAction)llmParseResult.getActionToExecute()).getActions()) {
			if (innerAction instanceof Type) {
				innerText = innerAction.get(Tags.InputText, "nothing");
			}
		}
		Assert.isTrue(innerText.equals("testar"));
	}

	@Test
	public void test_parsing_type_action() {
		String llmResponse = "```json{\"actionId\":\"AID_type\",\"input\":\"testar\"}```";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_type"));

		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer, null) != null);
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer) instanceof TextVisualizer);
		Assert.isTrue(((TextVisualizer)llmParseResult.getActionToExecute().get(Tags.Visualizer)).getText().equals("testar"));

		// We need to iterate through the compound actions to extract the input text to type
		String innerText = "undefined";
		for(Action innerAction : ((CompoundAction)llmParseResult.getActionToExecute()).getActions()) {
			if (innerAction instanceof Type) {
				innerText = innerAction.get(Tags.InputText, "nothing");
			}
		}
		Assert.isTrue(innerText.equals("testar"));
	}

	@Test
	public void test_llm_selects_remote_type_action() {
		String llmResponse = "{\"actionId\":\"AID_remote_type\",\"input\":\"testar_remote\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_remote_type"));
		Assert.isTrue(llmParseResult.getActionToExecute() instanceof WdRemoteTypeAction);
		Assert.isTrue(((WdRemoteTypeAction) llmParseResult.getActionToExecute()).getKeys().equals("testar_remote"));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.InputText, "").equals("testar_remote"));
	}

	@Test
	public void test_llm_selects_remote_scroll_type_action() {
		String llmResponse = "{\"actionId\":\"AID_remote_scroll_type\",\"input\":\"testar_remote_scroll\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_remote_scroll_type"));
		Assert.isTrue(llmParseResult.getActionToExecute() instanceof WdRemoteTypeAction);
		Assert.isTrue(llmParseResult.getActionToExecute() instanceof WdRemoteScrollTypeAction);
		Assert.isTrue(((WdRemoteScrollTypeAction) llmParseResult.getActionToExecute()).getKeys().equals("testar_remote_scroll"));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.InputText, "").equals("testar_remote_scroll"));
	}

	@Test
	public void test_llm_creates_combobox_action() {
		String llmResponse = "{\"actionId\":\"AID_select\",\"input\":\"Saab\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		// For WdSelectListActions we internally create a new one instead of select existing
		Assert.isTrue(llmParseResult.getActionToExecute() instanceof WdSelectListAction);
		Assert.isTrue(llmParseResult.getActionToExecute().toShortString().contains("to 'Saab'"));
	}

	@Test
	public void test_llm_missing_actionid() {
		String llmResponse = "{\"input\":\"\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.INVALID_ACTION));
	}

	@Test
	public void test_llm_selects_invalid_action() {
		String llmResponse = "{\"actionId\":\"ABCDEF\",\"input\":\"\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.INVALID_ACTION));
	}

	@Test
	public void test_llm_missing_select_input() {
		String llmResponse = "{\"actionId\":\"AID_select\",\"input\":\"\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SL_MISSING_INPUT));
	}

	@Test
	public void test_invalid_response_format() {
		String llmResponse = "\"actionId\":\"AID_click\",\"input\":\"\"";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.PARSE_FAILED));
	}

	@Test
	public void test_null_response() {
		String llmResponse = null;
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.COMMUNICATION_FAILURE));
	}

	@Test
	public void test_llm_selects_android_type_action() {
		String llmResponse = "{\"actionId\":\"AID_android_action_type\",\"input\":\"testar\"}";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_android_action_type"));

		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer, null) != null);
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.Visualizer) instanceof TextVisualizer);
		Assert.isTrue(((TextVisualizer)llmParseResult.getActionToExecute().get(Tags.Visualizer)).getText().equals("testar"));

		String innerText = llmParseResult.getActionToExecute().get(Tags.InputText, "nothing");
		Assert.isTrue(innerText.equals("testar"));
	}

	@Test
	public void test_parsing_hit_escape_action() {
		String llmResponse = "```json{\"actionId\":\"AID_HitEsc\",\"input\":\"\"}```";
		Set<Action> derivedActions = createDefaultDerivedActions(createState());

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		assertEquals(LlmParseActionResult.ParseResult.SUCCESS, llmParseResult.getParseResult());
		assertEquals("AID_HitEsc", llmParseResult.getActionToExecute().get(Tags.AbstractID));
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
				"CID_typeable_widget", "AID_typeable_widget", "CID_type", "AID_type", "random_text"));
		derivedActions.add(createRemoteTypeAction("remote_type_widget", "remote_type_id",
				"CID_remote_type", "AID_remote_type", "random_remote_text"));
		derivedActions.add(createRemoteScrollTypeAction("remote_scroll_type_widget", "remote_scroll_type_id",
				"CID_remote_scroll_type", "AID_remote_scroll_type", "random_remote_scroll_text"));
		derivedActions.add(createSelectAction(state, "combobox_widget_desc", "combobox_widget_web_id",
				"CID_combobox_widget", "AID_combobox_widget", "CID_select", "AID_select", "Saab"));
		derivedActions.add(createAndroidTypeAction(state));
		derivedActions.add(createHitEscAction(state, "CID_HitEsc", "AID_HitEsc"));
		return derivedActions;
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
			String widgetAbstractId, String actionConcreteId, String actionAbstractId, String inputText) {
		Action action = ac.clickTypeInto(createWebWidget(parentState, WdRoles.WdTEXTAREA, description, webId, widgetConcreteId, widgetAbstractId),
				inputText, false);
		action.set(Tags.ConcreteID, actionConcreteId);
		action.set(Tags.AbstractID, actionAbstractId);
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

	private static Action createAndroidTypeAction(StateStub parentState) {
		WidgetStub widget = new WidgetStub();
		parentState.addChild(widget);
		widget.setParent(parentState);
		widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		widget.set(Tags.Role, AndroidRoles.AndroidWidget);
		widget.set(Tags.Path, "[0,0,1]");
		widget.set(AndroidTags.AndroidXpath, "//path[0]");
		widget.set(Tags.Desc, "android_edit_widget_desc");
		widget.set(Tags.ConcreteID, "CID_android_edit_widget");
		widget.set(Tags.AbstractID, "AID_android_edit_widget");
		widget.set(AndroidTags.AndroidAccessibilityId, "accessibilityId");
		widget.set(AndroidTags.AndroidClassName, "className");

		Action action = new AndroidActionType(parentState, widget, "default");
		action.set(Tags.ConcreteID, "CID_android_action_type");
		action.set(Tags.AbstractID, "AID_android_action_type");
		return action;
	}

	private static Action createHitEscAction(StateStub parentState, String actionConcreteId, String actionAbstractId) {
		Action action = ac.hitESC(parentState);
		action.set(Tags.ConcreteID, actionConcreteId);
		action.set(Tags.AbstractID, actionAbstractId);
		return action;
	}

}
