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

import org.testar.webstudio.api.dto.CliAgentSettingsDto;
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
        return cliExecutionAdapter().startManualSession(
            profileName,
            request == null ? "" : request.platform(),
            request == null ? "" : request.target()
        );
    }

    public ExecutionStatusDto startCliAgentSession(String profileName, CliManualSessionRequestDto request) {
        return cliExecutionAdapter().startAgentSession(
            profileName,
            request == null ? "" : request.platform(),
            request == null ? "" : request.target()
        );
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

    public ScriptlessResultsDto cliResults() {
        return cliExecutionAdapter().cliResults();
    }

    public ResultFileDto cliResultFile(String fileName, String filePath) {
        return cliExecutionAdapter().readCliResultFile(fileName, filePath);
    }

    public ScriptlessResultsDto deleteCliResultFile(String filePath) {
        return cliExecutionAdapter().deleteCliResultFile(filePath);
    }

    public ScriptlessResultsDto deleteCliResultGroup(String groupPath) {
        return cliExecutionAdapter().deleteCliResultGroup(groupPath);
    }

    public byte[] cliResultAsset(String filePath) {
        try {
            return Files.readAllBytes(cliExecutionAdapter().resolveCliResultAsset(filePath));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read CLI result asset: " + filePath, exception);
        }
    }

    public String cliResultAssetContentType(String filePath) {
        return cliExecutionAdapter().cliResultAssetContentType(filePath);
    }

    public CliAgentSettingsDto cliAgentSettings() {
        return cliExecutionAdapter().loadAgentSettings();
    }

    public CliAgentSettingsDto saveCliAgentSettings(CliAgentSettingsDto settings) {
        return cliExecutionAdapter().saveAgentSettings(settings);
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

    public ScriptlessResultsDto deleteScriptlessResultFile(String filePath) {
        return scriptlessExecutionAdapter().deleteScriptlessResultFile(filePath);
    }

    public ScriptlessResultsDto deleteScriptlessResultGroup(String groupPath) {
        return scriptlessExecutionAdapter().deleteScriptlessResultGroup(groupPath);
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

    private CliExecutionAdapter cliExecutionAdapter() {
        return (CliExecutionAdapter) executionAdapters.adapterFor(ExecutionBackend.CLI);
    }
}
