package org.testar.monkey.alayer.android.actions;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestBuildAndroidActions {

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
		widget.set(AndroidTags.AndroidClassName, "ClassNameValue");
		widget.set(AndroidTags.AndroidXpath, widgetPath);
	}

	@Test
	public void buildAndroidActionClick() {
		Action androidClick = new AndroidActionClick(state, widget);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidClick.get(Tags.OriginWidget));
		Assert.assertTrue(androidClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.LeftClickAt, androidClick.get(Tags.Role));
        Assert.assertTrue(androidClick.toParametersString().startsWith("role=" + ActionRoles.LeftClickAt));
	}

	@Test
	public void buildAndroidActionLongClick() {
		Action androidLongClick = new AndroidActionLongClick(state, widget);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidLongClick.get(Tags.OriginWidget));
		Assert.assertTrue(androidLongClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.LeftClickAt, androidLongClick.get(Tags.Role));
        Assert.assertTrue(androidLongClick.toParametersString().startsWith("role=" + ActionRoles.LeftClickAt));
	}

	@Test
	public void buildAndroidActionScroll() {
		Action androidScroll = new AndroidActionScroll(state, widget);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidScroll.get(Tags.OriginWidget));
		Assert.assertTrue(androidScroll.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.Drag, androidScroll.get(Tags.Role));
        Assert.assertTrue(androidScroll.toParametersString().startsWith("role=" + ActionRoles.Drag));
	}

	@Test
	public void buildAndroidActionType() {
		Action androidType = new AndroidActionType(state, widget, "TextToType");
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidType.get(Tags.OriginWidget));
		Assert.assertTrue(androidType.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
		Assert.assertTrue(androidType.get(Tags.InputText).equals("TextToType"));
	    // Verify action role
		Assert.assertEquals(ActionRoles.ClickTypeInto, androidType.get(Tags.Role));
        Assert.assertTrue(androidType.toParametersString().startsWith("role=" + ActionRoles.ClickTypeInto));
	}

	@Test
	public void buildAndroidBackAction() {
		Action androidBack = new AndroidBackAction(state);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidBack.get(Tags.OriginWidget));
		Assert.assertTrue(androidBack.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.Action, androidBack.get(Tags.Role));
        Assert.assertTrue(androidBack.toParametersString().startsWith("role=" + ActionRoles.Action));
	}

	@Test
	public void buildAndroidSystemActionCall() {
		Action androidSystemCall = new AndroidSystemActionCall(state);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidSystemCall.get(Tags.OriginWidget));
		Assert.assertTrue(androidSystemCall.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.Action, androidSystemCall.get(Tags.Role));
        Assert.assertTrue(androidSystemCall.toParametersString().startsWith("role=" + ActionRoles.Action));
	}

	@Test
	public void buildAndroidSystemActionOrientation() {
		Action androidSystemOrientation = new AndroidSystemActionOrientation(state);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidSystemOrientation.get(Tags.OriginWidget));
		Assert.assertTrue(androidSystemOrientation.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.Action, androidSystemOrientation.get(Tags.Role));
        Assert.assertTrue(androidSystemOrientation.toParametersString().startsWith("role=" + ActionRoles.Action));
	}

	@Test
	public void buildAndroidSystemActionText() {
		Action androidSystemText = new AndroidSystemActionText(state);
		// Verify Action <-> Widget mapping
		Assert.assertNotNull(androidSystemText.get(Tags.OriginWidget));
		Assert.assertTrue(androidSystemText.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	    // Verify action role
		Assert.assertEquals(ActionRoles.Action, androidSystemText.get(Tags.Role));
        Assert.assertTrue(androidSystemText.toParametersString().startsWith("role=" + ActionRoles.Action));
	}
}
