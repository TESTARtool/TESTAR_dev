package org.testar.statemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.exceptions.StateNotFoundException;

public class AbstractStateModelTest {

    private AbstractStateModel model;
    private StateModelEventListener listener;

    @Before
    public void setUp() {
        listener = new TestListener();
        model = new AbstractStateModel("model-001", "TestApp", "1.0", new HashSet<>(), listener);
    }

    @Test
    public void testAbstractStateModelTestConstructor() {
        assertEquals("model-001", model.getModelIdentifier());
        assertEquals("TestApp", model.getApplicationName());
        assertEquals("1.0", model.getApplicationVersion());
        assertNotNull(model.getStates());
        assertTrue(model.getStates().isEmpty());
        assertEquals(StateModelEventType.ABSTRACT_STATE_MODEL_INITIALIZED, ((TestListener)listener).lastEventType);
    }

    @Test
    public void testAddAndGetState() throws Exception {
        AbstractState state = new AbstractState("S1", Collections.emptySet());
        model.addState(state);

        assertTrue(model.containsState("S1"));
        assertEquals(state, model.getState("S1"));
    }

    @Test(expected = StateNotFoundException.class)
    public void testGetStateThrowsWhenMissing() throws Exception {
        model.getState("missing");
    }

    @Test
    public void testAddTransitionAndPreventDuplicate() throws Exception {
        AbstractState s1 = new AbstractState("S1", Collections.emptySet());
        AbstractState s2 = new AbstractState("S2", Collections.emptySet());
        AbstractAction action = new AbstractAction("A1");

        model.addTransition(s1, s2, action);
        assertEquals(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, ((TestListener)listener).lastEventType);

        // Add same transition again – should emit a CHANGED event
        model.addTransition(s1, s2, action);
        assertEquals(StateModelEventType.ABSTRACT_STATE_TRANSITION_CHANGED, ((TestListener)listener).lastEventType);

        Set<AbstractStateTransition> outgoing = model.getOutgoingTransitionsForState("S1");
        assertNotNull(outgoing);
        assertEquals(1, outgoing.size());
    }

    @Test
    public void testAddInitialState() throws Exception {
        AbstractState state = new AbstractState("INIT", Collections.emptySet());
        state.setInitial(true);
        model.addState(state);
        assertTrue(state.isInitial());
        // No exception = pass
    }

    @Test
    public void testTagsGetter() {
        assertNotNull(model.getTags());
        assertTrue(model.getTags().isEmpty());
    }

    @Test
    public void testAddEventListener() {
        StateModelEventListener another = new TestListener();
        model.addEventListener(another); // No exception = pass
    }

    class TestListener implements StateModelEventListener {
        public StateModelEventType lastEventType;
        public boolean listening = true;

        @Override
        public void eventReceived(StateModelEvent event) {
            this.lastEventType = event.getEventType();
        }

        @Override
        public void setListening(boolean listening) {
            this.listening = listening;
        }
    }
}
