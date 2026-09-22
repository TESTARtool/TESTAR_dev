// Implements WS-FUNC-RUNTIME-EXECUTION-001:
// maps runtime actions, pages, and user-facing feedback messages.
export const RUNTIME_ACTIONS = {
    GENERATE: "generate",
    REMOTE_SPY: "remote-spy",
    LOCAL_SPY: "local-spy",
    CLI_MANUAL: "cli-manual",
    CLI_AGENT: "cli-agent"
};

export const RUNTIME_PAGES = {
    GENERATE: "run",
    SPY: "spy",
    CLI: "cli"
};

export const RUNTIME_FEEDBACK_MESSAGES = {
    GENERATE_STARTED: "Generate mode started.",
    GENERATE_STOPPED: "Generate mode stopped.",
    REMOTE_SPY_STARTED: "Remote Spy Mode started.",
    REMOTE_SPY_STOPPED: "Remote Spy Mode stopped.",
    LOCAL_SPY_STARTED: "Local Spy Mode started.",
    LOCAL_SPY_STOPPED: "Local Spy Mode stopped.",
    CLI_MANUAL_STARTED: "Manual CLI session started.",
    CLI_MANUAL_STOPPED: "Manual CLI session stopped.",
    CLI_AGENT_STARTED: "Agent CLI execution started.",
    CLI_AGENT_STOPPED: "Agent CLI execution stopped."
};

export const RUNTIME_FEEDBACK_ACTIONS = {
    GENERATE_STARTED: "generate-started",
    GENERATE_STOPPED: "generate-stopped",
    REMOTE_SPY_STARTED: "remote-spy-started",
    REMOTE_SPY_STOPPED: "remote-spy-stopped",
    LOCAL_SPY_STARTED: "local-spy-started",
    LOCAL_SPY_STOPPED: "local-spy-stopped",
    CLI_MANUAL_STARTED: "cli-manual-started",
    CLI_MANUAL_STOPPED: "cli-manual-stopped",
    CLI_AGENT_STARTED: "cli-agent-started",
    CLI_AGENT_STOPPED: "cli-agent-stopped"
};

export function runtimePageForAction(action) {
    if (action === RUNTIME_ACTIONS.GENERATE) {
        return RUNTIME_PAGES.GENERATE;
    }

    if (action === RUNTIME_ACTIONS.REMOTE_SPY || action === RUNTIME_ACTIONS.LOCAL_SPY) {
        return RUNTIME_PAGES.SPY;
    }

    if (action === RUNTIME_ACTIONS.CLI_MANUAL || action === RUNTIME_ACTIONS.CLI_AGENT) {
        return RUNTIME_PAGES.CLI;
    }

    return "";
}

export function runtimeStatusReturnedError(status) {
    return status?.status === "error";
}

export function formatSequenceOutcomeLabel(sequenceOutcome) {
    if (!sequenceOutcome) {
        return "";
    }

    if (sequenceOutcome.label) {
        return sequenceOutcome.label;
    }

    if (sequenceOutcome.outputPath) {
        const outputPathSegments = sequenceOutcome.outputPath.split(/[\\/]/);
        const fileName = outputPathSegments[outputPathSegments.length - 1] || "";
        const trimmedExtension = fileName.replace(/\.html?$/i, "");
        const sequenceIndex = trimmedExtension.indexOf("_sequence_");
        if (sequenceIndex >= 0) {
            return trimmedExtension.substring(sequenceIndex + 1);
        }

        if (trimmedExtension) {
            return trimmedExtension;
        }
    }

    return `sequence_${sequenceOutcome.sequenceNumber}`;
}

export function sequenceVerdicts(sequenceOutcome) {
    if (sequenceOutcome?.verdicts?.length > 0) {
        const aggregateLabel = `sequence_${sequenceOutcome.sequenceNumber}`;
        const detailedVerdicts = sequenceOutcome.verdicts.filter((verdict) => {
            return verdict.label !== aggregateLabel;
        });

        return detailedVerdicts.length > 0 ? detailedVerdicts : sequenceOutcome.verdicts;
    }

    return [{
        label: formatSequenceOutcomeLabel(sequenceOutcome),
        status: sequenceOutcome?.status || "ok",
        outputPath: sequenceOutcome?.outputPath || null
    }];
}

export function runtimeFeedbackMessage(status, fallbackMessage) {
    return status?.message || fallbackMessage;
}

export function runtimeActionFeedbackMessage(action, status) {
    return runtimeFeedbackMessage(status, runtimeFallbackMessageForAction(action));
}

export function runtimeFallbackMessageForAction(action) {
    if (action === RUNTIME_FEEDBACK_ACTIONS.GENERATE_STARTED) {
        return RUNTIME_FEEDBACK_MESSAGES.GENERATE_STARTED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.GENERATE_STOPPED) {
        return RUNTIME_FEEDBACK_MESSAGES.GENERATE_STOPPED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.REMOTE_SPY_STARTED) {
        return RUNTIME_FEEDBACK_MESSAGES.REMOTE_SPY_STARTED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.REMOTE_SPY_STOPPED) {
        return RUNTIME_FEEDBACK_MESSAGES.REMOTE_SPY_STOPPED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.LOCAL_SPY_STARTED) {
        return RUNTIME_FEEDBACK_MESSAGES.LOCAL_SPY_STARTED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.LOCAL_SPY_STOPPED) {
        return RUNTIME_FEEDBACK_MESSAGES.LOCAL_SPY_STOPPED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STARTED) {
        return RUNTIME_FEEDBACK_MESSAGES.CLI_MANUAL_STARTED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STOPPED) {
        return RUNTIME_FEEDBACK_MESSAGES.CLI_MANUAL_STOPPED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.CLI_AGENT_STARTED) {
        return RUNTIME_FEEDBACK_MESSAGES.CLI_AGENT_STARTED;
    }

    if (action === RUNTIME_FEEDBACK_ACTIONS.CLI_AGENT_STOPPED) {
        return RUNTIME_FEEDBACK_MESSAGES.CLI_AGENT_STOPPED;
    }

    return "";
}
