import test from "node:test";
import assert from "node:assert/strict";
import { shouldShowBlankSelectOption } from "../src/settingsSelectOptions.js";

test("does not show a blank option for enum settings", () => {
    assert.equal(
        shouldShowBlankSelectOption({
            key: "CliStateProjectionMode",
            type: "enum"
        }),
        false
    );
});

test("does not show a blank option for string configured dropdown settings", () => {
    const configuredDropdownSettings = [
        "SUTConnector",
        "LlmReasoning",
        "DataStoreType",
        "DataStoreMode",
        "ActionSelectionAlgorithm"
    ];

    for (const settingKey of configuredDropdownSettings) {
        assert.equal(
            shouldShowBlankSelectOption({
                key: settingKey,
                type: "string",
                options: ["option"]
            }),
            false,
            `${settingKey} must not render an empty dropdown option`
        );
    }
});

test("keeps the blank option for non-dropdown string settings", () => {
    assert.equal(
        shouldShowBlankSelectOption({
            key: "OptionalStringSetting",
            type: "string",
            options: []
        }),
        true
    );
});
