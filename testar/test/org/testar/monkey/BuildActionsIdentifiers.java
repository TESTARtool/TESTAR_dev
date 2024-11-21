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

	private static String pathTest = "[0,0,1]";

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
		widget.set(Tags.Path, pathTest);

		// Build widget and state identifiers
		defaultProtocol.buildStateIdentifiers(state);

		// To derive actions (such as clicks, drag&drop, typing ...) we should create an action compiler.
		ac = new AnnotatingActionCompiler();
	}

	@Test
	public void buildLeftClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action leftClickAction = ac.leftClickAt(widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(leftClickAction.get(Tags.OriginWidget));
		Assert.isTrue(leftClickAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Left"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(leftClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(leftClickAction.get(Tags.AbstractID));
		Assert.notNull(leftClickAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildRightClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action rightClickAction = ac.rightClickAt(widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(rightClickAction.get(Tags.OriginWidget));
		Assert.isTrue(rightClickAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Right"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(rightClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(rightClickAction.get(Tags.AbstractID));
		Assert.notNull(rightClickAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildLeftDoubleClickAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action leftDoubleClickAction = ac.leftDoubleClickAt(widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(leftDoubleClickAction.get(Tags.OriginWidget));
		Assert.isTrue(leftDoubleClickAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Double-Click"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(leftDoubleClickAction)));
		// To check that Action identifiers were built
		Assert.notNull(leftDoubleClickAction.get(Tags.AbstractID));
		Assert.notNull(leftDoubleClickAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildDropwdownAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action dropdownAction = ac.dropDownAt(widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(dropdownAction.get(Tags.OriginWidget));
		Assert.isTrue(dropdownAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Dropdown"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(dropdownAction)));
		// To check that Action identifiers were built
		Assert.notNull(dropdownAction.get(Tags.AbstractID));
		Assert.notNull(dropdownAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildDragAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action dragAction = ac.dragFromTo(widget, widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(dragAction.get(Tags.OriginWidget));
		Assert.isTrue(dragAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Drag"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(dragAction)));
		// To check that Action identifiers were built
		Assert.notNull(dragAction.get(Tags.AbstractID));
		Assert.notNull(dragAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildSlideAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action slideAction = ac.slideFromTo(new AbsolutePosition(Point.from(0, 0)), new AbsolutePosition(Point.from(1, 1)), widget);
		// Verify Action <-> Widget mapping
		Assert.notNull(slideAction.get(Tags.OriginWidget));
		Assert.isTrue(slideAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Drag"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(slideAction)));
		// To check that Action identifiers were built
		Assert.notNull(slideAction.get(Tags.AbstractID));
		Assert.notNull(slideAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildClickTypeAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action clickTypeAction = ac.clickTypeInto(widget, "bla", false);
		// Verify Action <-> Widget mapping
		Assert.notNull(clickTypeAction.get(Tags.OriginWidget));
		Assert.isTrue(clickTypeAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Type"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(clickTypeAction)));
		// To check that Action identifiers were built
		Assert.notNull(clickTypeAction.get(Tags.AbstractID));
		Assert.notNull(clickTypeAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildClickTypeReplaceAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action clickTypeReplaceAction = ac.clickTypeInto(widget, "bla", true);
		// Verify Action <-> Widget mapping
		Assert.notNull(clickTypeReplaceAction.get(Tags.OriginWidget));
		Assert.isTrue(clickTypeReplaceAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Type"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(clickTypeReplaceAction)));
		// To check that Action identifiers were built
		Assert.notNull(clickTypeReplaceAction.get(Tags.AbstractID));
		Assert.notNull(clickTypeReplaceAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildPasteAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action pasteAction = ac.pasteTextInto(widget, "pasted", false);
		// Verify Action <-> Widget mapping
		Assert.notNull(pasteAction.get(Tags.OriginWidget));
		Assert.isTrue(pasteAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Paste Text"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(pasteAction)));
		// To check that Action identifiers were built
		Assert.notNull(pasteAction.get(Tags.AbstractID));
		Assert.notNull(pasteAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildPasteReplaceAction() {
		// Create the action and Assert that the widget is set as the OriginWidget Tag 
		Action pasteReplaceAction = ac.pasteTextInto(widget, "pasted", true);
		// Verify Action <-> Widget mapping
		Assert.notNull(pasteReplaceAction.get(Tags.OriginWidget));
		Assert.isTrue(pasteReplaceAction.get(Tags.OriginWidget).get(Tags.Path).equals(pathTest));
		Assert.notNull(widget.get(Tags.ActionSet));
		Assert.isTrue(widget.get(Tags.ActionSet).size() == 1);
		Assert.isTrue(widget.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("Paste Text"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(pasteReplaceAction)));
		// To check that Action identifiers were built
		Assert.notNull(pasteReplaceAction.get(Tags.AbstractID));
		Assert.notNull(pasteReplaceAction.get(Tags.ConcreteID));
	}

	@Test
	public void buildNoOperationalStateAction() {
		// Create the NOP action and Assert that the state is set as the OriginWidget Tag 
		Action NOPAction = ac.noOperationalState(state);
		// Verify Action <-> Widget mapping
		Assert.notNull(NOPAction.get(Tags.OriginWidget));
		Assert.notNull(state.get(Tags.ActionSet));
		Assert.isTrue(state.get(Tags.ActionSet).size() == 1);
		Assert.notNull(state.get(Tags.ActionSet).iterator().next().get(Tags.Desc));
		Assert.isTrue(state.get(Tags.ActionSet).iterator().next().get(Tags.Desc, "").contains("No Operation"));
		// Then build the action identifiers
		defaultProtocol.buildStateActionsIdentifiers(state, new HashSet<>(Collections.singletonList(NOPAction)));
		// To check that Action identifiers were built
		Assert.notNull(NOPAction.get(Tags.AbstractID));
		Assert.notNull(NOPAction.get(Tags.ConcreteID));
	}
}
