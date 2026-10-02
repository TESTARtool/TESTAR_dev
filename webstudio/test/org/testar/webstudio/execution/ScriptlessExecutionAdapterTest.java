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
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.SequenceOutcomeDto;
import org.testar.statemodel.analysis.export.StaticGraphExporter;

// Verifies WS-FUNC-RUNTIME-EXECUTION-001: cleanup respects Spy completion and Generate export lifecycle signals.
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

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
