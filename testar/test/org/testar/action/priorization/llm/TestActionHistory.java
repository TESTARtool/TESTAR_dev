package org.testar.action.priorization.llm;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
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
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestActionHistory {

    private static StdActionCompiler ac = new AnnotatingActionCompiler();

    private static Action web_click_action;
    private static Action web_type_action;
    private static Action web_select_action;
    private static Action android_type_action;

    @BeforeClass
    public static void setup() {
        StateStub state = new StateStub();
        state.set(WdTags.WebTitle, "Page Title | State");

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
        web_click_action = ac.leftClickAt(clickable_widget);
        web_click_action.set(Tags.ConcreteID, "CID_click");
        web_click_action.set(Tags.AbstractID, "AID_click");

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
        web_type_action = ac.clickTypeInto(typeable_widget, "input_text", false);
        web_type_action.set(Tags.ConcreteID, "CID_type");
        web_type_action.set(Tags.AbstractID, "AID_type");
        // Right now, TESTAR creates compound actions which contains Type actions
        for(Action innerAction : ((CompoundAction)web_type_action).getActions()) {
            if(innerAction instanceof Type) {
                ((Type)innerAction).set(Tags.InputText, "LLM_text");
            }
        }

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
        web_select_action = new WdSelectListAction("combobox_widget_web_id", "Saab", combobox_widget, WdSelectListAction.JsTargetMethod.ID);
        web_select_action.set(Tags.ConcreteID, "CID_select");
        web_select_action.set(Tags.AbstractID, "AID_select");

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
        android_type_action = new AndroidActionType(state, android_edit_widget, "default", "accessibilityId", "className");
        android_type_action.set(Tags.ConcreteID, "CID_android_action_type");
        android_type_action.set(Tags.AbstractID, "AID_android_action_type");
        android_type_action.set(Tags.InputText, "LLM_text");
    }

    @Test
    public void test_history_web_click_action() {
        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_click_action);
        assertTrue(actionHistory.getActions().contains(web_click_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed: AID_click:"));
        assertTrue(actionHistory.toString().contains("Clicked on 'clickable_widget_desc'"));
    }

    @Test
    public void test_history_web_type_action() {
        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_type_action);
        assertTrue(actionHistory.getActions().contains(web_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed: AID_type:"));
        assertTrue(actionHistory.toString().contains("Typed 'LLM_text' in TextField 'typeable_widget_desc'"));
    }

    @Test
    public void test_history_web_select_action() {
        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(web_select_action);
        assertTrue(actionHistory.getActions().contains(web_select_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed: AID_select:"));
        assertTrue(actionHistory.toString().contains("Set value of ComboBox 'combobox_widget_web_id' to 'Saab'"));
    }

    @Test
    public void test_history_android_type_action() {
        ActionHistory actionHistory = new ActionHistory(1);
        actionHistory.addToHistory(android_type_action);
        assertTrue(actionHistory.getActions().contains(android_type_action));
        assertTrue(actionHistory.toString().contains("This is the last action we executed: AID_android_action_type:"));
        assertTrue(actionHistory.toString().contains("Typed 'LLM_text' in TextField 'android_edit_widget_desc'"));
    }

    @Test
    public void test_history_multiple_actions() {
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

}
