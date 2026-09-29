// Verifies WS-FUNC-TEST-SETTINGS-004.
import test from "node:test";
import assert from "node:assert/strict";
import { visibleSpyProperties } from "../../../src/views/spy/spyInspectorModel.js";

test("shows selected Spy properties beside structural widget metadata", () => {
    const properties = visibleSpyProperties({
        id: "widget-1",
        parentId: "root",
        role: "button",
        enabled: true,
        x: 10,
        y: 20,
        width: 40,
        height: 30,
        properties: { Title: "Submit" }
    });

    assert.equal(properties.some(([key]) => key === "Title"), true);
    assert.equal(properties.some(([key]) => key === "Path"), false);
    assert.equal(properties.some(([key]) => key === "Role"), false);
    assert.equal(properties.some(([key]) => key === "Enabled"), false);
    assert.equal(properties.some(([key]) => key === "WidgetId"), true);
    assert.equal(properties.some(([key]) => key === "ParentId"), true);
});
