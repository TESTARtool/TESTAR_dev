package org.testar.core;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.alayer.Roles;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.action.PasteText;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestCodingManager {

    private StateStub state;
    private WidgetStub widget;

    @Before
    public void initializeCodingIDs() {
        state = new StateStub();
        widget = new WidgetStub();
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
        CodingManager.buildIDs(state);
        Action action = new PasteText("paste");
        action.set(Tags.OriginWidget, widget);

        // Build and check IDs for the action are set correctly
        CodingManager.buildIDs(state, Collections.singleton(action));
        Assert.assertTrue(action.get(Tags.AbstractID).startsWith("AA"));
        Assert.assertTrue(action.get(Tags.ConcreteID).startsWith("AC"));
        Action equivalentAction = new PasteText("paste");
        equivalentAction.mapOriginWidget(widget);
        CodingManager.buildIDs(state, Collections.singleton(equivalentAction));
        Assert.assertEquals(action.get(Tags.AbstractID), equivalentAction.get(Tags.AbstractID));
        Assert.assertEquals(action.get(Tags.ConcreteID), equivalentAction.get(Tags.ConcreteID));
    }
}
