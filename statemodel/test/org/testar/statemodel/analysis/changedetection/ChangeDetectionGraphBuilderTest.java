package org.testar.statemodel.analysis.changedetection;

import org.junit.Test;
import org.testar.statemodel.changedetection.ActionPrimaryKeyProvider;
import org.testar.statemodel.changedetection.ActionSetDiff;
import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.DeltaAction;
import org.testar.statemodel.changedetection.DeltaState;
import org.testar.statemodel.changedetection.DiffType;
import org.testar.statemodel.changedetection.PropertyDiff;
import org.testar.statemodel.changedetection.VertexPropertyDiff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ChangeDetectionGraphBuilderTest {

    private final ActionPrimaryKeyProvider pkProvider = new ActionPrimaryKeyProvider() {
        @Override
        public String getPrimaryKey(String actionId) {
            if(actionId.equals("actionId-11")) return "click Help";
            else if(actionId.equals("actionId-12")) return "click View";
            else return "click action";
        }
    };

    @Test
    public void testGraphJoinsActionEdgesByKeyProvider() {
        List<Map<String, Object>> oldElements = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> newElements = new ArrayList<Map<String, Object>>();
        oldElements.add(node("S1"));
        oldElements.add(node("S2"));
        newElements.add(node("S1"));
        newElements.add(node("S3"));

        oldElements.add(edge("S1", "S2", "A1"));
        newElements.add(edge("S1", "S3", "A2"));

        ChangeDetectionResult result = new ChangeDetectionResult(
                "old", "new",
                Arrays.asList(delta("S3")), // added states
                Arrays.asList(delta("S2")), // removed states
                Collections.<String, VertexPropertyDiff>emptyMap(),
                Collections.<String, ActionSetDiff>emptyMap());

        ChangeDetectionGraphBuilder builder = new ChangeDetectionGraphBuilder(pkProvider);
        List<Map<String, Object>> merged = builder.build("old", "new", oldElements, newElements, result);

        List<Map<String, Object>> actionEdges = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> el : merged) {
            if ("edges".equals(el.get("group"))) {
                actionEdges.add(el);
            }
        }

        assertEquals("Only one action edge should remain after merge process", 1, actionEdges.size());
        Map<String, Object> actionData = getData(actionEdges.get(0));
        assertEquals("unchanged", actionData.get("status"));
        assertEquals("S3", actionData.get("target"));
        assertEquals("click action", actionData.get("label"));
        assertEquals("click action", actionData.get("primaryKey"));

        for (Map<String, Object> e : actionEdges) {
            Map<String, Object> d = getData(e);
            assertFalse("No edge should be marked removed", "removed".equals(d.get("status")));
        }
    }

    @Test
    public void testGraphShowsAddedRemovedStates() {
        List<Map<String, Object>> oldElements = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> newElements = new ArrayList<Map<String, Object>>();
        oldElements.add(node("S1"));
        oldElements.add(node("S2"));
        newElements.add(node("S1"));
        newElements.add(node("S3"));

        oldElements.add(edge("S1", "S2", "actionId-11"));
        newElements.add(edge("S1", "S3", "actionId-12"));

        ChangeDetectionResult result = new ChangeDetectionResult(
                "old", "new",
                Arrays.asList(delta("S3")), // added states
                Arrays.asList(delta("S2")), // removed states
                Collections.<String, VertexPropertyDiff>emptyMap(),
                Collections.<String, ActionSetDiff>emptyMap());

        ChangeDetectionGraphBuilder builder = new ChangeDetectionGraphBuilder(pkProvider);
        List<Map<String, Object>> merged = builder.build("old", "new", oldElements, newElements, result);

        List<Map<String, Object>> actionEdges = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> el : merged) {
            if ("edges".equals(el.get("group"))) {
                actionEdges.add(el);
            }
        }

        assertEquals("Two action edges should remain after merge process", 2, actionEdges.size());
        Map<String, Object> actionDataOne = getData(actionEdges.get(0));
        Map<String, Object> actionDataTwo = getData(actionEdges.get(1));

        assertNotEquals("unchanged", actionDataOne.get("status"));
        assertNotEquals("unchanged", actionDataTwo.get("status"));

        assertNotEquals("changed", actionDataOne.get("status"));
        assertNotEquals("changed", actionDataTwo.get("status"));

        assertTrue("Edge status must be added or removed",
                "added".equals(actionDataOne.get("status")) || "removed".equals(actionDataOne.get("status")));
        assertTrue("Edge status must be added or removed",
                "added".equals(actionDataTwo.get("status")) || "removed".equals(actionDataTwo.get("status")));

        assertTrue("Edge target must be S2 or S3",
                "S2".equals(actionDataOne.get("target")) || "S3".equals(actionDataOne.get("target")));
        assertTrue("Edge target must be S2 or S3",
                "S2".equals(actionDataTwo.get("target")) || "S3".equals(actionDataTwo.get("target")));

        assertTrue("Edge label must be resolved to a primary key",
                "click Help".equals(actionDataOne.get("label")) || "click View".equals(actionDataOne.get("label")));
        assertTrue("Edge label must be resolved to a primary key",
                "click Help".equals(actionDataTwo.get("label")) || "click View".equals(actionDataTwo.get("label")));

        assertTrue("Edge primaryKey must be resolved to a primary key",
                "click Help".equals(actionDataOne.get("primaryKey")) || "click View".equals(actionDataOne.get("primaryKey")));
        assertTrue("Edge primaryKey must be resolved to a primary key",
                "click Help".equals(actionDataTwo.get("primaryKey")) || "click View".equals(actionDataTwo.get("primaryKey")));

        int addedCount = 0;
        int removedCount = 0;
        for (Map<String, Object> e : actionEdges) {
            Map<String, Object> d = getData(e);
            String status = (String) d.get("status");
            assertTrue("Edge status must be added or removed", "added".equals(status) || "removed".equals(status));
            if ("added".equals(status)) {
                addedCount++;
            } else if ("removed".equals(status)) {
                removedCount++;
            }
        }
        assertEquals("Expected exactly one added edge", 1, addedCount);
        assertEquals("Expected exactly one removed edge", 1, removedCount);
    }

    @Test
    public void testChangedStateShowsOldAndNewScreenshots() {
        List<Map<String, Object>> oldElements = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> newElements = new ArrayList<Map<String, Object>>();

        // Old: concrete -> abstract
        oldElements.add(abstractNode("nA_old", "SA_old"));
        oldElements.add(concreteNode("nC_old", "SC_old"));
        oldElements.add(isAbstractedBy("nC_old", "nA_old"));

        // New: concrete -> abstract
        newElements.add(abstractNode("nA_new", "SA_new"));
        newElements.add(concreteNode("nC_new", "SC_new"));
        newElements.add(isAbstractedBy("nC_new", "nA_new"));

        // Result contains a mapping as a stateId diff (old -> new)
        VertexPropertyDiff diff = new VertexPropertyDiff(
                Collections.<PropertyDiff>emptyList(),
                Collections.<PropertyDiff>emptyList(),
                Arrays.asList(new PropertyDiff("stateId", "SA_old", "SA_new", DiffType.CHANGED))
        );
        Map<String, VertexPropertyDiff> changedStates = new HashMap<String, VertexPropertyDiff>();
        changedStates.put("SA_new", diff);

        ChangeDetectionResult result = new ChangeDetectionResult(
                "old", "new",
                Collections.<DeltaState>emptyList(),
                Collections.<DeltaState>emptyList(),
                changedStates,
                Collections.<String, ActionSetDiff>emptyMap()
        );

        ChangeDetectionGraphBuilder builder = new ChangeDetectionGraphBuilder(actionId -> actionId);
        List<Map<String, Object>> merged = builder.build("old", "new", oldElements, newElements, result);

        Map<String, Object> newNode = findNode(merged, "SA_new");
        assertNotNull(newNode);
        Map<String, Object> data = getData(newNode);
        assertEquals("changed", data.get("status"));
        assertEquals("old/nC_old.png", data.get("oldScreenshot"));
        assertEquals("new/nC_new.png", data.get("newScreenshot"));
        assertEquals("new/nC_new.png", data.get("screenshot"));
        assertEquals("SA_old", data.get("oldStateId"));
    }

    @Test
    public void testAddedStateIsNotChanged() {
        List<Map<String, Object>> oldElements = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> newElements = new ArrayList<Map<String, Object>>();
        oldElements.add(node("S1"));
        newElements.add(node("S1"));
        newElements.add(node("S3"));

        newElements.add(edge("S1", "S3", "A11"));

        Map<String, ActionSetDiff> changedActions = new HashMap<String, ActionSetDiff>();
        changedActions.put("S3", new ActionSetDiff(
                Arrays.asList(new DeltaAction("A11", "click", DeltaAction.Direction.INCOMING)),
                Collections.<DeltaAction>emptyList(),
                Collections.<DeltaAction>emptyList(),
                Collections.<DeltaAction>emptyList()
        ));

        ChangeDetectionResult result = new ChangeDetectionResult(
                "old", "new",
                Arrays.asList(delta("S3")),
                Collections.<DeltaState>emptyList(),
                Collections.<String, VertexPropertyDiff>emptyMap(),
                changedActions
        );

        ChangeDetectionGraphBuilder builder = new ChangeDetectionGraphBuilder(pkProvider);
        List<Map<String, Object>> merged = builder.build("old", "new", oldElements, newElements, result);

        Map<String, Object> addedNode = findNode(merged, "S3");
        assertNotNull(addedNode);
        assertEquals("added", getData(addedNode).get("status"));
    }

    private Map<String, Object> node(String id) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);
        List<String> classes = new ArrayList<String>();
        classes.add("AbstractState");
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "nodes");
        el.put("data", data);
        el.put("classes", classes);
        return el;
    }

    private Map<String, Object> abstractNode(String rawId, String stateId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", rawId);
        data.put("stateId", stateId);
        List<String> classes = new ArrayList<String>();
        classes.add("AbstractState");
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "nodes");
        el.put("data", data);
        el.put("classes", classes);
        return el;
    }

    private Map<String, Object> concreteNode(String rawId, String stateId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", rawId);
        data.put("stateId", stateId);
        List<String> classes = new ArrayList<String>();
        classes.add("ConcreteState");
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "nodes");
        el.put("data", data);
        el.put("classes", classes);
        return el;
    }

    private Map<String, Object> isAbstractedBy(String rawConcreteId, String rawAbstractId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("source", rawConcreteId);
        data.put("target", rawAbstractId);
        List<String> classes = new ArrayList<String>();
        classes.add("isAbstractedBy");
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "edges");
        el.put("data", data);
        el.put("classes", classes);
        return el;
    }

    private Map<String, Object> edge(String source, String target, String actionId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("source", source);
        data.put("target", target);
        data.put("actionId", actionId);
        List<String> classes = new ArrayList<String>();
        classes.add("AbstractAction");
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "edges");
        el.put("data", data);
        el.put("classes", classes);
        return el;
    }

    private DeltaState delta(String stateId) {
        return new DeltaState(stateId, Collections.<String>emptyList(),
                Collections.<DeltaAction>emptyList(),
                Collections.<DeltaAction>emptyList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(Map<String, Object> el) {
        Object d = el.get("data");
        if (d instanceof Map) {
            return (Map<String, Object>) d;
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> findNode(List<Map<String, Object>> elements, String nodeId) {
        for (Map<String, Object> el : elements) {
            if (!"nodes".equals(el.get("group"))) {
                continue;
            }
            Map<String, Object> data = getData(el);
            if (nodeId.equals(data.get("id"))) {
                return el;
            }
        }
        return null;
    }

}
