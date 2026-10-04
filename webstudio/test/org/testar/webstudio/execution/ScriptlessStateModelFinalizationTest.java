package org.testar.webstudio.execution;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ModelManager;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.analysis.export.StaticGraphExporter;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.SequenceOutcomeDto;

public class ScriptlessStateModelFinalizationTest {

    @Test
    public void realModelLifecycleProtectsQuietShutdownAndExportFromIdleCleanup() throws Exception {
        Process process = Mockito.mock(Process.class);
        ProcessHandle processHandle = Mockito.mock(ProcessHandle.class);
        Mockito.when(process.isAlive()).thenReturn(true);
        Mockito.when(process.toHandle()).thenReturn(processHandle);
        Mockito.when(processHandle.descendants()).thenReturn(Stream.empty());
        ScriptlessExecutionAdapter adapter = new ScriptlessExecutionAdapter();
        setPrivateField(adapter, "currentProcess", process);
        setPrivateField(adapter, "currentWorkspace", "webdriver_generic");
        setPrivateField(adapter, "currentMode", "Generate");
        setPrivateField(adapter, "plannedSequenceCount", 1);
        setPrivateField(adapter, "sequenceOutcomes", List.of(new SequenceOutcomeDto(1, "OK")));

        PersistenceManager persistence = Mockito.mock(PersistenceManager.class);
        Runnable export = Mockito.mock(Runnable.class);
        ModelManager manager = new ModelManager(Mockito.mock(AbstractStateModel.class), Mockito.mock(ActionSelector.class),
                persistence, Mockito.mock(SequenceManager.class), false, export);
        Mockito.doAnswer(invocation -> {
            // Expire the idle timer inside shutdown, before the exporter can emit any signal.
            setPrivateField(adapter, "lastOutputEpochMillis", 0L);
            ExecutionStatusDto status = adapter.status();
            Assert.assertEquals("running", status.status());
            Assert.assertTrue(status.message().contains("Finalizing state model"));
            Assert.assertTrue(status.consoleOutput().contains("closing the datastore"));
            Mockito.verifyNoInteractions(export);
            Mockito.verify(processHandle, Mockito.never()).destroyForcibly();
            return null;
        }).when(persistence).shutdown();
        Mockito.doAnswer(invocation -> {
            System.out.println(StaticGraphExporter.EXPORT_STARTED_SIGNAL);
            setPrivateField(adapter, "lastOutputEpochMillis", 0L);
            Assert.assertTrue(adapter.status().message().contains("Exporting static state model"));
            Mockito.verify(processHandle, Mockito.never()).destroyForcibly();

            System.out.println(StaticGraphExporter.EXPORT_FINISHED_SIGNAL);
            setPrivateField(adapter, "lastOutputEpochMillis", 0L);
            Assert.assertTrue(adapter.status().message().contains("Finalizing state model"));
            Mockito.verify(processHandle, Mockito.never()).destroyForcibly();
            return null;
        }).when(export).run();

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream runtimeOutput = new PrintStream(output, true, StandardCharsets.UTF_8) {
            @Override
            public void println(String line) {
                adapter.acceptProcessOutputLine(line);
                super.println(line);
            }
        }) {
            System.setOut(runtimeOutput);
            try {
                manager.notifyTestingEnded();
                manager.notifyTestingEnded();
            } finally {
                System.setOut(originalOutput);
            }
        }

        Mockito.verify(persistence).shutdown();
        Mockito.verify(export).run();
        ExecutionStatusDto status = adapter.status();
        Assert.assertTrue(status.consoleOutput().contains("State model finalization finished."));
        Assert.assertFalse(status.consoleOutput().contains("TESTAR_STATE_MODEL_FINALIZATION"));
        Assert.assertFalse(status.consoleOutput().contains("TESTAR_STATIC_GRAPH_EXPORT"));
        Assert.assertFalse(status.message().contains("Finalizing"));
        Assert.assertFalse(status.message().contains("Exporting"));
        Mockito.verify(processHandle, Mockito.never()).destroyForcibly();

        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        adapter.status();
        Mockito.verify(processHandle).destroyForcibly();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
