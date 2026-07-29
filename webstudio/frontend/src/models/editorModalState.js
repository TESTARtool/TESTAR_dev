// Implements WS-UX-CONFIG-GUARD-001: prevents editor modal dismissal while save/compile is running.
export function editorModalCanClose(saving) {
    return !saving;
}
