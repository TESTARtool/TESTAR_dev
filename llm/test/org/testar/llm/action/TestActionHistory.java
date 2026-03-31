package org.testar.llm.action;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testar.android.action.AndroidActionType;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.action.Action;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.CompoundAction;
import org.testar.core.action.StdActionCompiler;
import org.testar.core.action.Type;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Role;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.action.WdSelectListAction;
import org.testar.webdriver.action.WdRemoteClickAction;
import org.testar.webdriver.action.WdRemoteScrollClickAction;
import org.testar.webdriver.action.WdRemoteTypeAction;
import org.testar.webdriver.action.WdRemoteScrollTypeAction;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.stub.WdWidgetStub;
import org.testar.webdriver.tag.WdTags;

public class TestActionHistory {

    private static StdActionCompiler ac = new AnnotatingActionCompiler();

    @Test
    public void test_history_web_click_action() {
        Action web_click_action = createClickAction(
            createState(), 
            "clickable_widget_desc", 
            "clickable_widget_web_id", 
            "CID_clickable_widget", 
            "AID_clickable_widget", 
            "CID_click", 
            "AID_click"
        );

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_click_action);
        assertTrue(actionHistory.getActions().contains(web_click_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_click"));
        assertTrue(actionHistory.toString().contains("Clicked on 'clickable_widget_desc'"));
    }

    @Test
    public void test_history_web_type_action() {
        Action web_type_action = createTypeAction(
                createState(),
                "typeable_widget_desc",
                "typeable_widget_web_id",
                "CID_typeable_widget",
                "AID_typeable_widget",
                "CID_type",
                "AID_type",
                "input_text",
                "LLM_text");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_type_action);
        assertTrue(actionHistory.getActions().contains(web_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_type"));
        assertTrue(actionHistory.toString().contains("Typed 'LLM_text' in TextField 'typeable_widget_desc'"));
    }

    @Test
    public void test_history_web_select_action() {
        Action web_select_action = createSelectAction(
                createState(),
                "combobox_widget_desc",
                "combobox_widget_web_id",
                "CID_combobox_widget",
                "AID_combobox_widget",
                "CID_select",
                "AID_select",
                "Saab");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_select_action);
        assertTrue(actionHistory.getActions().contains(web_select_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_select"));
        assertTrue(actionHistory.toString().contains("Set value of ComboBox 'combobox_widget_web_id' to 'Saab'"));
    }

    @Test
    public void test_history_android_type_action() {
        Action android_type_action = createAndroidTypeAction(createState());

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(android_type_action);
        assertTrue(actionHistory.getActions().contains(android_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_android_action_type"));
        assertTrue(actionHistory.toString().contains("Typed 'LLM_text' in TextField 'android_edit_widget_desc'"));
    }

    @Test
    public void test_history_web_remote_click_action() {
        Action web_remote_click_action = createRemoteClickAction(
                "remote_click_widget",
                "remote_click_id",
                "CID_remote_click",
                "AID_remote_click");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_remote_click_action);
        assertTrue(actionHistory.getActions().contains(web_remote_click_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_remote_click"));
        assertTrue(actionHistory.toString().contains("Clicked on 'remote_click_widget'"));
    }

    @Test
    public void test_history_web_remote_scroll_click_action() {
        Action web_remote_scroll_click_action = createRemoteScrollClickAction(
                "remote_scroll_click_widget",
                "remote_scroll_click_id",
                "CID_remote_scroll_click",
                "AID_remote_scroll_click");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_remote_scroll_click_action);
        assertTrue(actionHistory.getActions().contains(web_remote_scroll_click_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_remote_scroll_click"));
        assertTrue(actionHistory.toString().contains("Clicked on 'remote_scroll_click_widget'"));
    }

    @Test
    public void test_history_web_remote_type_action() {
        Action web_remote_type_action = createRemoteTypeAction(
                "remote_type_widget",
                "remote_type_id",
                "CID_remote_type",
                "AID_remote_type",
                "remote_text");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_remote_type_action);
        assertTrue(actionHistory.getActions().contains(web_remote_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_remote_type"));
        assertTrue(actionHistory.toString().contains("Typed 'remote_text' in TextField 'remote_type_widget'"));
    }

    @Test
    public void test_history_web_remote_scroll_type_action() {
        Action web_remote_scroll_type_action = createRemoteScrollTypeAction(
                "remote_scroll_type_widget",
                "remote_scroll_type_id",
                "CID_remote_scroll_type",
                "AID_remote_scroll_type",
                "remote_scroll_text");

        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_remote_scroll_type_action);
        assertTrue(actionHistory.getActions().contains(web_remote_scroll_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed:"));
        assertTrue(!actionHistory.toString().contains("AID_remote_scroll_type"));
        assertTrue(actionHistory.toString().contains("Typed 'remote_scroll_text' in TextField 'remote_scroll_type_widget'"));
    }

    @Test
    public void test_history_multiple_actions() {
        StateStub state = createState();
        Action web_click_action = createClickAction(state, "clickable_widget_desc", "clickable_widget_web_id",
                "CID_clickable_widget", "AID_clickable_widget", "CID_click", "AID_click");
        Action web_type_action = createTypeAction(state, "typeable_widget_desc", "typeable_widget_web_id",
                "CID_typeable_widget", "AID_typeable_widget", "CID_type", "AID_type", "input_text", "LLM_text");
        Action web_select_action = createSelectAction(state, "combobox_widget_desc", "combobox_widget_web_id",
                "CID_combobox_widget", "AID_combobox_widget", "CID_select", "AID_select", "Saab");
        Action android_type_action = createAndroidTypeAction(state);

        ActionHistory actionHistory = new ActionHistory(3);
        actionHistory.addToHistory(web_click_action);
        actionHistory.addToHistory(web_type_action);

        actionHistory.clear();

        actionHistory.addToHistory(web_select_action);
        actionHistory.addToHistory(android_type_action);

        assertTrue(actionHistory.getActions().size() == 2);

        assertTrue(actionHistory.getActions().contains(web_select_action));
        assertTrue(actionHistory.getActions().contains(android_type_action));

        assertTrue(actionHistory.toString().contains("These are the last 2 actions we executed:"));
        assertTrue(actionHistory.toString().contains("Set value of ComboBox 'combobox_widget_web_id' to 'Saab'"));
        assertTrue(actionHistory.toString().contains("Typed 'LLM_text' in TextField 'android_edit_widget_desc'"));
    }

    private static StateStub createState() {
        StateStub createdState = new StateStub();
        createdState.set(WdTags.WebTitle, "Page Title | State");
        return createdState;
    }

    private static WidgetStub createWebWidget(StateStub parentState, Role role, String description,
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
        action.set(Tags.InputText, "LLM_text");
        return action;
    }

}
