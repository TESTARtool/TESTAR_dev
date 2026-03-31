package org.testar.android.actions;

import org.junit.Test;
import org.junit.Before;
import org.testar.android.action.AndroidActionClick;
import org.testar.android.action.AndroidActionLongClick;
import org.testar.android.action.AndroidActionPinch;
import org.testar.android.action.AndroidActionScroll;
import org.testar.android.action.AndroidActionType;
import org.testar.android.action.AndroidBackAction;
import org.testar.android.action.AndroidSystemActionCall;
import org.testar.android.action.AndroidSystemActionOrientation;
import org.testar.android.action.AndroidSystemActionText;
import org.testar.android.tag.AndroidTags;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

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
		widget.set(AndroidTags.AndroidClassName, "ClassNameValue");
		widget.set(AndroidTags.AndroidXpath, widgetPath);
	}

	@Test
	public void buildAndroidActionClick() {
		Action androidClick = new AndroidActionClick(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidClick.get(Tags.OriginWidget));
		Assert.isTrue(androidClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	}

	@Test
	public void buildAndroidActionLongClick() {
		Action androidLongClick = new AndroidActionLongClick(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidLongClick.get(Tags.OriginWidget));
		Assert.isTrue(androidLongClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	}

	@Test
	public void buildAndroidActionPinch() {
		Action androidPinch = new AndroidActionPinch(state, widget, false);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidPinch.get(Tags.OriginWidget));
		Assert.isTrue(androidPinch.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	}

	@Test
	public void buildAndroidActionScroll() {
		Action androidScroll = new AndroidActionScroll(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidScroll.get(Tags.OriginWidget));
		Assert.isTrue(androidScroll.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
	}

	@Test
	public void buildAndroidActionType() {
		Action androidType = new AndroidActionType(state, widget, "TextToType");
		// Verify Action <-> Widget mapping
		Assert.notNull(androidType.get(Tags.OriginWidget));
		Assert.isTrue(androidType.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(widgetPath));
		Assert.isTrue(androidType.get(Tags.InputText).equals("TextToType"));
	}

	@Test
	public void buildAndroidBackAction() {
		Action androidBack = new AndroidBackAction(state);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidBack.get(Tags.OriginWidget));
		Assert.isTrue(androidBack.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	}

	@Test
	public void buildAndroidSystemActionCall() {
		Action androidSystemCall = new AndroidSystemActionCall(state);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemCall.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemCall.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	}

	@Test
	public void buildAndroidSystemActionOrientation() {
		Action androidSystemOrientation = new AndroidSystemActionOrientation(state);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemOrientation.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemOrientation.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	}

	@Test
	public void buildAndroidSystemActionText() {
		Action androidSystemText = new AndroidSystemActionText(state);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemText.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemText.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(statePath));
	}
}
