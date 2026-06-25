import test from "node:test";
import assert from "node:assert/strict";
import {
    filterResultFiles,
    filterResultGroups,
    formatResultFileLabel,
    sortedResultGroups,
    summarizeResultGroup
} from "../src/testResultsViewModel.js";

test("selecting an output group can show summary without selecting first file", () => {
    const summary = summarizeResultGroup({
        totalSequenceCount: 2,
        files: [
            { name: "run_sequence_1_V001_OK.html", status: "ok" },
            { name: "run_sequence_2_V001_SUSPICIOUS_TAG.html", status: "failed" }
        ]
    });

    assert.equal(summary.okCount, 1);
    assert.equal(summary.failedSequenceCount, 1);
    assert.equal(summary.failedVerdictCount, 1);
});

test("sorts output result groups latest first by default", () => {
    const groups = sortedResultGroups([
        { name: "2026-06-16_10h00m00s_run" },
        { name: "2026-06-16_12h00m00s_run" }
    ]);

    assert.equal(groups[0].name, "2026-06-16_12h00m00s_run");
});

test("filters failed output result groups and generated files", () => {
    assert.deepEqual(
        filterResultGroups([
            { name: "ok-run", status: "ok" },
            { name: "failed-run", status: "failed" }
        ], "failed").map((group) => group.name),
        ["failed-run"]
    );

    assert.deepEqual(
        filterResultFiles([
            { name: "ok.html", status: "ok" },
            { name: "failed.html", status: "failed" }
        ], "failed").map((file) => file.name),
        ["failed.html"]
    );
});

test("formats result file labels using sequence suffix", () => {
    assert.equal(
        formatResultFileLabel("2026-06-16_22h49m58s_webdriver_parabank_1_sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html"),
        "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT"
    );
});
