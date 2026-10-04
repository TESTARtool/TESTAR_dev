/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.statemodel.ModelManager;
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.SequenceOutcomeDto;
import org.testar.statemodel.analysis.export.StaticGraphExporter;

// Verifies WS-FUNC-RUNTIME-EXECUTION-001: cleanup respects Spy completion and state-model finalization.
public class ScriptlessExecutionAdapterTest {

    @Test
    public void recognizesSpyCompletionSignal() {
        Assert.assertTrue(ScriptlessExecutionAdapter.isSpySessionCompletionSignal(
                " TESTAR_SPY_SESSION_COMPLETED "
        ));
    }

    @Test
    public void ignoresUnrelatedConsoleOutput() {
        Assert.assertFalse(ScriptlessExecutionAdapter.isSpySessionCompletionSignal(
                "User requested to stop monkey!"
        ));
    }

    @Test
    public void completedSpySessionTransitionsToIdleAndCleansUpProcess() throws Exception {
        Process process = Mockito.mock(Process.class);
        ProcessHandle processHandle = Mockito.mock(ProcessHandle.class);
        Mockito.when(process.isAlive()).thenReturn(true);
        Mockito.when(process.toHandle()).thenReturn(processHandle);
        Mockito.when(processHandle.descendants()).thenReturn(Stream.empty());
        Mockito.when(processHandle.destroyForcibly()).thenReturn(true);

        ScriptlessExecutionAdapter adapter = new ScriptlessExecutionAdapter();
        setPrivateField(adapter, "currentProcess", process);
        setPrivateField(adapter, "currentWorkspace", "webdriver_generic");
        setPrivateField(adapter, "currentMode", "Spy");

        adapter.acceptProcessOutputLine("TESTAR_SPY_SESSION_COMPLETED");
        ExecutionStatusDto status = adapter.status();

        Assert.assertEquals("idle", status.status());
        Assert.assertEquals("Local Spy session finished after the SUT stopped", status.message());
        Assert.assertTrue(status.consoleOutput().contains("Local Spy session completed after the SUT stopped"));
        Assert.assertFalse(status.consoleOutput().contains("TESTAR_SPY_SESSION_COMPLETED"));
        Mockito.verify(processHandle).destroyForcibly();
    }

    @Test
    public void completedGenerateRunWaitsForExportBeforeIdleCleanup() throws Exception {
        Process process = Mockito.mock(Process.class);
        ProcessHandle processHandle = Mockito.mock(ProcessHandle.class);
        Mockito.when(process.isAlive()).thenReturn(true);
        Mockito.when(process.toHandle()).thenReturn(processHandle);
        Mockito.when(processHandle.descendants()).thenReturn(Stream.empty());
        Mockito.when(processHandle.destroyForcibly()).thenReturn(true);
        ScriptlessExecutionAdapter adapter = new ScriptlessExecutionAdapter();
        setPrivateField(adapter, "currentProcess", process);
        setPrivateField(adapter, "currentWorkspace", "webdriver_generic");
        setPrivateField(adapter, "currentMode", "Generate");
        setPrivateField(adapter, "plannedSequenceCount", 1);
        setPrivateField(adapter, "sequenceOutcomes", List.of(new SequenceOutcomeDto(1, "OK")));

        adapter.acceptProcessOutputLine(StaticGraphExporter.EXPORT_STARTED_SIGNAL);
        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        ExecutionStatusDto status = adapter.status();

        Assert.assertEquals("running", status.status());
        Assert.assertTrue(status.message().contains("Exporting static state model"));
        Assert.assertFalse(status.consoleOutput().contains(StaticGraphExporter.EXPORT_STARTED_SIGNAL));
        Mockito.verify(processHandle, Mockito.never()).destroyForcibly();

        adapter.acceptProcessOutputLine(StaticGraphExporter.EXPORT_FINISHED_SIGNAL);
        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        adapter.status();

        Mockito.verify(processHandle).destroyForcibly();
    }

    @Test
    public void completedGenerateRunWaitsForFinalizationWithoutExport() throws Exception {
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

        adapter.acceptProcessOutputLine(" " + ModelManager.FINALIZATION_STARTED_SIGNAL + " ");
        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        ExecutionStatusDto status = adapter.status();

        Assert.assertEquals("running", status.status());
        Assert.assertTrue(status.message().contains("Finalizing state model"));
        Assert.assertFalse(status.consoleOutput().contains(ModelManager.FINALIZATION_STARTED_SIGNAL));
        Mockito.verify(processHandle, Mockito.never()).destroyForcibly();

        adapter.acceptProcessOutputLine(ModelManager.FINALIZATION_FINISHED_SIGNAL);
        adapter.status();
        Mockito.verify(processHandle, Mockito.never()).destroyForcibly();
        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        adapter.status();
        Mockito.verify(processHandle).destroyForcibly();
    }

    @Test
    public void manualStopRemainsAvailableDuringStateModelFinalization() throws Exception {
        Process process = Mockito.mock(Process.class);
        ProcessHandle processHandle = Mockito.mock(ProcessHandle.class);
        Mockito.when(process.isAlive()).thenReturn(true);
        Mockito.when(process.toHandle()).thenReturn(processHandle);
        Mockito.when(processHandle.descendants()).thenReturn(Stream.empty());
        ScriptlessExecutionAdapter adapter = new ScriptlessExecutionAdapter();
        setPrivateField(adapter, "currentProcess", process);

        adapter.acceptProcessOutputLine(ModelManager.FINALIZATION_STARTED_SIGNAL);
        adapter.acceptProcessOutputLine(StaticGraphExporter.EXPORT_STARTED_SIGNAL);
        ExecutionStatusDto status = adapter.stop();

        Assert.assertEquals("idle", status.status());
        Assert.assertEquals("Scriptless run stopped", status.message());
        Mockito.verify(processHandle).destroyForcibly();

        setPrivateField(adapter, "currentProcess", process);
        setPrivateField(adapter, "currentMode", "Generate");
        setPrivateField(adapter, "plannedSequenceCount", 1);
        setPrivateField(adapter, "sequenceOutcomes", List.of(new SequenceOutcomeDto(1, "OK")));
        setPrivateField(adapter, "lastOutputEpochMillis", 0L);
        Mockito.when(processHandle.descendants()).thenReturn(Stream.empty());
        adapter.status();
        Mockito.verify(processHandle, Mockito.times(2)).destroyForcibly();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
