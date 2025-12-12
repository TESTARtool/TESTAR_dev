package org.testar.statemodel.changedetection;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.StateModelException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

public class GraphTraversalComparatorTest {

    private Map<String, String> descByAction;
    private ActionPrimaryKeyProvider descriptionProvider;

    @Before
    public void setup() {
        descByAction = new HashMap<>();
        descriptionProvider = id -> descByAction.getOrDefault(id, id);
    }

    @Test
    public void testUnchangedMatchesActionsByDescription() throws Exception {
        descByAction.put("a1", "click");
        descByAction.put("b1", "click"); // different id but same description

        AbstractStateModel oldModel = model("old", new String[]{"S1", "S2"}, new String[][]{{"S1", "a1", "S2"}});
        AbstractStateModel newModel = model("new", new String[]{"S1", "S2"}, new String[][]{{"S1", "b1", "S2"}});

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);

        assertTrue(result.getAddedStates().isEmpty());
        assertTrue(result.getRemovedStates().isEmpty());
        assertTrue(result.getChangedStates().isEmpty());
        assertTrue(result.getChangedActions().isEmpty());
    }

    @Test
    public void testDetectsChangedStates() throws Exception {
        AbstractStateModel oldModel = model("old", new String[]{"S1", "S2"}, new String[][]{{"S1", "a1", "S2"}});
        AbstractStateModel newModel = model("new", new String[]{"S1", "S3"}, new String[][]{{"S1", "a1", "S3"}});

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);

        assertTrue(result.getAddedStates().isEmpty());
        assertTrue(result.getRemovedStates().isEmpty());

        assertEquals(1, result.getChangedStates().size());
        assertTrue(result.getChangedStates().containsKey("S3"));
    }

    @Test
    public void testChangedStateDueToDifferentAction() throws Exception {
        AbstractStateModel oldModel = model("old", new String[]{"S1", "S2"}, new String[][]{{"S1", "a1", "S2"}});
        AbstractStateModel newModel = model("new", new String[]{"S1", "S3"}, new String[][]{{"S1", "b2", "S3"}});

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);

        // If the corresponding abstract states are different, or the abstract actions of the corresponding states do not match
        // The states contain changes

        assertEquals(1, result.getChangedStates().size());
        assertTrue(result.getChangedStates().containsKey("S1"));

        assertEquals(1, result.getAddedStates().size());
        assertTrue(result.getAddedStates().get(0).getStateId().equals("S3"));

        assertEquals(1, result.getRemovedStates().size());
        assertTrue(result.getRemovedStates().get(0).getStateId().equals("S2"));
    }

    @Test
    public void testDetectsStatePropertyChanges() throws Exception {
        AbstractStateModel oldModel = model("old", new String[]{"S1"}, new String[][]{{"S1", "a1", "S1"}});
        AbstractStateModel newModel = model("new", new String[]{"S1"}, new String[][]{{"S1", "a1", "S1"}});

        Tag<String> title = Tags.Title;
        oldModel.getState("S1").addAttribute(title, "Old");
        newModel.getState("S1").addAttribute(title, "New");

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);

        assertTrue(result.getChangedStates().containsKey("S1"));
        assertEquals(1, result.getChangedStates().get("S1").getChanged().size());
        assertEquals("Old", result.getChangedStates().get("S1").getChanged().get(0).getOldValue());
        assertEquals("New", result.getChangedStates().get("S1").getChanged().get(0).getNewValue());
    }

    @Test
    public void testDetectsAddedActionsOnMatchedStates() throws Exception {
        descByAction.put("a1", "click");
        descByAction.put("a2", "drag");

        AbstractStateModel oldModel = model("old", new String[]{"S1"}, new String[][]{{"S1", "a1", "S1"}});
        AbstractStateModel newModel = model("new", new String[]{"S1"}, new String[][]{{"S1", "a1", "S1"}, {"S1", "a2", "S1"}});

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);

        assertEquals(1, result.getChangedActions().size());
        ActionSetDiff diff = result.getChangedActions().get("S1");
        assertEquals(1, diff.getAddedOutgoing().size());
        assertEquals("a2", diff.getAddedOutgoing().get(0).getActionId());

        // If the corresponding abstract states are different, or the abstract actions of the corresponding states do not match
        // The states contain changes

        assertTrue(result.getChangedStates().containsKey("S1"));
    }

    @Test
    public void testHandledNodesAvoidsInfiniteLoops() throws Exception {
        AbstractStateModel oldModel = model("old", new String[]{"S1", "S2"}, new String[][]{
                {"S1", "a1", "S2"},
                {"S2", "a2", "S1"}
        });
        AbstractStateModel newModel = model("new", new String[]{"S1", "S2"}, new String[][]{
                {"S1", "a1", "S2"},
                {"S2", "a2", "S1"}
        });

        ChangeDetectionResult result = new GraphTraversalComparator(descriptionProvider).compare(oldModel, newModel);
        assertTrue(result.getAddedStates().isEmpty());
        assertTrue(result.getRemovedStates().isEmpty());
        assertTrue(result.getChangedStates().isEmpty());
        assertTrue(result.getChangedActions().isEmpty());
    }

    private AbstractStateModel model(String id, String[] states, String[][] transitions) throws StateModelException {
        AbstractStateModel model = new AbstractStateModel(id, "app", "1.0", Collections.singleton(Tags.Title));
        boolean first = true;
        for (String s : states) {
            AbstractState st = new AbstractState(s, new HashSet<>());
            st.setInitial(first);
            first = false;
            model.addState(st);
        }
        for (String[] t : transitions) {
            String source = t[0];
            String actionId = t[1];
            String target = t[2];
            model.addTransition(model.getState(source), model.getState(target), new AbstractAction(actionId));
        }
        return model;
    }

}
