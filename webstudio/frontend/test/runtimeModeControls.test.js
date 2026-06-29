import test from "node:test";
import assert from "node:assert/strict";
import { canStartRuntimeMode } from "../src/runtimeModeControls.js";

test("runtime mode can start with any selected shared workspace", () => {
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "webdriver_generic" }), true);
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "windows_generic" }), true);
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "android_generic" }), true);
});

test("runtime mode cannot start without workspace, while saving, or while running", () => {
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "" }), false);
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "webdriver_generic", saving: true }), false);
    assert.equal(canStartRuntimeMode({ selectedWorkspaceName: "webdriver_generic", running: true }), false);
});
