// Verifies WS-UX-CONFIG-GUARD-001: editor modals stay open during save/compile operations.
import test from "node:test";
import assert from "node:assert/strict";
import { editorModalCanClose } from "../../src/models/editorModalState.js";

test("editor modals can close when no save or compile operation is running", () => {
    assert.equal(editorModalCanClose(false), true);
});

test("editor modals cannot close while save and compile is running", () => {
    assert.equal(editorModalCanClose(true), false);
});
