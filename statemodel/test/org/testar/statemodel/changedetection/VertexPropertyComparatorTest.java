package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class VertexPropertyComparatorTest {

    @Test
    public void testUnchangedProperties() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("name", "Home");
        oldProps.put("id", "same");

        Map<String, String> newProps = new HashMap<>();
        newProps.put("name", "Home");
        newProps.put("id", "same");

        VertexPropertyDiff vertexDiff = VertexPropertyComparator.compare(oldProps, newProps);

        assertEquals(0, vertexDiff.getAdded().size());
        assertEquals(0, vertexDiff.getRemoved().size());
        assertEquals(0, vertexDiff.getChanged().size());
    }

    @Test
    public void testChangedProperties() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("name", "Home");
        oldProps.put("id", "same");

        Map<String, String> newProps = new HashMap<>();
        newProps.put("name", "Start");
        newProps.put("id", "same");

        VertexPropertyDiff vertexDiff = VertexPropertyComparator.compare(oldProps, newProps);

        assertEquals(0, vertexDiff.getAdded().size());
        assertEquals(0, vertexDiff.getRemoved().size());

        assertEquals(1, vertexDiff.getChanged().size());
        assertEquals("name", vertexDiff.getChanged().get(0).getPropertyName());
        assertEquals("Home", vertexDiff.getChanged().get(0).getOldValue());
        assertEquals("Start", vertexDiff.getChanged().get(0).getNewValue());
        assertEquals(DiffType.CHANGED, vertexDiff.getChanged().get(0).getDiffType());
    }

    @Test
    public void testAddedProperties() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("name", "Home");
        oldProps.put("id", "same");

        Map<String, String> newProps = new HashMap<>();
        newProps.put("name", "Home");
        newProps.put("id", "same");
        newProps.put("title", "Bank");

        VertexPropertyDiff vertexDiff = VertexPropertyComparator.compare(oldProps, newProps);

        assertEquals(0, vertexDiff.getChanged().size());
        assertEquals(0, vertexDiff.getRemoved().size());

        assertEquals(1, vertexDiff.getAdded().size());
        assertEquals("title", vertexDiff.getAdded().get(0).getPropertyName());
        assertEquals(null, vertexDiff.getAdded().get(0).getOldValue());
        assertEquals("Bank", vertexDiff.getAdded().get(0).getNewValue());
        assertEquals(DiffType.ADDED, vertexDiff.getAdded().get(0).getDiffType());
    }

    @Test
    public void testRemovedProperties() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("name", "Home");
        oldProps.put("id", "same");

        Map<String, String> newProps = new HashMap<>();
        newProps.put("name", "Home");

        VertexPropertyDiff vertexDiff = VertexPropertyComparator.compare(oldProps, newProps);

        assertEquals(0, vertexDiff.getChanged().size());
        assertEquals(0, vertexDiff.getAdded().size());

        assertEquals(1, vertexDiff.getRemoved().size());
        assertEquals("id", vertexDiff.getRemoved().get(0).getPropertyName());
        assertEquals("same", vertexDiff.getRemoved().get(0).getOldValue());
        assertEquals(null, vertexDiff.getRemoved().get(0).getNewValue());
        assertEquals(DiffType.REMOVED, vertexDiff.getRemoved().get(0).getDiffType());
    }

    @Test
    public void testNullAndEmptyValuesAsDifferent() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("title", null);

        Map<String, String> newProps = new HashMap<>();
        newProps.put("title", "");

        VertexPropertyDiff diff = VertexPropertyComparator.compare(oldProps, newProps);
        assertEquals(1, diff.getChanged().size());
        assertEquals("title", diff.getChanged().get(0).getPropertyName());
    }

    @Test
    public void testCustomIgnoreFilter() {
        Map<String, String> oldProps = new HashMap<>();
        oldProps.put("keep", "a");
        oldProps.put("skip_me", "old");

        Map<String, String> newProps = new HashMap<>();
        newProps.put("keep", "b");
        newProps.put("skip_me", "new");

        VertexPropertyDiff diff = VertexPropertyComparator.compare(oldProps, newProps, name -> !name.startsWith("skip"));
        assertEquals(1, diff.getChanged().size());
        assertEquals("keep", diff.getChanged().get(0).getPropertyName());
        assertTrue(diff.getAdded().isEmpty());
        assertTrue(diff.getRemoved().isEmpty());
    }

}
