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
