// Verifies WS-FUNC-WORKSPACE-SOURCE-EDITOR-001, WS-FUNC-COMPOSITION-FLOW-001, and WS-FUNC-POLICIES-001:
// workspace source, configuration property file, and compile request contracts.
import test from "node:test";
import assert from "node:assert/strict";
import {
    compileWorkspaceProfileRequest,
    compileWorkspaceProfileUrl,
    compileWorkspaceSourceRequest,
    createCompositionModuleSourceRequest,
    createPolicySourceRequest,
    loadWorkspaceDocumentRequest,
    loadWorkspaceSourceRequest,
    saveWorkspaceCompositionPropertiesRequest,
    saveWorkspacePoliciesPropertiesRequest,
    saveWorkspaceSourceRequest,
    workspaceCompositionModuleSourceUrl,
    workspaceCompositionPropertiesUrl,
    workspaceDocumentUrl,
    workspacePoliciesPropertiesUrl,
    workspacePolicySourceUrl,
    workspaceSourceCompileUrl,
    workspaceSourceUrl
} from "../../src/models/sourceEditorApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("builds workspace source URLs", () => {
    assert.equal(
        workspaceDocumentUrl("webdriver_generic"),
        "/api/workspaces/webdriver_generic"
    );
    assert.equal(
        workspaceCompositionPropertiesUrl("webdriver_generic"),
        "/api/workspaces/webdriver_generic/composition-properties"
    );
    assert.equal(
        workspacePoliciesPropertiesUrl("webdriver_generic"),
        "/api/workspaces/webdriver_generic/policies-properties"
    );
    assert.equal(
        workspaceSourceUrl("webdriver_generic", "My Source.java"),
        "/api/workspaces/webdriver_generic/sources/My%20Source.java"
    );
    assert.equal(
        workspaceSourceCompileUrl("webdriver_generic", "My Source.java"),
        "/api/workspaces/webdriver_generic/sources/My%20Source.java/compile"
    );
    assert.equal(
        compileWorkspaceProfileUrl("webdriver_generic"),
        "/api/workspaces/webdriver_generic/compile-profile"
    );
    assert.equal(
        workspaceCompositionModuleSourceUrl("webdriver_generic", "service key"),
        "/api/workspaces/webdriver_generic/composition/modules/service%20key/source"
    );
    assert.equal(
        workspacePolicySourceUrl("webdriver_generic", "policy/key"),
        "/api/workspaces/webdriver_generic/policies/policy%2Fkey/source"
    );
});

test("loads workspace documents and saves workspace property files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await loadWorkspaceDocumentRequest(loadJson, "webdriver_generic");
    await saveWorkspaceCompositionPropertiesRequest(loadJson, "webdriver_generic", "a=b");
    await saveWorkspacePoliciesPropertiesRequest(loadJson, "webdriver_generic", "p=q");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic",
            options: undefined
        },
        {
            path: "/api/workspaces/webdriver_generic/composition-properties",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "a=b" })
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/policies-properties",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "p=q" })
            }
        }
    ]);
});

test("loads and saves workspace sources", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await loadWorkspaceSourceRequest(loadJson, "webdriver_generic", "MyPolicy.java");
    await saveWorkspaceSourceRequest(loadJson, "webdriver_generic", "MyPolicy.java", "class MyPolicy {}");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/sources/MyPolicy.java",
            options: undefined
        },
        {
            path: "/api/workspaces/webdriver_generic/sources/MyPolicy.java",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "class MyPolicy {}" })
            }
        }
    ]);
});

test("compiles workspace sources and profiles", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await compileWorkspaceSourceRequest(loadJson, "webdriver_generic", "MyPolicy.java");
    await compileWorkspaceProfileRequest(loadJson, "webdriver_generic");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/sources/MyPolicy.java/compile",
            options: {
                method: "POST"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/compile-profile",
            options: {
                method: "POST"
            }
        }
    ]);
});

test("creates composition module and policy source files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await createCompositionModuleSourceRequest(loadJson, "webdriver_generic", "service key");
    await createPolicySourceRequest(loadJson, "webdriver_generic", "policy/key");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/composition/modules/service%20key/source",
            options: {
                method: "POST"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/policies/policy%2Fkey/source",
            options: {
                method: "POST"
            }
        }
    ]);
});
