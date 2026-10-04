import test from "node:test";
import assert from "node:assert/strict";
import { isSettingDisabled } from "../../../src/views/settings/settingsFieldState.js";

test("static tree capture follows export enablement without changing its saved value", () => {
    const capture = { key: "StateModelExportStaticGraphIncludeWidgetTrees", value: "true" };
    const enabled = { key: "StateModelExportStaticGraph", value: "false" };
    const groups = [{ settings: [enabled, capture] }];

    assert.equal(isSettingDisabled(capture, groups), true);
    enabled.value = "true";
    assert.equal(isSettingDisabled(capture, groups), false);
    enabled.value = "false";
    assert.equal(isSettingDisabled(capture, groups), true);
    assert.equal(capture.value, "true");
    assert.equal(isSettingDisabled({ key: "StateModelStoreWidgets" }, groups), false);
    assert.equal(isSettingDisabled(capture, []), true);
});
