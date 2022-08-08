package org.testar.monkey;

import java.util.Collections;
import java.util.HashSet;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.testar.monkey.alayer.AbsolutePosition;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Point;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class BuildActionsIdentifiers {

	private static DefaultProtocol defaultProtocol;
	private static StateStub state;
	private static WidgetStub widget;
	private static StdActionCompiler ac;

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
		defaultProtocol = new DefaultProtocol();
	}

	@Before
	public void prepare_widget_and_state() {
		state = new StateStub();
		widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
		widget.set(Tags.Role, Roles.Button);
		widget.set(Tags.Path, "[0,0,1]");

		// Build widget and state identifiers
		defaultProtocol.buildStateIdentifiers(state);

		// To derive actions (such as clicks, drag&drop, typing ...) we should create an action compiler.
		ac = new AnnotatingActionCompiler();
	}

	@Test
	public void buildLeftClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action leftClickAction = ac.leftClickAt(widget);
		Assert.notNull(leftClickAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(leftClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(leftClickAction.get(Tags.AbstractIDCustom));
		Assert.notNull(leftClickAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildRightClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action rightClickAction = ac.rightClickAt(widget);
		Assert.notNull(rightClickAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(rightClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(rightClickAction.get(Tags.AbstractIDCustom));
		Assert.notNull(rightClickAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildLeftDoubleClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action leftDoubleClickAction = ac.leftDoubleClickAt(widget);
		Assert.notNull(leftDoubleClickAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(leftDoubleClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(leftDoubleClickAction.get(Tags.AbstractIDCustom));
		Assert.notNull(leftDoubleClickAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildDropwdownAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action dropdownAction = ac.dropDownAt(widget);
		Assert.notNull(dropdownAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(dropdownAction)));
		// To check that Action identifiers were built
		Assert.notNull(dropdownAction.get(Tags.AbstractIDCustom));
		Assert.notNull(dropdownAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildDragAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action dragAction = ac.dragFromTo(widget, widget);
		Assert.notNull(dragAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(dragAction)));
		// To check that Action identifiers were built
		Assert.notNull(dragAction.get(Tags.AbstractIDCustom));
		Assert.notNull(dragAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildSlideAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action slideAction = ac.slideFromTo(new AbsolutePosition(Point.from(0, 0)), new AbsolutePosition(Point.from(1, 1)), widget);
		Assert.notNull(slideAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(slideAction)));
		// To check that Action identifiers were built
		Assert.notNull(slideAction.get(Tags.AbstractIDCustom));
		Assert.notNull(slideAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildClickTypeAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action clickTypeAction = ac.clickTypeInto(widget, "bla", false);
		Assert.notNull(clickTypeAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(clickTypeAction)));
		// To check that Action identifiers were built
		Assert.notNull(clickTypeAction.get(Tags.AbstractIDCustom));
		Assert.notNull(clickTypeAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildClickTypeReplaceAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action clickTypeReplaceAction = ac.clickTypeInto(widget, "bla", true);
		Assert.notNull(clickTypeReplaceAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(clickTypeReplaceAction)));
		// To check that Action identifiers were built
		Assert.notNull(clickTypeReplaceAction.get(Tags.AbstractIDCustom));
		Assert.notNull(clickTypeReplaceAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildPasteAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action pasteAction = ac.pasteTextInto(widget, "pasted", false);
		Assert.notNull(pasteAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(pasteAction)));
		// To check that Action identifiers were built
		Assert.notNull(pasteAction.get(Tags.AbstractIDCustom));
		Assert.notNull(pasteAction.get(Tags.ConcreteIDCustom));
	}

	@Test
	public void buildPasteReplaceAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action pasteReplaceAction = ac.pasteTextInto(widget, "pasted", true);
		Assert.notNull(pasteReplaceAction.get(Tags.OriginWidget));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(pasteReplaceAction)));
		// To check that Action identifiers were built
		Assert.notNull(pasteReplaceAction.get(Tags.AbstractIDCustom));
		Assert.notNull(pasteReplaceAction.get(Tags.ConcreteIDCustom));
	}
}
