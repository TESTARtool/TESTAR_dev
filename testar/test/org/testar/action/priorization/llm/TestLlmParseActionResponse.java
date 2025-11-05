package org.testar.action.priorization.llm;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.android.actions.AndroidActionType;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.TextVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestLlmParseActionResponse {

	private static StateStub state;
	private static Set<Action> derivedActions;
	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	@BeforeClass
	public static void setup() {
		state = new StateStub();
		state.set(WdTags.WebTitle, "Page Title | State");
		derivedActions = new HashSet<>();

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
		Action type_action = ac.clickTypeInto(typeable_widget, "random_text", false);
		type_action.set(Tags.ConcreteID, "CID_type");
		type_action.set(Tags.AbstractID, "AID_type");
		derivedActions.add(type_action);

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

		// Derive an AndroidActionType action
		WidgetStub android_edit_widget = new WidgetStub();
		state.addChild(android_edit_widget);
		android_edit_widget.setParent(state);
		android_edit_widget.set(Tags.Shape, Rect.fromCoordinates(1, 1, 1, 1));
		android_edit_widget.set(Tags.Role, AndroidRoles.AndroidWidget);
		android_edit_widget.set(Tags.Path, "[0,0,1]");
		android_edit_widget.set(AndroidTags.AndroidXpath, "//path[0]");
		android_edit_widget.set(Tags.Desc, "android_edit_widget_desc");
		android_edit_widget.set(Tags.ConcreteID, "CID_android_edit_widget");
		android_edit_widget.set(Tags.AbstractID, "AID_android_edit_widget");
		Action android_action_type = new AndroidActionType(state, android_edit_widget, "default", "accessibilityId", "className");
		android_action_type.set(Tags.ConcreteID, "CID_android_action_type");
		android_action_type.set(Tags.AbstractID, "AID_android_action_type");
		derivedActions.add(android_action_type);
	}

	@Test
	public void test_llm_selects_click_action() {
		String llmResponse = "{\"actionId\":\"AID_click\",\"input\":\"\"}";

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_click"));
	}

	@Test
	public void test_parsing_click_action() {
		String llmResponse = "```json{\"actionId\":\"AID_click\",\"input\":\"\"}```";

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SUCCESS));
		Assert.isTrue(llmParseResult.getActionToExecute().get(Tags.AbstractID).equals("AID_click"));
	}

	@Test
	public void test_llm_selects_type_action() {
		String llmResponse = "{\"actionId\":\"AID_type\",\"input\":\"testar\"}";

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
	public void test_llm_creates_combobox_action() {
		String llmResponse = "{\"actionId\":\"AID_select\",\"input\":\"Saab\"}";

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

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.INVALID_ACTION));
	}

	@Test
	public void test_llm_selects_invalid_action() {
		String llmResponse = "{\"actionId\":\"ABCDEF\",\"input\":\"\"}";

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.INVALID_ACTION));
	}

	@Test
	public void test_llm_missing_select_input() {
		String llmResponse = "{\"actionId\":\"AID_select\",\"input\":\"\"}";

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.SL_MISSING_INPUT));
	}

	@Test
	public void test_invalid_response_format() {
		String llmResponse = "\"actionId\":\"AID_click\",\"input\":\"\"";

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.PARSE_FAILED));
	}

	@Test
	public void test_null_response() {
		String llmResponse = null;

		LlmParseActionResponse llmParseResponse = new LlmParseActionResponse();
		LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(derivedActions, llmResponse);

		Assert.isTrue(llmParseResult.getParseResult().equals(LlmParseActionResult.ParseResult.COMMUNICATION_FAILURE));
	}

	@Test
	public void test_llm_selects_android_type_action() {
		String llmResponse = "{\"actionId\":\"AID_android_action_type\",\"input\":\"testar\"}";

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

}
