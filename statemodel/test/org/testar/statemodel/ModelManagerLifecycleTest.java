package org.testar.statemodel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.core.verdict.Verdict;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ModelManagerLifecycleTest {

    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream output;

    @Before
    public void captureLifecycleOutput() {
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    @After
    public void restoreOutput() {
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    public void stoppingSequenceForwardsEveryFinalVerdict() {
        SequenceManager sequences = mock(SequenceManager.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                mock(PersistenceManager.class), sequences, false);
        List<Verdict> verdicts = List.of(new Verdict(Verdict.Severity.LLM_COMPLETE, "Goal achieved."), Verdict.OK);

        manager.notifyTestSequenceStopped(verdicts);

        verify(sequences).stopSequence(verdicts);
    }

    @Test
    public void shutdownFlushesPersistenceBeforeExportAndOnlyRunsOnce() {
        PersistenceManager persistence = mock(PersistenceManager.class);
        Runnable export = mock(Runnable.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                persistence, mock(SequenceManager.class), false, export);
        doAnswer(invocation -> {
            String console = output.toString(StandardCharsets.UTF_8);
            assertTrue(console.contains(ModelManager.FINALIZATION_STARTED_SIGNAL));
            assertTrue(console.contains("flushing persistence and closing the datastore"));
            assertFalse(console.contains(ModelManager.FINALIZATION_FINISHED_SIGNAL));
            return null;
        }).when(persistence).shutdown();
        doAnswer(invocation -> {
            assertFalse(output.toString(StandardCharsets.UTF_8).contains(ModelManager.FINALIZATION_FINISHED_SIGNAL));
            return null;
        }).when(export).run();

        manager.notifyTestingEnded();
        manager.notifyTestingEnded();

        InOrder order = inOrder(persistence, export);
        order.verify(persistence).shutdown();
        order.verify(export).run();
        verify(persistence, times(1)).shutdown();
        verify(export, times(1)).run();
        String console = output.toString(StandardCharsets.UTF_8);
        assertEquals(1L, console.lines().filter(ModelManager.FINALIZATION_STARTED_SIGNAL::equals).count());
        assertEquals(1L, console.lines().filter(ModelManager.FINALIZATION_FINISHED_SIGNAL::equals).count());
        assertTrue(console.contains("State model finalization finished."));
    }

    @Test
    public void failedPersistenceShutdownCanBeRetriedWithoutExportingUnflushedData() {
        PersistenceManager persistence = mock(PersistenceManager.class);
        Runnable export = mock(Runnable.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                persistence, mock(SequenceManager.class), false, export);
        doThrow(new IllegalStateException("flush failed")).doNothing().when(persistence).shutdown();

        try {
            manager.notifyTestingEnded();
            fail("Persistence failure should remain visible to the caller");
        } catch (IllegalStateException exception) {
            assertEquals("flush failed", exception.getMessage());
        }
        verifyNoInteractions(export);
        String failedOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(failedOutput.contains(ModelManager.FINALIZATION_FINISHED_SIGNAL));
        assertFalse(failedOutput.contains("State model finalization finished."));

        manager.notifyTestingEnded();
        verify(persistence, times(2)).shutdown();
        verify(export).run();
        String console = output.toString(StandardCharsets.UTF_8);
        assertEquals(2L, console.lines().filter(ModelManager.FINALIZATION_STARTED_SIGNAL::equals).count());
        assertEquals(2L, console.lines().filter(ModelManager.FINALIZATION_FINISHED_SIGNAL::equals).count());
    }

    @Test
    public void exportFailureEndsFinalizationWithoutRepeatingClosedPersistence() {
        PersistenceManager persistence = mock(PersistenceManager.class);
        Runnable export = mock(Runnable.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                persistence, mock(SequenceManager.class), false, export);
        doThrow(new IllegalStateException("export failed")).when(export).run();

        try {
            manager.notifyTestingEnded();
            fail("Export failure should remain visible to the caller");
        } catch (IllegalStateException exception) {
            assertEquals("export failed", exception.getMessage());
        }
        manager.notifyTestingEnded();

        verify(persistence).shutdown();
        verify(export).run();
        String console = output.toString(StandardCharsets.UTF_8);
        assertEquals(1L, console.lines().filter(ModelManager.FINALIZATION_STARTED_SIGNAL::equals).count());
        assertEquals(1L, console.lines().filter(ModelManager.FINALIZATION_FINISHED_SIGNAL::equals).count());
        assertFalse(console.contains("State model finalization finished."));
    }

    @Test
    public void finalizationAlsoClosesPersistenceWhenExportIsDisabled() {
        PersistenceManager persistence = mock(PersistenceManager.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                persistence, mock(SequenceManager.class), false);

        manager.notifyTestingEnded();

        verify(persistence).shutdown();
        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains(ModelManager.FINALIZATION_STARTED_SIGNAL));
        assertTrue(console.contains("State model finalization finished."));
        assertTrue(console.contains(ModelManager.FINALIZATION_FINISHED_SIGNAL));
    }
}
