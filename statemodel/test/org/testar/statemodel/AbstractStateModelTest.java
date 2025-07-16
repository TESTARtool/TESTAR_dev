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

        // Add same transition again it should emit a CHANGED event
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

    @Test(expected = NullPointerException.class)
    public void testConstructorNullModelIdentifier() {
        new AbstractStateModel(null, "App", "1.0", new HashSet<>(), listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyModelIdentifier() {
        new AbstractStateModel("   ", "App", "1.0", new HashSet<>(), listener);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullAppName() {
        new AbstractStateModel("model", null, "1.0", new HashSet<>(), listener);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullAppVersion() {
        new AbstractStateModel("model", "App", null, new HashSet<>(), listener);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullTags() {
        new AbstractStateModel("model", "App", "1.0", null, listener);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullEventListener() {
        new AbstractStateModel("model", "App", "1.0", new HashSet<>(), (StateModelEventListener[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddTransitionNullSource() throws Exception {
        model.addTransition(null, new AbstractState("S2", Collections.emptySet()), new AbstractAction("A1"));
    }

    @Test(expected = NullPointerException.class)
    public void testAddTransitionNullTarget() throws Exception {
        model.addTransition(new AbstractState("S1", Collections.emptySet()), null, new AbstractAction("A1"));
    }

    @Test(expected = NullPointerException.class)
    public void testAddTransitionNullAction() throws Exception {
        model.addTransition(new AbstractState("S1", Collections.emptySet()), new AbstractState("S2", Collections.emptySet()), null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetStateWithNullId() throws Exception {
        model.getState(null);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsStateWithNullId() {
        model.containsState(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetOutgoingTransitionsNullId() {
        model.getOutgoingTransitionsForState(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetIncomingTransitionsNullId() {
        model.getIncomingTransitionsForState(null);
    }

    @Test
    public void testGetTransitionsForMissingStateReturnsEmptySet() {
        Set<AbstractStateTransition> out = model.getOutgoingTransitionsForState("unknown");
        Set<AbstractStateTransition> in = model.getIncomingTransitionsForState("unknown");

        assertNotNull(out);
        assertNotNull(in);
        assertTrue(out.isEmpty());
        assertTrue(in.isEmpty());
    }

    @Test
    public void testMultipleListenersReceiveEvents() throws Exception {
        TestListener extraListener = new TestListener();
        model.addEventListener(extraListener);

        AbstractState s1 = new AbstractState("X1", Collections.emptySet());
        AbstractState s2 = new AbstractState("X2", Collections.emptySet());
        AbstractAction action = new AbstractAction("A1");

        model.addTransition(s1, s2, action);

        assertEquals(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, ((TestListener) listener).lastEventType);
        assertEquals(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, extraListener.lastEventType);
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
