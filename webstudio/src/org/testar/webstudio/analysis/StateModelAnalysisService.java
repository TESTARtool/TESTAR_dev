/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.analysis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.StateModelDebugLog;
import org.testar.statemodel.analysis.webserver.JettyServer;
import org.testar.statemodel.persistence.orientdb.entity.Config;
import org.testar.webstudio.api.dto.StateModelStatusDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class StateModelAnalysisService {

    private static final String ANALYSIS_URL = "http://localhost:8090/models";
    private static final String STATUS_STOPPED = "STOPPED";
    private static final String STATUS_STARTING = "STARTING";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_FAILED = "FAILED";

    private final WorkspaceService workspaceService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "webstudio-state-model-analysis");
        thread.setDaemon(true);
        return thread;
    });
    private JettyServer jettyServer;
    private AnalysisManager analysisManager;
    private String status = STATUS_STOPPED;
    private String message = "State model analysis is not running.";

    public StateModelAnalysisService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public synchronized StateModelStatusDto open(String workspaceName) {
        return start(workspaceName);
    }

    public synchronized StateModelStatusDto start(String workspaceName) {
        Path runtimeHome = workspaceService.workspaceRuntimeHomeDirectory(workspaceName);
        Path debugLogPath = runtimeHome.resolve("state-model-debug.log");
        StateModelDebugLog.install(debugLogPath);
        System.clearProperty("testar.analysis.keepOrientDbOpen");
        StateModelDebugLog.log("Opening state model analysis for workspace: " + workspaceName);
        StateModelDebugLog.log("State model analysis runtime home: " + runtimeHome);

        if (isRunning()) {
            StateModelDebugLog.log("Reusing in-process state model analysis server instance.");
            status = STATUS_RUNNING;
            message = "State model analysis is already running.";
            return status();
        }

        if (isAnalysisServerReachable()) {
            StateModelDebugLog.log("State model analysis server already reachable, reusing existing instance.");
            status = STATUS_RUNNING;
            message = "State model analysis is already running.";
            return status();
        }

        if (STATUS_STARTING.equals(status)) {
            return status();
        }

        status = STATUS_STARTING;
        message = "State model analysis is starting. OrientDB may be recovering the datastore.";
        executorService.submit(() -> startAnalysis(workspaceName, runtimeHome));
        return status();
    }

    public synchronized StateModelStatusDto status() {
        if (isRunning() || isAnalysisServerReachable()) {
            status = STATUS_RUNNING;
            message = "State model analysis is running.";
        }

        return new StateModelStatusDto(status, ANALYSIS_URL, message, STATUS_RUNNING.equals(status));
    }

    public synchronized StateModelStatusDto stop() {
        try {
            if (jettyServer == null && isAnalysisServerReachable()) {
                status = STATUS_FAILED;
                message = "State model analysis is running outside the current WebStudio process. Stop the owning process to close it.";
                return new StateModelStatusDto(status, ANALYSIS_URL, message, false);
            }

            if (jettyServer != null) {
                jettyServer.stop();
                jettyServer = null;
            }

            closeAnalysisManager();
            status = STATUS_STOPPED;
            message = "State model analysis stopped.";
            return status();
        } catch (Exception exception) {
            StateModelDebugLog.log("Unable to stop state model analysis cleanly.", exception);
            status = STATUS_FAILED;
            message = "Unable to stop state model analysis: " + exception.getMessage();
            return status();
        }
    }

    private void startAnalysis(String workspaceName, Path runtimeHome) {
        Path workspaceDirectory = workspaceService.workspaceDirectory(workspaceName);
        Path testSettingsFile = workspaceDirectory.resolve("test.settings");

        try {
            Settings.setSettingsPath(workspaceDirectory.toString());
            Settings settings = Settings.loadSettings(new String[0], testSettingsFile.toString());
            validateSettings(settings);

            Path graphsDirectory = resolveGraphsDirectory(runtimeHome);
            Files.createDirectories(graphsDirectory);

            Config config = new Config();
            config.setConnectionType(settings.get(StateModelTags.DataStoreType, ""));
            config.setServer(settings.get(StateModelTags.DataStoreServer, ""));
            config.setDatabase(settings.get(StateModelTags.DataStoreDB, ""));
            config.setUser(settings.get(StateModelTags.DataStoreUser, ""));
            config.setPassword(settings.get(StateModelTags.DataStorePassword, ""));
            config.setDatabaseDirectory(resolveAgainstTestarHome(
                runtimeHome,
                settings.get(StateModelTags.DataStoreDirectory, "")
            ).toString());

            analysisManager = new AnalysisManager(config, graphsDirectory.toString() + File.separator);
            jettyServer = new JettyServer();
            jettyServer.start(graphsDirectory.toString() + File.separator, analysisManager);

            setRunning("State model analysis server started.");
        } catch (Exception exception) {
            StateModelDebugLog.log("Unable to open state model analysis for workspace: " + workspaceName, exception);
            if (isAddressAlreadyInUse(exception) && isAnalysisServerReachable()) {
                setRunning("State model analysis is already running.");
                return;
            }

            if (isMissingStateModelDatabase(exception)) {
                setFailed("No generated state model was found yet. Please run TESTAR in Generate mode first, then open View State Model.");
                return;
            }

            setFailed("Unable to start state model analysis: " + exception.getMessage());
        }
    }

    private synchronized void setRunning(String message) {
        status = STATUS_RUNNING;
        this.message = message;
    }

    private synchronized void setFailed(String message) {
        closeAnalysisManager();
        status = STATUS_FAILED;
        this.message = message;
    }

    private boolean isRunning() {
        return jettyServer != null && jettyServer.isRunning();
    }

    private void closeAnalysisManager() {
        if (analysisManager == null) {
            return;
        }

        analysisManager.shutdown();
        analysisManager = null;
    }

    private void validateSettings(Settings settings) {
        List<String> missingSettings = new ArrayList<>();
        if (!settings.get(StateModelTags.StateModelInference, false)) {
            missingSettings.add("StateModelInference=true");
        }

        String dataStoreType = settings.get(StateModelTags.DataStoreType, "");
        if (dataStoreType.isBlank()) {
            missingSettings.add("DataStoreType");
        }

        if (settings.get(StateModelTags.DataStoreDB, "").isBlank()) {
            missingSettings.add("DataStoreDB");
        }

        if (settings.get(StateModelTags.DataStoreUser, "").isBlank()) {
            missingSettings.add("DataStoreUser");
        }

        if ("plocal".equalsIgnoreCase(dataStoreType) && settings.get(StateModelTags.DataStoreDirectory, "").isBlank()) {
            missingSettings.add("DataStoreDirectory");
        }

        if ("remote".equalsIgnoreCase(dataStoreType) && settings.get(StateModelTags.DataStoreServer, "").isBlank()) {
            missingSettings.add("DataStoreServer");
        }

        if (!missingSettings.isEmpty()) {
            throw new IllegalStateException("State model analysis requires: " + String.join(", ", missingSettings));
        }
    }

    static Path resolveGraphsDirectory(Path testarHome) {
        return testarHome.resolve("output").resolve("graphs").toAbsolutePath().normalize();
    }

    private Path resolveAgainstTestarHome(Path testarHome, String value) {
        if (value == null || value.isBlank()) {
            return testarHome.toAbsolutePath().normalize();
        }

        Path candidate = Path.of(value);
        if (candidate.isAbsolute()) {
            return candidate.normalize();
        }

        return testarHome.resolve(candidate).toAbsolutePath().normalize();
    }

    private boolean isAnalysisServerReachable() {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(ANALYSIS_URL).toURL().openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 500;
        } catch (IOException exception) {
            return false;
        }
    }

    private boolean isAddressAlreadyInUse(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("Address already in use")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private boolean isMissingStateModelDatabase(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null
                    && message.contains("Cannot open the storage")
                    && message.contains("because it does not exist")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
