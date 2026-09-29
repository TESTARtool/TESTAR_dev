// Verifies WS-UX-TEST-SETTINGS-004.
import test from "node:test";
import assert from "node:assert/strict";
import {
    availableSpyTags,
    defaultSpyTags,
    selectedSpyTags,
    updateSpyTags
} from "../../../src/views/settings/spyTagsModel.js";

test("reads the semicolon-separated Spy attribute setting", () => {
    assert.deepEqual([...selectedSpyTags("Title; Role ;Title;;")], ["Title", "Role"]);
});

test("includes and excludes attributes without dropping other selections", () => {
    assert.equal(updateSpyTags("Title;UnknownTag", ["Role"], true), "Role;Title;UnknownTag");
    assert.equal(updateSpyTags("Role;Title;UnknownTag", ["Role"], false), "Title;UnknownTag");
});

test("restores configured defaults and retains unknown selected names in the editor", () => {
    const options = [
        { key: "Title", group: "Common", defaultSelected: true },
        { key: "WebTagName", group: "WebDriver", defaultSelected: false }
    ];

    assert.equal(defaultSpyTags(options), "Title");
    assert.deepEqual(availableSpyTags(options, "Title;CustomTag"), [
        ...options,
        { key: "CustomTag", group: "Other", defaultSelected: false }
    ]);
});

test("bulk selections retain existing values until explicitly excluded", () => {
    assert.equal(updateSpyTags("Title", ["WebTagName", "Title"], true), "Title;WebTagName");
    assert.equal(updateSpyTags("Title;WebTagName", ["Title", "WebTagName"], false), "");
});
