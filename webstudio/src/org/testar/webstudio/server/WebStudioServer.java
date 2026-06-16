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
import org.testar.webstudio.api.StateModelAnalysisController;
import org.testar.webstudio.api.ValidationController;
import org.testar.webstudio.api.WorkspaceController;
import org.testar.webstudio.api.dto.WorkspaceFileUpdateDto;
import org.testar.webstudio.analysis.StateModelAnalysisService;
import org.testar.webstudio.execution.CliExecutionAdapter;
import org.testar.webstudio.execution.ExecutionAdapter;
import org.testar.webstudio.execution.ExecutionAdapterRegistry;
import org.testar.webstudio.execution.ExecutionBackend;
import org.testar.webstudio.execution.RemoteExecutionAdapter;
import org.testar.webstudio.execution.ScriptlessExecutionAdapter;
import org.testar.webstudio.validation.ValidationService;
import org.testar.webstudio.workspace.WorkspaceService;

public final class WebStudioServer {

    private static final int DEFAULT_PORT = 8888;

    private final WorkspaceController workspaceController;
    private final ValidationController validationController;
    private final ExecutionController executionController;
    private final StateModelAnalysisController stateModelAnalysisController;
    private final Gson gson;
    private Javalin app;

    public WebStudioServer() {
        WorkspaceService workspaceService = new WorkspaceService();
        ValidationService validationService = new ValidationService(workspaceService);
        ExecutionAdapterRegistry executionAdapters = new ExecutionAdapterRegistry(defaultExecutionAdapters());
        StateModelAnalysisService stateModelAnalysisService = new StateModelAnalysisService(workspaceService);

        this.workspaceController = new WorkspaceController(workspaceService);
        this.validationController = new ValidationController(validationService);
        this.executionController = new ExecutionController(executionAdapters);
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

    private List<ExecutionAdapter> defaultExecutionAdapters() {
        return List.of(
            new CliExecutionAdapter(),
            new ScriptlessExecutionAdapter(),
            new RemoteExecutionAdapter()
        );
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
        routes.get("/api/validation/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return validationController.validateWorkspace(workspace);
        }));
        routes.get("/api/execution/backends", context -> handle(context, executionController::availableBackends));
        routes.get("/api/execution/status/{backend}", context -> handle(context, () -> {
            ExecutionBackend backend = ExecutionBackend.fromId(context.pathParam("backend"));
            return executionController.status(backend);
        }));
        routes.get("/api/execution/scriptless/results", context -> handle(context, executionController::scriptlessResults));
        routes.get("/api/execution/scriptless/results/{fileName}", context -> handle(context, () ->
            executionController.scriptlessResultFile(
                context.pathParam("fileName"),
                context.queryParam("path")
            )
        ));
        routes.post("/api/execution/scriptless/generate/{workspace}", context -> handle(context, () -> {
            String workspace = context.pathParam("workspace");
            return executionController.startGenerate(workspace, workspaceController.settingsRoot());
        }));
        routes.post("/api/execution/scriptless/stop", context -> handle(context, executionController::stopScriptlessRun));
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
