package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.StateModelException;

public class ChangeDetectionEngineFactoryTest {

    @Test
    public void testEngineFactoryWithDefaultProvider() throws StateModelException {
        ChangeDetectionEngine engine = ChangeDetectionEngineFactory.createWithDefaultDescription();

        AbstractStateModel oldModel = createModel("old", new String[]{"AS1"}, new String[][]{}, new String[][]{});
        AbstractStateModel newModel = createModel("new", new String[]{"AS1", "AS2"}, new String[][]{}, new String[][]{});

        ChangeDetectionResult result = engine.compare(oldModel, newModel);
        assertEquals(1, result.getAddedStates().size());
        assertTrue(result.getChangedActions().isEmpty());
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
