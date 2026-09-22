// Test Oracles inventory, Java/DSL file, metadata, and generation API requests.
export function testOracleApiPath(workspaceName, suffix = "") {
    const encodedWorkspaceName = encodeURIComponent(workspaceName || "");
    return `/api/workspaces/${encodedWorkspaceName}/test-oracles${suffix}`;
}

function oraclePathQuery(path) {
    return `path=${encodeURIComponent(path)}`;
}

export function dslOracleMetadataUrl() {
    return "/api/test-oracles/dsl/metadata";
}

export async function loadTestOracleInventoryRequest(loadJson, workspaceName) {
    return loadJson(testOracleApiPath(workspaceName));
}

export async function loadDslOracleMetadataRequest(loadJson) {
    return loadJson(dslOracleMetadataUrl());
}

export async function loadOracleDslFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/dsl/file")}?${oraclePathQuery(path)}`);
}

export async function loadOracleJavaFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/java/file")}?${oraclePathQuery(path)}`);
}

export async function saveOracleJavaFileRequest(loadJson, workspaceName, path, content) {
    return loadJson(`${testOracleApiPath(workspaceName, "/java/file")}?${oraclePathQuery(path)}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}

export async function compileOracleJavaFileRequest(loadJson, workspaceName, path, content) {
    return loadJson(`${testOracleApiPath(workspaceName, "/java/file/compile")}?${oraclePathQuery(path)}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}

export async function createOracleDslFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/dsl/file")}?${oraclePathQuery(path)}`, {
        method: "POST"
    });
}

export async function createOracleJavaFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/java/file")}?${oraclePathQuery(path)}`, {
        method: "POST"
    });
}

export async function deleteOracleDslFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/dsl/file")}?${oraclePathQuery(path)}`, {
        method: "DELETE"
    });
}

export async function deleteOracleJavaFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testOracleApiPath(workspaceName, "/java/file")}?${oraclePathQuery(path)}`, {
        method: "DELETE"
    });
}

export async function generateJavaFromOracleDslFileRequest(loadJson, workspaceName, path, content) {
    return loadJson(`${testOracleApiPath(workspaceName, "/dsl/generate-java")}?${oraclePathQuery(path)}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}
