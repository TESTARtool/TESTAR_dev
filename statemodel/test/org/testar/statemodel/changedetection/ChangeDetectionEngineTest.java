package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.StateModelException;

public class ChangeDetectionEngineTest {

    private ChangeDetectionEngine engine;

    @Before
    public void setUp() {
        ActionDescriptionProvider descriptionProvider = id -> "desc-" + id;
        engine = new ChangeDetectionEngine(descriptionProvider, new StateDifferenceFinder());
    }

    @Test
    public void testDetectsAddedState() throws StateModelException {
        AbstractStateModel oldModel = createModel("old-id",
                new String[]{"AS1"}, new String[][]{}, new String[][]{});

        AbstractStateModel newModel = createModel("new-id",
                new String[]{"AS1", "AS2"}, new String[][]{}, new String[][]{});

        ChangeDetectionResult result = engine.compare(oldModel, newModel);

        assertEquals("old-id", result.getOldModelIdentifier());
        assertEquals("new-id", result.getNewModelIdentifier());
        assertEquals(1, result.getAddedStates().size());
        assertEquals("AS2", result.getAddedStates().get(0).getStateId());

        assertEquals(0, result.getRemovedStates().size());

        assertTrue(result.getChangedStates().isEmpty());
        assertTrue(result.getChangedActions().isEmpty());

        assertTrue(result.hasDifferences());
    }

    @Test
    public void testDetectsRemovedState() throws StateModelException {
        AbstractStateModel oldModel = createModel("old-id",
                new String[]{"AS1", "AS2"}, new String[][]{}, new String[][]{});

        AbstractStateModel newModel = createModel("new-id",
                new String[]{"AS2"}, new String[][]{}, new String[][]{});

        ChangeDetectionResult result = engine.compare(oldModel, newModel);

        assertEquals("old-id", result.getOldModelIdentifier());
        assertEquals("new-id", result.getNewModelIdentifier());
        assertEquals(1, result.getRemovedStates().size());
        assertEquals("AS1", result.getRemovedStates().get(0).getStateId());

        assertEquals(0, result.getAddedStates().size());

        assertTrue(result.getChangedStates().isEmpty());
        assertTrue(result.getChangedActions().isEmpty());

        assertTrue(result.hasDifferences());
    }

    @Test
    public void testDetectsChangedState() throws StateModelException {
        AbstractStateModel oldModel = createModel("old-id",
                new String[]{"AS1"}, new String[][]{}, new String[][]{});
        AbstractStateModel newModel = createModel("new-id",
                new String[]{"AS1"}, new String[][]{}, new String[][]{});

        Tag<String> title = Tag.from("title", String.class);
        oldModel.getState("AS1").addAttribute(title, "Old");
        newModel.getState("AS1").addAttribute(title, "New");

        ChangeDetectionResult result = engine.compare(oldModel, newModel);

        assertEquals(1, result.getChangedStates().size());
        assertTrue(result.getChangedStates().containsKey("AS1"));

        VertexPropertyDiff diff = result.getChangedStates().get("AS1");
        assertEquals(1, diff.getChanged().size());
        assertEquals("title", diff.getChanged().get(0).getPropertyName());
        assertEquals("Old", diff.getChanged().get(0).getOldValue());
        assertEquals("New", diff.getChanged().get(0).getNewValue());
    }

    @Test
    public void testDetectsChangedAction() throws StateModelException {
        AbstractStateModel oldModel = createModel("old-id",
                new String[]{"AS1"}, new String[][]{{"AS1", "AA1", "AS1"}}, new String[][]{});
        AbstractStateModel newModel = createModel("new-id",
                new String[]{"AS1"}, new String[][]{{"AS1", "AA2", "AS1"}}, new String[][]{});

        ChangeDetectionResult result = engine.compare(oldModel, newModel);

        assertEquals(1, result.getChangedActions().size());

        ActionSetDiff diff = result.getChangedActions().get("AS1");
        assertEquals(1, diff.getAddedOutgoing().size());
        assertEquals("AA2", diff.getAddedOutgoing().get(0).getActionId());
        assertEquals(1, diff.getRemovedOutgoing().size());
        assertEquals("AA1", diff.getRemovedOutgoing().get(0).getActionId());
    }

    private AbstractStateModel createModel(String modelId,
                                           String[] stateIds,
                                           String[][] transitions,
                                           String[][] concreteIdsPerState
    ) throws StateModelException {

        AbstractStateModel model = new AbstractStateModel(
            modelId, 
            "app", 
            "1.0",
            Collections.singleton(Tags.Title)
        );

        // create states with no actions;
        for (String s : stateIds) {
            model.addState(new AbstractState(s, new HashSet<>()));
        }

        // add action transitions as in/out sets
        for (String[] t : transitions) {
            String source = t[0];
            String actionId = t[1];
            String target = t[2];
            AbstractAction action = new AbstractAction(actionId);
            model.addTransition(model.getState(source), model.getState(target), action);
        }

        // add concrete ids
        for (String[] entry : concreteIdsPerState) {
            String stateId = entry[0];
            AbstractState state = model.getState(stateId);
            for (int i = 1; i < entry.length; i++) {
                state.addConcreteStateId(entry[i]);
            }
        }

        return model;
    }

}
