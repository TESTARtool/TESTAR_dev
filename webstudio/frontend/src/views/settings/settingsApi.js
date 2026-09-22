export function testSettingsUrl(workspaceName) {
    return `/api/workspaces/${workspaceName}/test-settings`;
}

export function regexValidationUrl() {
    return "/api/settings/regex/validate";
}

export async function saveTestSettingsRequest(loadJson, workspaceName, content) {
    return loadJson(testSettingsUrl(workspaceName), {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}

export async function validateRegexRequest(loadJson, value) {
    return loadJson(regexValidationUrl(), {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            value: value || ""
        })
    });
}
