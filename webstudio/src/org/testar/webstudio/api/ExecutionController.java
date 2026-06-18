/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.ResultFileDto;
import org.testar.webstudio.api.dto.ScriptlessResultsDto;
import org.testar.webstudio.execution.ExecutionAdapter;
import org.testar.webstudio.execution.ExecutionAdapterRegistry;
import org.testar.webstudio.execution.ExecutionBackend;
import org.testar.webstudio.execution.ScriptlessExecutionAdapter;

public final class ExecutionController {

    private final ExecutionAdapterRegistry executionAdapters;

    public ExecutionController(ExecutionAdapterRegistry executionAdapters) {
        this.executionAdapters = executionAdapters;
    }

    public List<String> availableBackends() {
        return executionAdapters.availableBackends()
            .stream()
            .map(ExecutionBackend::id)
            .collect(Collectors.toList());
    }

    public ExecutionStatusDto status(ExecutionBackend backend) {
        ExecutionAdapter adapter = executionAdapters.adapterFor(backend);
        return adapter.status();
    }

    public ExecutionStatusDto startGenerate(String workspaceName, Path settingsRoot) {
        return scriptlessExecutionAdapter().startGenerate(workspaceName, settingsRoot);
    }

    public ExecutionStatusDto startLocalSpy(String workspaceName, Path settingsRoot) {
        return scriptlessExecutionAdapter().startLocalSpy(workspaceName, settingsRoot);
    }

    public ExecutionStatusDto stopScriptlessRun() {
        return scriptlessExecutionAdapter().stop();
    }

    public ScriptlessResultsDto scriptlessResults() {
        return scriptlessExecutionAdapter().scriptlessResults();
    }

    public ResultFileDto scriptlessResultFile(String fileName, String filePath) {
        return scriptlessExecutionAdapter().readScriptlessResultFile(fileName, filePath);
    }

    public byte[] scriptlessResultAsset(String filePath) {
        try {
            return Files.readAllBytes(scriptlessExecutionAdapter().resolveScriptlessResultAsset(filePath));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read scriptless result asset: " + filePath, exception);
        }
    }

    public String scriptlessResultAssetContentType(String filePath) {
        String lowerCasePath = filePath == null ? "" : filePath.toLowerCase();
        if (lowerCasePath.endsWith(".png")) {
            return "image/png";
        }
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerCasePath.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerCasePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lowerCasePath.endsWith(".css")) {
            return "text/css";
        }
        if (lowerCasePath.endsWith(".js")) {
            return "application/javascript";
        }
        return "application/octet-stream";
    }

    private ScriptlessExecutionAdapter scriptlessExecutionAdapter() {
        return (ScriptlessExecutionAdapter) executionAdapters.adapterFor(ExecutionBackend.SCRIPTLESS);
    }
}
