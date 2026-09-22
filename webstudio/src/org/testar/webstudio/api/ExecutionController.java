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

import org.testar.webstudio.api.dto.CliManualSessionRequestDto;
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.ResultFileDto;
import org.testar.webstudio.api.dto.ScriptlessResultsDto;
import org.testar.webstudio.execution.CliExecutionAdapter;
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

    public List<String> cliProfiles() {
        return cliExecutionAdapter().profiles();
    }

    public ExecutionStatusDto startGenerate(String workspaceName, Path settingsRoot) {
        return scriptlessExecutionAdapter().startGenerate(workspaceName, settingsRoot);
    }

    public ExecutionStatusDto startLocalSpy(String workspaceName, Path settingsRoot) {
        return scriptlessExecutionAdapter().startLocalSpy(workspaceName, settingsRoot);
    }

    public ExecutionStatusDto startCliManualSession(String profileName, CliManualSessionRequestDto request) {
        return cliExecutionAdapter().startManualSession(profileName);
    }

    public ExecutionStatusDto startCliAgentSession(String profileName, CliManualSessionRequestDto request) {
        return cliExecutionAdapter().startAgentSession(profileName);
    }

    public ExecutionStatusDto runCliManualCommand(String commandLine) {
        return cliExecutionAdapter().runManualCommand(commandLine);
    }

    public ExecutionStatusDto stopCliManualSession() {
        return cliExecutionAdapter().stopManualSession();
    }

    public ExecutionStatusDto stopCliAgentSession() {
        return cliExecutionAdapter().stopAgentSession();
    }

    public ScriptlessResultsDto cliResults(String workspaceName) {
        return cliExecutionAdapter().cliResults(workspaceName);
    }

    public ResultFileDto cliResultFile(String workspaceName, String fileName, String filePath) {
        return cliExecutionAdapter().readCliResultFile(workspaceName, fileName, filePath);
    }

    public ScriptlessResultsDto deleteCliResultFile(String workspaceName, String filePath) {
        return cliExecutionAdapter().deleteCliResultFile(workspaceName, filePath);
    }

    public ScriptlessResultsDto deleteCliResultGroup(String workspaceName, String groupPath) {
        return cliExecutionAdapter().deleteCliResultGroup(workspaceName, groupPath);
    }

    public byte[] cliResultAsset(String workspaceName, String filePath) {
        try {
            return Files.readAllBytes(cliExecutionAdapter().resolveCliResultAsset(workspaceName, filePath));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read CLI result asset: " + filePath, exception);
        }
    }

    public String cliResultAssetContentType(String filePath) {
        return cliExecutionAdapter().cliResultAssetContentType(filePath);
    }

    public ExecutionStatusDto stopScriptlessRun() {
        return scriptlessExecutionAdapter().stop();
    }

    public ScriptlessResultsDto scriptlessResults(String workspaceName) {
        return scriptlessExecutionAdapter().scriptlessResults(workspaceName);
    }

    public ResultFileDto scriptlessResultFile(String workspaceName, String fileName, String filePath) {
        return scriptlessExecutionAdapter().readScriptlessResultFile(workspaceName, fileName, filePath);
    }

    public ScriptlessResultsDto deleteScriptlessResultFile(String workspaceName, String filePath) {
        return scriptlessExecutionAdapter().deleteScriptlessResultFile(workspaceName, filePath);
    }

    public ScriptlessResultsDto deleteScriptlessResultGroup(String workspaceName, String groupPath) {
        return scriptlessExecutionAdapter().deleteScriptlessResultGroup(workspaceName, groupPath);
    }

    public byte[] scriptlessResultAsset(String workspaceName, String filePath) {
        try {
            return Files.readAllBytes(scriptlessExecutionAdapter().resolveScriptlessResultAsset(workspaceName, filePath));
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

    private CliExecutionAdapter cliExecutionAdapter() {
        return (CliExecutionAdapter) executionAdapters.adapterFor(ExecutionBackend.CLI);
    }
}
