/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.server;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.router.JavalinDefaultRoutingApi;
import org.testar.webstudio.api.ExecutionController;
import org.testar.webstudio.api.RemoteSpyController;
import org.testar.webstudio.api.StateModelAnalysisController;
import org.testar.webstudio.api.ValidationController;
import org.testar.webstudio.api.WorkspaceController;
import org.testar.webstudio.api.dto.CliAgentSettingsDto;
import org.testar.webstudio.api.dto.CliManualCommandRequestDto;
import org.testar.webstudio.api.dto.RegexValidationRequestDto;
import org.testar.webstudio.api.dto.SpyTypeRequestDto;
import org.testar.webstudio.api.dto.WorkspaceFileUpdateDto;
import org.testar.webstudio.analysis.StateModelAnalysisService;
import org.testar.webstudio.execution.CliExecutionAdapter;
import org.testar.webstudio.execution.ExecutionAdapterRegistry;
import org.testar.webstudio.execution.ExecutionBackend;
import org.testar.webstudio.execution.RemoteExecutionAdapter;
import org.testar.webstudio.execution.ScriptlessExecutionAdapter;
import org.testar.webstudio.spy.RemoteSpyService;
import org.testar.webstudio.validation.ValidationService;
import org.testar.webstudio.workspace.WorkspaceService;

public final class WebStudioServer {

    private static final int DEFAULT_PORT = 8888;

    private final WorkspaceController workspaceController;
    private final ValidationController validationController;
    private final ExecutionController executionController;
    private final RemoteSpyController remoteSpyController;
    private final StateModelAnalysisController stateModelAnalysisController;
    private final Gson gson;
    private Javalin app;

    public WebStudioServer() {
        WorkspaceService workspaceService = new WorkspaceService();
        System.setProperty("testar.home", workspaceService.testarHomeDirectory().toString());
        ValidationService validationService = new ValidationService(workspaceService);
        ExecutionAdapterRegistry executionAdapters = new ExecutionAdapterRegistry(List.of(
            new CliExecutionAdapter(workspaceService),
            new ScriptlessExecutionAdapter(),
            new RemoteExecutionAdapter()
        ));
        StateModelAnalysisService stateModelAnalysisService = new StateModelAnalysisService(workspaceService);
        RemoteSpyService remoteSpyService = new RemoteSpyService(workspaceService);

        this.workspaceController = new WorkspaceController(workspaceService);
        this.validationController = new ValidationController(validationService);
        this.executionController = new ExecutionController(executionAdapters);
        this.remoteSpyController = new RemoteSpyController(remoteSpyService);
        this.stateModelAnalysisController = new StateModelAnalysisController(stateModelAnalysisService);
        this.gson = new Gson();
    }

    public void start() {
        if (app != null) {
            return;
        }

        int port = configuredPort();
        app = Javalin.create(config -> {
            config.staticFiles.add("/web", Location.CLASSPATH);
            registerRoutes(config.routes);
        });
        app.start(port);

        System.out.println("TESTAR web studio started at http://localhost:" + port);
    }

    private int configuredPort() {
        String configuredPort = System.getProperty("testar.webstudio.port");
        if (configuredPort == null || configuredPort.isBlank()) {
            return DEFAULT_PORT;
        }

        return Integer.parseInt(configuredPort);
    }

    private void registerRoutes(JavalinDefaultRoutingApi routes) {
        routes.get("/", context -> context.redirect("/index.html"));
        routes.get("/api/health", context -> handle(context, () -> Map.of(
            "name", "TESTAR web studio",
            "status", "ok",
            "settingsRoot", workspaceController.settingsRoot().toString(),
            "workspaceCount", workspaceController.listWorkspaces().size()
        )));
        routes.get("/api/workspaces", context -> handle(context, workspaceController::listWorkspaces));
        routes.get("/api/debug-files", context -> handle(context, workspaceController::listDebugFiles));
        routes.get("/api/debug-files/{fileName}", context -> handle(context, () ->
            workspaceController.readDebugFile(
                context.pathParam("fileName"),
                context.queryParam("path")
            )
        ));
        routes.get("/api/workspaces/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return workspaceController.readWorkspaceDocument(workspace);
        }));
        routes.put("/api/workspaces/{workspace}/test-settings", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            WorkspaceFileUpdateDto update = gson.fromJson(context.body(), WorkspaceFileUpdateDto.class);
            return workspaceController.saveTestSettings(workspace, update.content());
        }));
        routes.put("/api/workspaces/{workspace}/composition-properties", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            WorkspaceFileUpdateDto update = gson.fromJson(context.body(), WorkspaceFileUpdateDto.class);
            return workspaceController.saveCompositionProperties(workspace, update.content());
        }));
        routes.put("/api/workspaces/{workspace}/policies-properties", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            WorkspaceFileUpdateDto update = gson.fromJson(context.body(), WorkspaceFileUpdateDto.class);
            return workspaceController.savePoliciesProperties(workspace, update.content());
        }));
        routes.get("/api/workspaces/{workspace}/sources/{sourceName}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            String sourceName = context.pathParam("sourceName");
            return workspaceController.readWorkspaceSourceFile(workspace, sourceName);
        }));
        routes.put("/api/workspaces/{workspace}/sources/{sourceName}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            String sourceName = context.pathParam("sourceName");
            WorkspaceFileUpdateDto update = gson.fromJson(context.body(), WorkspaceFileUpdateDto.class);
            return workspaceController.saveWorkspaceSourceFile(workspace, sourceName, update.content());
        }));
        routes.post("/api/workspaces/{workspace}/sources/{sourceName}/compile", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            String sourceName = context.pathParam("sourceName");
            return workspaceController.compileWorkspaceSource(workspace, sourceName);
        }));
        routes.post("/api/workspaces/{workspace}/compile-profile", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return workspaceController.compileWorkspaceProfile(workspace);
        }));
        routes.post("/api/workspaces/{workspace}/composition/modules/{propertyKey}/source", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            String propertyKey = context.pathParam("propertyKey");
            return workspaceController.createOrOpenModuleSource(workspace, propertyKey);
        }));
        routes.post("/api/workspaces/{workspace}/policies/{propertyKey}/source", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            String propertyKey = context.pathParam("propertyKey");
            return workspaceController.createOrOpenPolicySource(workspace, propertyKey);
        }));
        routes.post("/api/settings/regex/validate", context -> handle(context, () -> {
            RegexValidationRequestDto request = gson.fromJson(context.body(), RegexValidationRequestDto.class);
            return workspaceController.validateRegex(request == null ? "" : request.value());
        }));
        routes.get("/api/validation/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return validationController.validateWorkspace(workspace);
        }));
        routes.get("/api/execution/backends", context -> handle(context, executionController::availableBackends));
        routes.get("/api/execution/cli/profiles", context -> handle(context, executionController::cliProfiles));
        routes.get("/api/execution/status/{backend}", context -> handle(context, () -> {
            ExecutionBackend backend = ExecutionBackend.fromId(context.pathParam("backend"));
            return executionController.status(backend);
        }));
        routes.get("/api/execution/cli/agent-settings", context -> handle(context, executionController::cliAgentSettings));
        routes.put("/api/execution/cli/agent-settings", context -> handle(context, () -> {
            CliAgentSettingsDto update = gson.fromJson(context.body(), CliAgentSettingsDto.class);
            return executionController.saveCliAgentSettings(update);
        }));
        routes.post("/api/execution/cli/manual/start/{profile}", context -> handle(context, () -> {
            return executionController.startCliManualSession(context.pathParam("profile"), null);
        }));
        routes.post("/api/execution/cli/agent/start/{profile}", context -> handle(context, () -> {
            return executionController.startCliAgentSession(context.pathParam("profile"), null);
        }));
        routes.post("/api/execution/cli/manual/command", context -> handle(context, () -> {
            CliManualCommandRequestDto request = gson.fromJson(context.body(), CliManualCommandRequestDto.class);
            return executionController.runCliManualCommand(request == null ? "" : request.commandLine());
        }));
        routes.post("/api/execution/cli/manual/stop", context -> handle(context, executionController::stopCliManualSession));
        routes.post("/api/execution/cli/agent/stop", context -> handle(context, executionController::stopCliAgentSession));
        routes.get("/api/execution/cli/results", context -> handle(context, executionController::cliResults));
        routes.get("/api/execution/cli/results/{fileName}", context -> handle(context, () ->
            executionController.cliResultFile(
                context.pathParam("fileName"),
                context.queryParam("path")
            )
        ));
        routes.delete("/api/execution/cli/results/{fileName}", context -> handle(context, () ->
            executionController.deleteCliResultFile(context.queryParam("path"))
        ));
        routes.delete("/api/execution/cli/result-groups", context -> handle(context, () ->
            executionController.deleteCliResultGroup(context.queryParam("path"))
        ));
        routes.get("/api/execution/cli/result-asset", context -> {
            String assetPath = context.queryParam("path");
            context.contentType(executionController.cliResultAssetContentType(assetPath));
            context.result(executionController.cliResultAsset(assetPath));
        });
        routes.get("/api/execution/scriptless/results", context -> handle(context, executionController::scriptlessResults));
        routes.get("/api/execution/scriptless/results/{fileName}", context -> handle(context, () ->
            executionController.scriptlessResultFile(
                context.pathParam("fileName"),
                context.queryParam("path")
            )
        ));
        routes.delete("/api/execution/scriptless/results/{fileName}", context -> handle(context, () ->
            executionController.deleteScriptlessResultFile(context.queryParam("path"))
        ));
        routes.delete("/api/execution/scriptless/result-groups", context -> handle(context, () ->
            executionController.deleteScriptlessResultGroup(context.queryParam("path"))
        ));
        routes.get("/api/execution/scriptless/result-asset", context -> {
            String assetPath = context.queryParam("path");
            context.contentType(executionController.scriptlessResultAssetContentType(assetPath));
            context.result(executionController.scriptlessResultAsset(assetPath));
        });
        routes.post("/api/execution/scriptless/generate/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return executionController.startGenerate(workspace, workspaceController.settingsRoot());
        }));
        routes.post("/api/execution/scriptless/local-spy/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return executionController.startLocalSpy(workspace, workspaceController.settingsRoot());
        }));
        routes.post("/api/execution/scriptless/stop", context -> handle(context, executionController::stopScriptlessRun));
        routes.get("/api/spy/status", context -> handle(context, remoteSpyController::remoteSpyStatus));
        routes.post("/api/spy/start/{workspace}", context -> handle(context, () ->
            remoteSpyController.startRemoteSpy(context.pathParam("workspace"))
        ));
        routes.post("/api/spy/refresh", context -> handle(context, remoteSpyController::refreshRemoteSpy));
        routes.post("/api/spy/actions/{actionId}", context -> handle(context, () ->
            remoteSpyController.executeRemoteSpyAction(context.pathParam("actionId"))
        ));
        routes.post("/api/spy/widgets/{widgetId}/default-action", context -> handle(context, () ->
            remoteSpyController.executeRemoteSpyDefaultWidgetAction(context.pathParam("widgetId"))
        ));
        routes.post("/api/spy/widgets/{widgetId}/direct-type", context -> handle(context, () -> {
            SpyTypeRequestDto request = gson.fromJson(context.body(), SpyTypeRequestDto.class);
            return remoteSpyController.executeRemoteSpyDirectType(
                context.pathParam("widgetId"),
                request == null ? "" : request.text()
            );
        }));
        routes.post("/api/spy/stop", context -> handle(context, remoteSpyController::stopRemoteSpy));
        routes.get("/api/spy/screenshot", context -> {
            String screenshotPath = context.queryParam("path");
            context.contentType("image/png");
            context.result(remoteSpyController.screenshot(screenshotPath));
        });
        routes.post("/api/statemodel/open/{workspace}", context -> handle(context, () ->
            stateModelAnalysisController.open(context.pathParam("workspace"))
        ));
    }

    private void writeJson(Context context, Object payload) {
        context.contentType("application/json");
        context.result(gson.toJson(payload));
    }

    private void writeError(Context context, int status, Exception exception) {
        context.status(status);
        writeJson(context, Map.of(
            "error", exception.getClass().getSimpleName(),
            "message", exception.getMessage()
        ));
    }

    private void handle(Context context, JsonSupplier supplier) {
        try {
            writeJson(context, supplier.get());
        } catch (IllegalArgumentException exception) {
            writeError(context, 400, exception);
        } catch (IllegalStateException exception) {
            writeError(context, 500, exception);
        } catch (Exception exception) {
            writeError(context, 500, exception);
        }
    }

    @FunctionalInterface
    private interface JsonSupplier {
        Object get() throws Exception;
    }
}
