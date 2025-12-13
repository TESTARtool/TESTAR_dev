package org.testar;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.PasteText;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestCodingManager {

	private static StateStub state = new StateStub();
	private static WidgetStub widget = new WidgetStub();

	@BeforeClass
	public static void initializeCodingIDs() {
		Tag<?>[] abstractTags = new Tag<?>[]{Tags.Role, Tags.Path};
		CodingManager.setCustomTagsForAbstractId(abstractTags);

		Tag<?>[] concreteTags = new Tag<?>[]{Tags.Role, Tags.Title, Tags.Path};
		CodingManager.setCustomTagsForConcreteId(concreteTags);

		// Create the sample widget and state
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, Roles.Button);
		widget.set(Tags.Title, "Submit");
		widget.set(Tags.Path, "0,0,1");
	}

	@Test
	public void testInitialCodingIDs() {
		Assert.assertEquals(CodingManager.getDefaultAbstractStateTags()[0].toString(), "Widget control type");

		Assert.assertEquals(CodingManager.getCustomTagsForAbstractId().length, 2);
		Assert.assertEquals(Arrays.toString(CodingManager.getCustomTagsForAbstractId()), "[Path, Role]");

		Assert.assertEquals(CodingManager.getCustomTagsForConcreteId().length, 3);
		Assert.assertEquals(Arrays.toString(CodingManager.getCustomTagsForConcreteId()), "[Path, Role, Title]");
	}

	@Test
	public void testWidgetCodingIDs() {
		// Build and check IDs for the widget are set correctly
		CodingManager.buildIDs(widget);
		Assert.assertEquals(widget.get(Tags.AbstractID), "WAane37vb337119275");
		Assert.assertEquals(widget.get(Tags.ConcreteID), "WCxrhgw3113942939805");
	}

	@Test
	public void testStateCodingIDs() {
		// Build and check IDs for the state are set correctly
		CodingManager.buildIDs(state);
		Assert.assertEquals(state.get(Tags.AbstractID), "SA1fl7scw122940428572");
		Assert.assertEquals(state.get(Tags.ConcreteID), "SCr5r0gz142938361104");
	}

	@Test
	public void testActionCodingIDs() {
		Action action = new PasteText("paste");
		action.set(Tags.OriginWidget, widget);

		// Build and check IDs for the action are set correctly
		CodingManager.buildIDs(state, Collections.singleton(action));
		Assert.assertEquals(action.get(Tags.AbstractID), "AA15k44gp2d1871300549");
		Assert.assertEquals(action.get(Tags.ConcreteID), "AC1dn3okk412646313092");
	}
}
