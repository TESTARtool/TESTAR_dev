export const STATE_MODEL_DEFAULT_URL = "http://localhost:8090/models";

// Maps backend state model status/errors to dialog content and follow-up actions.
const STATE_MODEL_NOT_AVAILABLE_MESSAGE = "Dear user, before opening the analysis mode, TESTAR must execute a Generate run with the state model enabled. Currently there are no generated state models available.";

export function stateModelWorkspaceDialog(selectedWorkspaceName) {
    if (!selectedWorkspaceName) {
        return {
            title: "Unable To Open State Model",
            message: "Dear user, select a TESTAR workspace before opening the state model analysis."
        };
    }

    return null;
}

export function stateModelDialogMessage(openError) {
    const errorMessage = openError?.message || "";

    if (errorMessage.includes("No generated state model was found yet")
        || errorMessage.includes("Cannot open the storage")
        || errorMessage.includes("because it does not exist")) {
        return {
            title: "State Model Not Available",
            message: STATE_MODEL_NOT_AVAILABLE_MESSAGE
        };
    }

    return {
        title: "Unable To Open State Model",
        message: STATE_MODEL_NOT_AVAILABLE_MESSAGE
    };
}

export function stateModelDialogTitle(status) {
    if (status === "RUNNING") {
        return "State Model Analysis Running";
    }
    if (status === "STARTING") {
        return "State Model Analysis Starting";
    }
    if (status === "FAILED") {
        return "Unable To Open State Model";
    }
    return "State Model Analysis";
}

export function stateModelDialogFromStatus(statusResponse) {
    return {
        title: stateModelDialogTitle(statusResponse?.status),
        message: statusResponse?.message || "State model analysis status is unknown.",
        status: statusResponse || {}
    };
}

export function stateModelExternalUrl(statusResponse) {
    return statusResponse?.url || STATE_MODEL_DEFAULT_URL;
}

export function stateModelRequestedMessage(statusResponse) {
    return statusResponse?.message || "State model analysis requested.";
}

export function stateModelStoppedMessage(statusResponse) {
    return statusResponse?.message || "State model analysis stopped.";
}

export function stateModelShouldOpenExternalTab(statusResponse) {
    return statusResponse?.status === "RUNNING";
}

export function stateModelShouldPollStatus(statusResponse) {
    return statusResponse?.status === "STARTING";
}
