import test from "node:test";
import assert from "node:assert/strict";
import {
    defaultAbstractStateTags,
    selectedAbstractStateTags,
    updatedAbstractStateTags
} from "../../../src/views/settings/abstractIdentificationModel.js";

test("reads the saved abstract identification selection", () => {
    assert.deepEqual([...selectedAbstractStateTags("WidgetControlType, WebWidgetId")],
        ["WidgetControlType", "WebWidgetId"]);
});

test("updates one attribute without removing another", () => {
    assert.equal(updatedAbstractStateTags("WidgetControlType", "WebWidgetId", true),
        "WidgetControlType,WebWidgetId");
    assert.equal(updatedAbstractStateTags("WidgetControlType,WebWidgetId", "WidgetControlType", false),
        "WebWidgetId");
});

test("restores defaults from backend catalog metadata", () => {
    assert.equal(defaultAbstractStateTags([
        { key: "WidgetControlType", defaultSelected: true },
        { key: "WebWidgetId", defaultSelected: false }
    ]), "WidgetControlType");
});

test("keeps hidden control patterns when editing visible attributes", () => {
    assert.equal(updatedAbstractStateTags("WidgetValuePattern,WidgetControlType", "AndroidWidgetResourceId", true),
        "WidgetValuePattern,WidgetControlType,AndroidWidgetResourceId");
});

test("restore defaults removes hidden non-default control patterns", () => {
    assert.equal(defaultAbstractStateTags([
        { key: "WidgetControlType", defaultSelected: true },
        { key: "AndroidWidgetResourceId", defaultSelected: false }
    ]), "WidgetControlType");
});
