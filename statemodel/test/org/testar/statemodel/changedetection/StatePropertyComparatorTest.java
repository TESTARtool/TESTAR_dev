package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractAction;

public class StatePropertyComparatorTest {

    @Test
    public void testUnchangedAttributes() {
        AbstractAction action = new AbstractAction("AA1");
        AbstractState oldState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));
        AbstractState newState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));

        Tag<String> title = Tag.from("title", String.class);

        oldState.addAttribute(title, "Home");
        newState.addAttribute(title, "Home");

        VertexPropertyDiff diff = StatePropertyComparator.compare(oldState, newState);

        assertEquals(0, diff.getChanged().size());
        assertEquals(0, diff.getAdded().size());
        assertEquals(0, diff.getRemoved().size());
    }

    @Test
    public void testChangedAttributes() {
        AbstractAction action = new AbstractAction("AA1");
        AbstractState oldState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));
        AbstractState newState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));

        Tag<String> title = Tag.from("title", String.class);

        oldState.addAttribute(title, "Old");
        newState.addAttribute(title, "New");

        VertexPropertyDiff diff = StatePropertyComparator.compare(oldState, newState);

        assertEquals(0, diff.getAdded().size());
        assertEquals(0, diff.getRemoved().size());

        assertEquals(1, diff.getChanged().size());
        assertEquals("title", diff.getChanged().get(0).getPropertyName());
        assertEquals("Old", diff.getChanged().get(0).getOldValue());
        assertEquals("New", diff.getChanged().get(0).getNewValue());
    }

    @Test
    public void testAddedAttribute() {
        AbstractAction action = new AbstractAction("AA1");
        AbstractState oldState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));
        AbstractState newState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));

        Tag<String> title = Tag.from("title", String.class);
        Tag<String> header = Tag.from("header", String.class);

        oldState.addAttribute(title, "Home");
        newState.addAttribute(title, "Home");
        newState.addAttribute(header, "NewHeader");

        VertexPropertyDiff diff = StatePropertyComparator.compare(oldState, newState);

        assertEquals(0, diff.getChanged().size());
        assertEquals(0, diff.getRemoved().size());

        assertEquals(1, diff.getAdded().size());
        assertEquals("header", diff.getAdded().get(0).getPropertyName());
        assertEquals("NewHeader", diff.getAdded().get(0).getNewValue());
    }

    @Test
    public void testRemovedAttribute() {
        AbstractAction action = new AbstractAction("AA1");
        AbstractState oldState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));
        AbstractState newState = new AbstractState("AS1", new HashSet<>(Collections.singleton(action)));

        Tag<String> title = Tag.from("title", String.class);
        Tag<String> header = Tag.from("header", String.class);

        oldState.addAttribute(title, "Home");
        oldState.addAttribute(header, "OldHeader");
        newState.addAttribute(title, "Home");

        VertexPropertyDiff diff = StatePropertyComparator.compare(oldState, newState);

        assertEquals(0, diff.getChanged().size());
        assertEquals(0, diff.getAdded().size());

        assertEquals(1, diff.getRemoved().size());
        assertEquals("header", diff.getRemoved().get(0).getPropertyName());
        assertEquals("OldHeader", diff.getRemoved().get(0).getOldValue());
    }

}
