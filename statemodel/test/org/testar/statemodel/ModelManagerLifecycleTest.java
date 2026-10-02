package org.testar.statemodel;

import org.junit.Test;
import org.mockito.InOrder;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ModelManagerLifecycleTest {

    @Test
    public void shutdownFlushesPersistenceBeforeExportAndOnlyRunsOnce() {
        PersistenceManager persistence = mock(PersistenceManager.class);
        Runnable export = mock(Runnable.class);
        ModelManager manager = new ModelManager(mock(AbstractStateModel.class), mock(ActionSelector.class),
                persistence, mock(SequenceManager.class), false, export);

        manager.notifyTestingEnded();
        manager.notifyTestingEnded();

        InOrder order = inOrder(persistence, export);
        order.verify(persistence).shutdown();
        order.verify(export).run();
        verify(persistence, times(1)).shutdown();
        verify(export, times(1)).run();
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

        manager.notifyTestingEnded();
        verify(persistence, times(2)).shutdown();
        verify(export).run();
    }
}
