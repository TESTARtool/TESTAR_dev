/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.PlatformSessionSpec.TargetType;

final class CliRunner {

    private final PrintStream output;

    CliRunner(PrintStream output) {
        this.output = Assert.notNull(output);
    }

    int run(CliRequest request) {
        switch (request.getCommand()) {
            case START_SESSION:
                return startSession(request);
            case GET_STATE:
                return getState(request);
            case GET_DERIVED_ACTIONS:
                return getDerivedActions(request);
            case EXECUTE_ACTION:
                return executeAction(request);
            case STOP_SYSTEM:
                return stopSystem(request);
            case HELP:
            default:
                printHelp();
                return 0;
        }
    }

    private int startSession(CliRequest request) {
        PlatformServices services = null;
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            services = PlatformOrchestrator.resolve(sessionSpec);
            SUT system = services.systemService().startSystem();
            output.println("sessionStarted");
            output.println("platform=" + sessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT));
            output.println("pid=" + system.get(Tags.PID, -1L));
            output.println("desc=" + system.get(Tags.Desc, ""));
            return 0;
        } catch (RuntimeException exception) {
            output.println("startSession failed: " + exception.getMessage());
            return 1;
        } finally {
            closeStateService(services);
        }
    }

    private int getState(CliRequest request) {
        PlatformServices services = null;
        SUT system = null;
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            services = PlatformOrchestrator.resolve(sessionSpec);
            system = services.systemService().startSystem();
            State state = services.stateService().getState(system);
            output.println("stateFetched");
            output.println("widgetCount=" + countWidgets(state));
            output.println("stateRole=" + String.valueOf(state.get(Tags.Role, null)));
            output.println("stateDesc=" + state.get(Tags.Desc, ""));
            return 0;
        } catch (RuntimeException exception) {
            output.println("getState failed: " + exception.getMessage());
            return 1;
        } finally {
            if (services != null && system != null) {
                services.systemService().stopSystem(system);
            }
            closeStateService(services);
        }
    }

    private int stopSystem(CliRequest request) {
        PlatformServices services = null;
        SUT system = null;
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            if (sessionSpec.getTargetType() != TargetType.PROCESS_ID) {
                output.println("stopSystem requires pid target");
                return 1;
            }
            services = PlatformOrchestrator.resolve(sessionSpec);
            system = services.systemService().startSystem();
            services.systemService().stopSystem(system);
            output.println("systemStopped");
            output.println("pid=" + sessionSpec.getTarget());
            return 0;
        } catch (RuntimeException exception) {
            output.println("stopSystem failed: " + exception.getMessage());
            return 1;
        } finally {
            closeStateService(services);
        }
    }

    private int getDerivedActions(CliRequest request) {
        PlatformServices services = null;
        SUT system = null;
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            services = PlatformOrchestrator.resolve(sessionSpec);
            system = services.systemService().startSystem();
            State state = services.stateService().getState(system);
            Set<Action> actions = services.actionDerivationService().deriveActions(system, state);

            output.println("derivedActionsFetched");
            output.println("count=" + actions.size());
            for (String actionLine : describeActions(actions)) {
                output.println(actionLine);
            }
            return 0;
        } catch (RuntimeException exception) {
            output.println("getDerivedActions failed: " + exception.getMessage());
            return 1;
        } finally {
            if (services != null && system != null) {
                services.systemService().stopSystem(system);
            }
            closeStateService(services);
        }
    }

    private int executeAction(CliRequest request) {
        PlatformServices services = null;
        SUT system = null;
        try {
            PlatformSessionSpec sessionSpec = buildSessionSpec(request);
            String actionIndexToken = request.argumentAt(3);
            if (actionIndexToken == null) {
                output.println("executeAction requires action index");
                return 1;
            }

            int actionIndex = Integer.parseInt(actionIndexToken);
            services = PlatformOrchestrator.resolve(sessionSpec);
            system = services.systemService().startSystem();
            State state = services.stateService().getState(system);
            List<Action> orderedActions = orderedActions(
                    services.actionDerivationService().deriveActions(system, state)
            );
            if (actionIndex < 0 || actionIndex >= orderedActions.size()) {
                output.println("executeAction failed: invalid action index " + actionIndex);
                output.println("availableActions=" + orderedActions.size());
                return 1;
            }

            Action action = orderedActions.get(actionIndex);
            boolean executed = services.actionExecutionService().executeAction(system, state, action);
            output.println("actionExecuted");
            output.println("index=" + actionIndex);
            output.println("success=" + executed);
            output.println("desc=" + action.get(Tags.Desc, action.toString()));
            return executed ? 0 : 1;
        } catch (RuntimeException exception) {
            output.println("executeAction failed: " + exception.getMessage());
            return 1;
        } finally {
            if (services != null && system != null) {
                services.systemService().stopSystem(system);
            }
            closeStateService(services);
        }
    }

    private void printHelp() {
        output.println("TESTAR CLI");
        output.println("Usage:");
        output.println("  startSession windows executable <path>");
        output.println("  startSession windows process <name>");
        output.println("  startSession windows pid <pid>");
        output.println("  startSession windows uwp <appUserModelId>");
        output.println("  getState windows executable <path>");
        output.println("  getState windows process <name>");
        output.println("  getState windows pid <pid>");
        output.println("  getDerivedActions windows executable <path>");
        output.println("  getDerivedActions windows process <name>");
        output.println("  getDerivedActions windows pid <pid>");
        output.println("  executeAction windows executable <path> <actionIndex>");
        output.println("  executeAction windows process <name> <actionIndex>");
        output.println("  executeAction windows pid <pid> <actionIndex>");
        output.println("  stopSystem windows pid <pid>");
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

    private void closeStateService(PlatformServices services) {
        if (services != null && services.stateService() instanceof AutoCloseable) {
            try {
                ((AutoCloseable) services.stateService()).close();
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to close state service", exception);
            }
        }
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
