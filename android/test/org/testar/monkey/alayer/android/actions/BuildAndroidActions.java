package org.testar.monkey.alayer.android.actions;

import org.junit.Test;
import org.junit.Before;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class BuildAndroidActions {

	private static StateStub state;
	private static WidgetStub widget;

	private static String pathTest = "[0,0,1]";

	@Before
	public void prepare_widget_and_state() {
		state = new StateStub();
		widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));

		widget.set(AndroidTags.AndroidText, "TextValue");
		widget.set(AndroidTags.AndroidAccessibilityId, "AccessibilityIdValue");
		widget.set(AndroidTags.AndroidClassName, "ClassNameValue");
		widget.set(AndroidTags.AndroidXpath, pathTest);
	}

	@Test
	public void buildAndroidActionClick() {
		Action androidClick = new AndroidActionClick(state, widget,
				widget.get(AndroidTags.AndroidText,""),
				widget.get(AndroidTags.AndroidAccessibilityId,""),
				widget.get(AndroidTags.AndroidClassName, ""));
		// Verify Action <-> Widget mapping
		Assert.notNull(androidClick.get(Tags.OriginWidget));
		Assert.isTrue(androidClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidActionLongClick() {
		Action androidLongClick = new AndroidActionLongClick(state, widget,
				widget.get(AndroidTags.AndroidAccessibilityId,""));
		// Verify Action <-> Widget mapping
		Assert.notNull(androidLongClick.get(Tags.OriginWidget));
		Assert.isTrue(androidLongClick.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidActionPinch() {
		Action androidPinch = new AndroidActionPinch(state, widget,
				widget.get(AndroidTags.AndroidAccessibilityId,""),
				false);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidPinch.get(Tags.OriginWidget));
		Assert.isTrue(androidPinch.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidActionScroll() {
		Action androidScroll = new AndroidActionScroll(state, widget,
				widget.get(AndroidTags.AndroidAccessibilityId,""));
		// Verify Action <-> Widget mapping
		Assert.notNull(androidScroll.get(Tags.OriginWidget));
		Assert.isTrue(androidScroll.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidActionType() {
		Action androidType = new AndroidActionType(state, widget,
				"TextToType",
				widget.get(AndroidTags.AndroidAccessibilityId,""),
				widget.get(AndroidTags.AndroidText,""),
				widget.get(AndroidTags.AndroidClassName, ""));
		// Verify Action <-> Widget mapping
		Assert.notNull(androidType.get(Tags.OriginWidget));
		Assert.isTrue(androidType.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidBackAction() {
		Action androidBack = new AndroidBackAction(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidBack.get(Tags.OriginWidget));
		Assert.isTrue(androidBack.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidSystemActionCall() {
		Action androidSystemCall = new AndroidSystemActionCall(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemCall.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemCall.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidSystemActionOrientation() {
		Action androidSystemOrientation = new AndroidSystemActionOrientation(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemOrientation.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemOrientation.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}

	@Test
	public void buildAndroidSystemActionText() {
		Action androidSystemText = new AndroidSystemActionText(state, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(androidSystemText.get(Tags.OriginWidget));
		Assert.isTrue(androidSystemText.get(Tags.OriginWidget).get(AndroidTags.AndroidXpath).equals(pathTest));
	}
}
