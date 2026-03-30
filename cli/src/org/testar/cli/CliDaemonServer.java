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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformSession;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.PlatformSessionSpec.TargetType;

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
            case STOP_SYSTEM:
                return stopSystem();
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

    private CliResponse getState() {
        try {
            PlatformSession session = requireActiveSession();
            State state = session.getState();
            return new CliResponse(0, List.of(
                    "stateFetched",
                    "widgetCount=" + countWidgets(state),
                    "stateRole=" + String.valueOf(state.get(Tags.Role, null)),
                    "stateDesc=" + state.get(Tags.Desc, "")
            ));
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("getState failed: " + exception.getMessage()));
        }
    }

    private CliResponse sessionStatus() {
        if (activeSession == null) {
            return new CliResponse(0, List.of(
                    "sessionStatus",
                    "active=false"
            ));
        }
        return new CliResponse(0, List.of(
                "sessionStatus",
                "active=true",
                "platform=" + activeSessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT),
                "pid=" + activeSession.system().get(Tags.PID, -1L),
                "desc=" + activeSession.system().get(Tags.Desc, "")
        ));
    }

    private CliResponse getDerivedActions() {
        try {
            Set<Action> actions = requireActiveSession().getDerivedActions();
            List<String> lines = new ArrayList<>();
            lines.add("derivedActionsFetched");
            lines.add("count=" + actions.size());
            lines.addAll(describeActions(actions));
            return new CliResponse(0, lines);
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("getDerivedActions failed: " + exception.getMessage()));
        }
    }

    private CliResponse executeAction(CliRequest request) {
        try {
            String actionIndexToken = request.argumentAt(0);
            if (actionIndexToken == null) {
                return new CliResponse(1, List.of("executeAction requires action index"));
            }
            int actionIndex = Integer.parseInt(actionIndexToken);
            PlatformSession session = requireActiveSession();
            List<Action> orderedActions = orderedActions(session.getDerivedActions());
            if (actionIndex < 0 || actionIndex >= orderedActions.size()) {
                return new CliResponse(1, List.of(
                        "executeAction failed: invalid action index " + actionIndex,
                        "availableActions=" + orderedActions.size()
                ));
            }
            Action action = orderedActions.get(actionIndex);
            boolean executed = session.executeAction(action);
            return new CliResponse(executed ? 0 : 1, List.of(
                    "actionExecuted",
                    "index=" + actionIndex,
                    "success=" + executed,
                    "desc=" + action.get(Tags.Desc, action.toString())
            ));
        } catch (RuntimeException exception) {
            return new CliResponse(1, List.of("executeAction failed: " + exception.getMessage()));
        }
    }

    private CliResponse stopSystem() {
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
            return new CliResponse(1, List.of("stopSystem failed: " + exception.getMessage()));
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
        String targetTypeToken = request.argumentAt(1);
        String target = request.argumentAt(2);

        if (platformToken == null || targetTypeToken == null || target == null) {
            throw new IllegalArgumentException("Expected: <platform> <targetType> <target>");
        }

        OperatingSystems operatingSystem = parseOperatingSystem(platformToken);
        TargetType targetType = parseTargetType(targetTypeToken);
        return PlatformSessionSpec.builder(operatingSystem, targetType, target).build();
    }

    private OperatingSystems parseOperatingSystem(String token) {
        String normalized = token.toLowerCase(Locale.ROOT);
        if ("windows".equals(normalized) || "windows10".equals(normalized)
                || "windows_10".equals(normalized)) {
            return OperatingSystems.WINDOWS;
        }
        throw new IllegalArgumentException("Unsupported platform token: " + token);
    }

    private TargetType parseTargetType(String token) {
        String normalized = token.toLowerCase(Locale.ROOT);
        if ("executable".equals(normalized)) {
            return TargetType.EXECUTABLE;
        }
        if ("process".equals(normalized) || "processname".equals(normalized)) {
            return TargetType.PROCESS_NAME;
        }
        if ("pid".equals(normalized)) {
            return TargetType.PROCESS_ID;
        }
        if ("uwp".equals(normalized)) {
            return TargetType.UWP;
        }
        throw new IllegalArgumentException("Unsupported target type: " + token);
    }

    private int countWidgets(State state) {
        int count = 0;
        for (Widget ignored : state) {
            count++;
        }
        return count;
    }

    private List<String> describeActions(Set<Action> actions) {
        List<String> descriptions = new ArrayList<>();
        List<Action> orderedActions = orderedActions(actions);
        for (int index = 0; index < orderedActions.size(); index++) {
            Action action = orderedActions.get(index);
            descriptions.add(String.format(
                    Locale.ROOT,
                    "action[%d]=role:%s;desc:%s",
                    index,
                    String.valueOf(action.get(Tags.Role, null)),
                    action.get(Tags.Desc, action.toString())
            ));
        }
        return descriptions;
    }

    private List<Action> orderedActions(Set<Action> actions) {
        List<Action> orderedActions = new ArrayList<>(actions);
        orderedActions.sort(Comparator.comparing(action -> action.get(Tags.Desc, action.toString())));
        return orderedActions;
    }
}
