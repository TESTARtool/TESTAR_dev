/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.testar.cli.settings.CliSettingsLoader;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformSession;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.PlatformSessionSpecFactory;

final class CliDaemonServer {

    private PlatformSession activeSession;
    private PlatformSessionSpec activeSessionSpec;

    void run() {
        try (ServerSocket serverSocket = new ServerSocket(CliDaemonConfig.PORT, 50)) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeActiveSession));
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    handle(socket);
                } catch (EOFException exception) {
                    // Reachability probes may connect and close immediately.
                } catch (IOException exception) {
                    // Keep the daemon alive for subsequent requests.
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start CLI daemon server", exception);
        }
    }

    private void handle(Socket socket) throws IOException {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            CliCommand command = CliCommand.valueOf(input.readUTF());
            int argumentCount = input.readInt();
            List<String> arguments = new ArrayList<>(argumentCount);
            for (int index = 0; index < argumentCount; index++) {
                arguments.add(input.readUTF());
            }
            CliResponse response = handle(CliRequest.of(command, arguments));
            output.writeInt(response.getExitCode());
            output.writeInt(response.getLines().size());
            for (String line : response.getLines()) {
                output.writeUTF(line);
            }
            output.flush();
        }
    }

    private CliResponse handle(CliRequest request) {
        switch (request.getCommand()) {
            case START_SESSION:
                return startSession(request);
            case SESSION_STATUS:
                return sessionStatus();
            case GET_STATE:
                return getState();
            case GET_DERIVED_ACTIONS:
                return getDerivedActions();
            case EXECUTE_ACTION:
                return executeAction(request);
            case STOP_SESSION:
                return stopSession();
            case HELP:
            case DAEMON:
            default:
                return new CliResponse(0, List.of("daemonReady"));
        }
    }

    private CliResponse startSession(CliRequest request) {
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            replaceActiveSession(sessionSpec);
            return new CliResponse(0, List.of(
                    "sessionStarted",
                    "platform=" + sessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT),
                    "pid=" + activeSession.system().get(Tags.PID, -1L),
                    "desc=" + activeSession.system().get(Tags.Desc, "")
            ));
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("startSession failed: " + exception.getMessage()));
        }
    }

    private CliResponse sessionStatus() {
        long daemonPid = ProcessHandle.current().pid();
        if (activeSession == null) {
            return new CliResponse(0, List.of(
                    "sessionStatus",
                    "daemonRunning=true",
                    "daemonPid=" + daemonPid,
                    "active=false"
            ));
        }
        return new CliResponse(0, List.of(
                "sessionStatus",
                "daemonRunning=true",
                "daemonPid=" + daemonPid,
                "active=true",
                "platform=" + activeSessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT),
                "pid=" + activeSession.system().get(Tags.PID, -1L),
                "desc=" + activeSession.system().get(Tags.Desc, "")
        ));
    }

    private CliResponse getState() {
        try {
            State state = requireActiveSession().getState();
            List<String> lines = new ArrayList<>();
            lines.addAll(describeStateWidgets(state));
            return new CliResponse(0, lines);
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("getState failed: " + exception.getMessage()));
        }
    }

    private CliResponse getDerivedActions() {
        try {
            Set<Action> actions = requireActiveSession().getDerivedActions();
            List<String> lines = new ArrayList<>();
            lines.addAll(describeActions(actions));
            return new CliResponse(0, lines);
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("getDerivedActions failed: " + exception.getMessage()));
        }
    }

    private CliResponse executeAction(CliRequest request) {
        try {
            PlatformSession session = requireActiveSession();
            ResolvedAction resolvedAction = session.resolveAction(request.getArguments());
            boolean executed = session.executeAction(resolvedAction.action());
            return new CliResponse(executed ? 0 : 1, List.of(
                    "actionExecuted",
                    "success=" + executed,
                    "desc=" + resolvedAction.action().get(Tags.Desc, resolvedAction.action().toString())
            ));
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("executeAction failed: " + exception.getMessage()));
        }
    }

    private CliResponse stopSession() {
        try {
            PlatformSession session = requireActiveSession();
            long pid = session.system().get(Tags.PID, -1L);
            session.stopSystem();
            closeActiveSession();
            return new CliResponse(0, List.of(
                    "systemStopped",
                    "pid=" + pid
            ));
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("stopSession failed: " + exception.getMessage()));
        }
    }

    private synchronized void replaceActiveSession(PlatformSessionSpec sessionSpec) {
        closeActiveSession();
        activeSessionSpec = sessionSpec;
        activeSession = PlatformOrchestrator.openSession(sessionSpec);
    }

    private synchronized PlatformSession requireActiveSession() {
        if (activeSession == null) {
            throw new IllegalStateException("No active session. Start a SUT first.");
        }
        return activeSession;
    }

    private synchronized void closeActiveSession() {
        if (activeSession != null) {
            try {
                activeSession.stopSystem();
            } catch (RuntimeException exception) {
                // Ignore stop failures during session replacement/shutdown.
            }
            try {
                activeSession.close();
            } finally {
                activeSession = null;
                activeSessionSpec = null;
            }
        }
    }

    private PlatformSessionSpec buildSessionSpec(CliRequest request) {
        String platformToken = request.argumentAt(0);
        String target = request.argumentAt(1);
        Settings settings = CliSettingsLoader.load();

        if (platformToken == null) {
            throw new IllegalArgumentException("Expected: startSession <platform> <target>");
        }

        if (target == null || request.argumentAt(2) != null) {
            throw new IllegalArgumentException("Expected: startSession <platform> <target>");
        }

        OperatingSystems operatingSystem = parseOperatingSystem(platformToken);
        return PlatformSessionSpecFactory.create(
                operatingSystem,
                PlatformSessionSpec.TargetType.EXECUTABLE,
                target,
                settings
        );
    }

    private OperatingSystems parseOperatingSystem(String token) {
        String normalized = token.toLowerCase(Locale.ROOT);
        if ("windows".equals(normalized) || "windows10".equals(normalized)
                || "windows_10".equals(normalized)) {
            return OperatingSystems.WINDOWS;
        }
        if ("webdriver".equals(normalized) || "web".equals(normalized)) {
            return OperatingSystems.WEBDRIVER;
        }
        throw new IllegalArgumentException("Unsupported platform token: " + token);
    }

    private List<String> describeStateWidgets(State state) {
        List<String> descriptions = new ArrayList<>();
        for (Widget widget : state) {
            descriptions.add(String.format(
                    Locale.ROOT,
                    "widget[%s]=role:%s;desc:%s",
                    String.valueOf(widget.get(Tags.AbstractID, "InvalidAbstractID")),
                    String.valueOf(widget.get(Tags.Role, Roles.Widget)),
                    widget.get(Tags.Desc, widget.toString())
            ));
        }
        return descriptions;
    }

    private List<String> describeActions(Set<Action> actions) {
        List<String> descriptions = new ArrayList<>();
        for (Action action : actions) {
            descriptions.add(String.format(
                    Locale.ROOT,
                    "action[%s]=role:%s;desc:%s",
                    String.valueOf(action.get(Tags.AbstractID, "InvalidAbstractID")),
                    String.valueOf(action.get(Tags.Role, ActionRoles.Action)),
                    action.get(Tags.Desc, action.toString())
            ));
        }
        return descriptions;
    }

}
