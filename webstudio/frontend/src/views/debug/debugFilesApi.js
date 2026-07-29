// Implements WS-FUNC-DEBUG-FILES-001: Debug Files list and file-content request contracts.
export function debugFilesListUrl() {
    return "/api/debug-files";
}

export function debugFileUrl(debugFile) {
    return `/api/debug-files/${debugFile.name}?path=${encodeURIComponent(debugFile.path)}`;
}

export async function loadDebugFilesRequest(loadJson) {
    return loadJson(debugFilesListUrl());
}

export async function loadDebugFileRequest(loadJson, debugFile) {
    return loadJson(debugFileUrl(debugFile));
}
