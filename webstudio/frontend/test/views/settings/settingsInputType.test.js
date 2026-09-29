import test from "node:test";
import assert from "node:assert/strict";
import { textInputTypeForSetting } from "../../../src/views/settings/settingsInputType.js";

test("masks the state model datastore password in the visual settings form", () => {
    assert.equal(textInputTypeForSetting({ key: "DataStorePassword" }), "password");
});

test("keeps ordinary string settings visible", () => {
    assert.equal(textInputTypeForSetting({ key: "DataStoreUser" }), "text");
    assert.equal(textInputTypeForSetting({ key: "SUTConnectorValue" }), "text");
});
