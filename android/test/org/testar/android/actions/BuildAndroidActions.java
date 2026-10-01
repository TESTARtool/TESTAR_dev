package org.testar.android.actions;

import org.junit.Test;
import org.junit.Before;
import org.testar.core.CodingManager;
import org.testar.android.action.AndroidActionClick;
import org.testar.android.action.AndroidActionLongClick;
import org.testar.android.action.AndroidActionScroll;
import org.testar.android.action.AndroidActionType;
import org.testar.android.action.AndroidBackAction;
import org.testar.android.action.AndroidSystemActionCall;
import org.testar.android.action.AndroidSystemActionOrientation;
import org.testar.android.action.AndroidSystemActionText;
import org.testar.android.tag.AndroidTags;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BuildAndroidActions {

    private static StateStub state;
    private static WidgetStub widget;

    private static String statePath = "[0]";
    private static String widgetPath = "[0,0,1]";

    @Before
    public void prepare_widget_and_state() {
        state = new StateStub();
        state.set(AndroidTags.AndroidXpath, statePath);
        widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));

        widget.set(AndroidTags.AndroidText, "TextValue");
        widget.set(AndroidTags.AndroidAccessibilityId, "AccessibilityIdValue");
        widget.set(AndroidTags.AndroidClassName, "android.widget.CheckBox");
        widget.set(AndroidTags.AndroidXpath, widgetPath);
    }

    @Test
    public void buildAndroidActionClick() {
        Action androidClick = new AndroidActionClick(state, widget);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidClick.get(Tags.OriginWidget));
        Assert.isTrue(androidClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
        Assert.isTrue(androidClick.get(Tags.Desc).equals("Android click on widget checkbox_accessibilityidvalue"));
    }

    @Test
    public void buildAndroidActionLongClick() {
        Action androidLongClick = new AndroidActionLongClick(state, widget);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidLongClick.get(Tags.OriginWidget));
        Assert.isTrue(androidLongClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
        Assert.isTrue(androidLongClick.get(Tags.Desc).equals("Android long click on widget checkbox_accessibilityidvalue"));
    }

    @Test
    public void buildAndroidActionScroll() {
        Action androidScroll = new AndroidActionScroll(state, widget);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidScroll.get(Tags.OriginWidget));
        Assert.isTrue(androidScroll.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
        Assert.isTrue(androidScroll.get(Tags.Desc).equals("Android scroll on widget checkbox_accessibilityidvalue"));
    }

    @Test
    public void buildAndroidActionType() {
        Action androidType = new AndroidActionType(state, widget, "TextToType");
        // Verify Action <-> Widget mapping
        Assert.notNull(androidType.get(Tags.OriginWidget));
        Assert.isTrue(androidType.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
        Assert.isTrue(androidType.get(Tags.InputText).equals("TextToType"));
        Assert.isTrue(androidType.get(Tags.Desc).equals("Android type text TextToType on widget checkbox_accessibilityidvalue"));
    }

    @Test
    public void buildAndroidActionClickUsesButtonRoleAndTextFallback() {
        widget.set(AndroidTags.AndroidAccessibilityId, "");
        widget.set(AndroidTags.AndroidText, "Save draft");
        widget.set(AndroidTags.AndroidHint, "");
        widget.set(AndroidTags.AndroidResourceId, "");
        widget.set(AndroidTags.AndroidClassName, "android.widget.Button");

        Action androidClick = new AndroidActionClick(state, widget);

        Assert.isTrue(androidClick.get(Tags.Desc).equals("Android click on widget button_save_draft"));
    }

    @Test
    public void buildAndroidActionTypePrefersHintForEditText() {
        widget.set(AndroidTags.AndroidAccessibilityId, "");
        widget.set(AndroidTags.AndroidText, "Current value");
        widget.set(AndroidTags.AndroidHint, "Type your answer here");
        widget.set(AndroidTags.AndroidResourceId, "");
        widget.set(AndroidTags.AndroidClassName, "android.widget.EditText");

        Action androidType = new AndroidActionType(state, widget, "TextToType");

        Assert.isTrue(androidType.get(Tags.Desc).equals("Android type text TextToType on widget edittext_type_your_answer_here"));
    }

    @Test
    public void buildAndroidActionTypePreservesMaskedHintCharacters() {
        widget.set(AndroidTags.AndroidAccessibilityId, "");
        widget.set(AndroidTags.AndroidText, "");
        widget.set(AndroidTags.AndroidHint, "\u2022\u2022\u2022\u2022\u2022\u2022");
        widget.set(AndroidTags.AndroidResourceId, "");
        widget.set(AndroidTags.AndroidClassName, "android.widget.EditText");

        Action androidType = new AndroidActionType(state, widget, "foo-boo@foo.com");

        Assert.isTrue(androidType.get(Tags.Desc).equals("Android type text foo-boo@foo.com on widget edittext_??????"));
    }

    @Test
    public void buildAndroidActionScrollUsesResourceIdFallbackForSwitch() {
        widget.set(AndroidTags.AndroidAccessibilityId, "");
        widget.set(AndroidTags.AndroidText, "");
        widget.set(AndroidTags.AndroidHint, "");
        widget.set(AndroidTags.AndroidResourceId, "project-toggle");
        widget.set(AndroidTags.AndroidClassName, "android.widget.Switch");

        Action androidScroll = new AndroidActionScroll(state, widget);

        Assert.isTrue(androidScroll.get(Tags.Desc).equals("Android scroll on widget switch_project-toggle"));
    }

    @Test
    public void buildAndroidBackAction() {
        Action androidBack = new AndroidBackAction(state);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidBack.get(Tags.OriginWidget));
        Assert.isTrue(androidBack.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
        Assert.isTrue(androidBack.get(Tags.Desc).equals("Android back button click"));
    }

    @Test
    public void buildAndroidSystemActionCall() {
        Action androidSystemCall = new AndroidSystemActionCall(state);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidSystemCall.get(Tags.OriginWidget));
        Assert.isTrue(androidSystemCall.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
        Assert.isTrue(androidSystemCall.get(Tags.Desc).equals("Android system event: Call"));
    }

    @Test
    public void buildAndroidSystemActionOrientation() {
        Action androidSystemOrientation = new AndroidSystemActionOrientation(state);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidSystemOrientation.get(Tags.OriginWidget));
        Assert.isTrue(androidSystemOrientation.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
        Assert.isTrue(androidSystemOrientation.get(Tags.Desc).equals("Android orientation change (portrait vs landscape)"));
    }

    @Test
    public void buildAndroidSystemActionText() {
        Action androidSystemText = new AndroidSystemActionText(state);
        // Verify Action <-> Widget mapping
        Assert.notNull(androidSystemText.get(Tags.OriginWidget));
        Assert.isTrue(androidSystemText.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
        Assert.isTrue(androidSystemText.get(Tags.Desc).equals("Android system event: text message"));
    }

    @Test
    public void widgetActionsHaveDistinctConcreteIdsAndSemanticRoles() {
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        widget.set(Tags.ConcreteID, "widget-concrete-id");
        widget.set(Tags.Path, widgetPath);

        Action click = new AndroidActionClick(state, widget);
        Action longClick = new AndroidActionLongClick(state, widget);
        Action scroll = new AndroidActionScroll(state, widget);
        Action typeFirst = new AndroidActionType(state, widget, "first");
        Action typeSecond = new AndroidActionType(state, widget, "second");
        Set<Action> actions = new HashSet<>(Set.of(click, longClick, scroll, typeFirst, typeSecond));

        CodingManager.buildIDs(state, actions);

        Set<String> concreteIds = new HashSet<>();
        for (Action action : actions) {
            concreteIds.add(action.get(Tags.ConcreteID));
            Assert.isTrue(!action.toParametersString().isEmpty());
            Assert.isTrue(action.toString(new org.testar.core.alayer.Role[0]).equals(action.toParametersString()));
        }
        assertEquals(actions.size(), concreteIds.size());
        assertEquals(ActionRoles.LeftClickAt, longClick.get(Tags.Role));
        assertEquals(ActionRoles.Drag, scroll.get(Tags.Role));
        assertNotEquals(click.get(Tags.ConcreteID), longClick.get(Tags.ConcreteID));
    }

    @Test
    public void systemActionsHaveDistinctConcreteIds() {
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        Action[] actions = {
            new AndroidBackAction(state),
            new AndroidSystemActionCall(state),
            new AndroidSystemActionOrientation(state),
            new AndroidSystemActionText(state)
        };

        Set<String> concreteIds = new HashSet<>();
        for (Action action : actions) {
            CodingManager.buildEnvironmentActionIDs(state, action);
            concreteIds.add(action.get(Tags.ConcreteID));
            assertEquals(ActionRoles.Action, action.get(Tags.Role));
        }
        assertEquals(actions.length, concreteIds.size());
    }

    @Test
    public void abstractActionIdsDoNotDependOnSetInsertionOrder() {
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        widget.set(Tags.ConcreteID, "widget-concrete-id");
        widget.set(Tags.Path, widgetPath);

        Action firstClick = new AndroidActionClick(state, widget);
        Action firstLongClick = new AndroidActionLongClick(state, widget);
        Set<Action> forward = new LinkedHashSet<>();
        forward.add(firstClick);
        forward.add(firstLongClick);
        CodingManager.buildIDs(state, forward);

        Action secondClick = new AndroidActionClick(state, widget);
        Action secondLongClick = new AndroidActionLongClick(state, widget);
        Set<Action> reverse = new LinkedHashSet<>();
        reverse.add(secondLongClick);
        reverse.add(secondClick);
        CodingManager.buildIDs(state, reverse);

        assertEquals(firstClick.get(Tags.AbstractID), secondClick.get(Tags.AbstractID));
        assertEquals(firstLongClick.get(Tags.AbstractID), secondLongClick.get(Tags.AbstractID));
        assertNotEquals(firstClick.get(Tags.AbstractID), firstLongClick.get(Tags.AbstractID));
    }
}
