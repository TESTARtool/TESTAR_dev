package org.testar.statemodel.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.event.StateModelEventListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class SequenceManagerTest {

    private List<StateModelEvent> events;
    private SequenceManager manager;

    @Before
    public void createSequenceManager() {
        events = new ArrayList<>();
        StateModelEventListener listener = mock(StateModelEventListener.class);
        doAnswer(invocation -> {
            events.add(invocation.getArgument(0));
            return null;
        }).when(listener).eventReceived(any());
        manager = new SequenceManager(Set.of(listener), "model-1");
    }

    @Test
    public void finalVerdictsLinkToTheLastOccurrenceOfARepeatedState() {
        AbstractState abstractState = new AbstractState("SA1", Set.of());
        ConcreteState state = new ConcreteState("SC1", abstractState);
        ConcreteAction action = new ConcreteAction("AC1", new AbstractAction("AA1"));
        action.addAttribute(Tags.Desc, "Click again");
        List<Verdict> verdicts = new ArrayList<>(List.of(
                new Verdict(Verdict.Severity.LLM_COMPLETE, "Organisation mode changed."),
                new Verdict(Verdict.Severity.WARNING, "Additional observation.")));

        manager.startNewSequence();
        manager.notifyStateReached(state, null);
        manager.notifyStateReached(state, action);
        manager.stopSequence(verdicts);
        Sequence sequence = (Sequence) events.get(events.size() - 1).getPayload();

        assertEquals(StateModelEventType.SEQUENCE_ENDED, events.get(events.size() - 1).getEventType());
        assertEquals(sequence.getLastNode().getNodeId(), sequence.getFinalStateOccurrenceId());
        assertFalse(sequence.getFirstNode().getNodeId().equals(sequence.getFinalStateOccurrenceId()));
        assertEquals(verdicts, sequence.getFinalVerdicts());
        assertEquals(SequenceVerdict.COMPLETED_SUCCESFULLY, sequence.getSequenceVerdict());
        assertNull(state.getAttributes().get(Tags.OracleVerdicts, null));

        verdicts.clear();
        manager.stopSequence(List.of(Verdict.FAIL));
        assertEquals(2, sequence.getFinalVerdicts().size());
        assertEquals(Verdict.Severity.LLM_COMPLETE.getValue(), sequence.getFinalVerdicts().get(0).severity(), 0.0);
    }

    @Test
    public void sequencesSharingAConcreteStateKeepIndependentFinalDecisions() {
        ConcreteState state = new ConcreteState("SC1", new AbstractState("SA1", Set.of()));
        manager.startNewSequence();
        manager.notifyStateReached(state, null);
        manager.stopSequence(List.of(new Verdict(Verdict.Severity.LLM_COMPLETE, "First goal passed.")));
        Sequence first = (Sequence) events.get(events.size() - 1).getPayload();

        manager.startNewSequence();
        manager.notifyStateReached(state, null);
        manager.stopSequence(List.of(new Verdict(Verdict.Severity.LLM_INVALID, "Second goal failed.")));
        Sequence second = (Sequence) events.get(events.size() - 1).getPayload();

        assertFalse(first.getFinalStateOccurrenceId().equals(second.getFinalStateOccurrenceId()));
        assertEquals("LLM_COMPLETE", first.getFinalVerdicts().get(0).verdictSeverityTitle());
        assertEquals("LLM_INVALID", second.getFinalVerdicts().get(0).verdictSeverityTitle());
        assertNull(state.getAttributes().get(Tags.OracleVerdicts, null));
    }

    @Test
    public void emptySequenceRetainsVerdictsWithoutInventingAStateOccurrence() {
        manager.startNewSequence();
        manager.stopSequence(List.of(Verdict.FAIL));
        Sequence sequence = (Sequence) events.get(events.size() - 1).getPayload();

        assertEquals(List.of(Verdict.FAIL), sequence.getFinalVerdicts());
        assertNull(sequence.getFinalStateOccurrenceId());
    }

    @Test
    public void stopWithoutFinalVerdictsKeepsAnEmptyDecisionList() {
        manager.stopSequence();
        manager.startNewSequence();
        manager.stopSequence();
        Sequence sequence = (Sequence) events.get(events.size() - 1).getPayload();

        assertTrue(sequence.getFinalVerdicts().isEmpty());
        assertNull(sequence.getFinalStateOccurrenceId());
    }
}
