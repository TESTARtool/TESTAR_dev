// Verifies WS-FUNC-TEST-ORACLES-001: Test Oracles API request contracts.
// Test Oracles API request construction, including DSL metadata and generation endpoints.
import test from "node:test";
import assert from "node:assert/strict";
import {
    compileOracleJavaFileRequest,
    createOracleDslFileRequest,
    createOracleJavaFileRequest,
    deleteOracleDslFileRequest,
    deleteOracleJavaFileRequest,
    dslOracleMetadataUrl,
    generateJavaFromOracleDslFileRequest,
    loadDslOracleMetadataRequest,
    loadOracleDslFileRequest,
    loadOracleJavaFileRequest,
    loadTestOracleInventoryRequest,
    saveOracleJavaFileRequest,
    testOracleApiPath
} from "../../../src/views/oracles/testOraclesApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("builds Test Oracle API paths", () => {
    assert.equal(testOracleApiPath("webdriver generic"), "/api/workspaces/webdriver%20generic/test-oracles");
    assert.equal(dslOracleMetadataUrl(), "/api/test-oracles/dsl/metadata");
});

test("loads oracle inventory and DSL metadata", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await loadTestOracleInventoryRequest(loadJson, "webdriver_generic");
    await loadDslOracleMetadataRequest(loadJson);

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-oracles",
            options: undefined
        },
        {
            path: "/api/test-oracles/dsl/metadata",
            options: undefined
        }
    ]);
});

test("loads Java and DSL oracle files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await loadOracleDslFileRequest(loadJson, "webdriver_generic", "web.testar");
    await loadOracleJavaFileRequest(loadJson, "webdriver_generic", "WebOracle.java");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/dsl/file?path=web.testar",
            options: undefined
        },
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/java/file?path=WebOracle.java",
            options: undefined
        }
    ]);
});

test("saves and compiles Java oracle files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await saveOracleJavaFileRequest(loadJson, "webdriver_generic", "WebOracle.java", "class WebOracle {}");
    await compileOracleJavaFileRequest(loadJson, "webdriver_generic", "WebOracle.java", "class WebOracle {}");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/java/file?path=WebOracle.java",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "class WebOracle {}" })
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/java/file/compile?path=WebOracle.java",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "class WebOracle {}" })
            }
        }
    ]);
});

test("creates and deletes Java and DSL oracle files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await createOracleDslFileRequest(loadJson, "webdriver_generic", "web.testar");
    await createOracleJavaFileRequest(loadJson, "webdriver_generic", "WebOracle.java");
    await deleteOracleDslFileRequest(loadJson, "webdriver_generic", "web.testar");
    await deleteOracleJavaFileRequest(loadJson, "webdriver_generic", "WebOracle.java");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/dsl/file?path=web.testar",
            options: {
                method: "POST"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/java/file?path=WebOracle.java",
            options: {
                method: "POST"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/dsl/file?path=web.testar",
            options: {
                method: "DELETE"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/java/file?path=WebOracle.java",
            options: {
                method: "DELETE"
            }
        }
    ]);
});

test("generates Java from DSL oracle files", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await generateJavaFromOracleDslFileRequest(loadJson, "webdriver_generic", "web.testar", "assert button");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-oracles/dsl/generate-java?path=web.testar",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "assert button" })
            }
        }
    ]);
});
