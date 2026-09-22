export function contentChanged(currentContent, savedContent) {
    return (currentContent || "") !== (savedContent || "");
}

export function settingsChanged(currentContent, savedContent, visualDirty = false) {
    return contentChanged(currentContent, savedContent) || Boolean(visualDirty);
}

export function objectChanged(currentObject, savedObject) {
    return JSON.stringify(currentObject || {}) !== JSON.stringify(savedObject || {});
}

export function objectSnapshot(currentObject) {
    return { ...(currentObject || {}) };
}
