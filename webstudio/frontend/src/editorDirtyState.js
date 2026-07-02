export function contentChanged(currentContent, savedContent) {
    return (currentContent || "") !== (savedContent || "");
}

export function objectChanged(currentObject, savedObject) {
    return JSON.stringify(currentObject || {}) !== JSON.stringify(savedObject || {});
}

export function objectSnapshot(currentObject) {
    return { ...(currentObject || {}) };
}
