import test from "node:test";
import assert from "node:assert/strict";
import { committedSelectChangeState } from "../../src/app/committedSelectModel.js";

test("keeps select controls displaying the committed value during guarded changes", () => {
    assert.deepEqual(
        committedSelectChangeState("webdriver_generic", "android_generic"),
        {
            requestedValue: "android_generic",
            displayedValue: "webdriver_generic"
        }
    );
});

test("normalizes requested and displayed select values", () => {
    assert.deepEqual(
        committedSelectChangeState("advanced", "unknown", (value) => value === "basic" ? "basic" : "advanced"),
        {
            requestedValue: "advanced",
            displayedValue: "advanced"
        }
    );
});
