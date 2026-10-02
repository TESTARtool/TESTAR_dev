package org.testar.core;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.After;
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
    private Tag<?>[] previousAbstractTags;
    private Tag<?>[] previousConcreteTags;

    @Before
    public void initializeCodingIDs() {
        previousAbstractTags = CodingManager.getCustomTagsForAbstractId();
        previousConcreteTags = CodingManager.getCustomTagsForConcreteId();
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

    @After
    public void restoreConfiguration() {
        CodingManager.setCustomTagsForAbstractId(previousAbstractTags);
        CodingManager.setCustomTagsForConcreteId(previousConcreteTags);
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
        String abstractId = widget.get(Tags.AbstractID);
        String concreteId = widget.get(Tags.ConcreteID);
        Assert.assertTrue(abstractId.startsWith("WA"));
        Assert.assertTrue(concreteId.startsWith("WC"));
        CodingManager.buildIDs(widget);
        Assert.assertEquals(abstractId, widget.get(Tags.AbstractID));
        Assert.assertEquals(concreteId, widget.get(Tags.ConcreteID));

        widget.set(Tags.Title, "Cancel");
        CodingManager.buildIDs(widget);
        Assert.assertEquals(abstractId, widget.get(Tags.AbstractID));
        Assert.assertNotEquals(concreteId, widget.get(Tags.ConcreteID));
    }

    @Test
    public void testStateCodingIDs() {
        // Build and check IDs for the state are set correctly
        CodingManager.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);
        Assert.assertTrue(abstractId.startsWith("SA"));
        Assert.assertTrue(concreteId.startsWith("SC"));
        CodingManager.buildIDs(state);
        Assert.assertEquals(abstractId, state.get(Tags.AbstractID));
        Assert.assertEquals(concreteId, state.get(Tags.ConcreteID));
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
