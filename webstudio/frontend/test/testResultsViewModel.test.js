import test from "node:test";
import assert from "node:assert/strict";
import {
    filterResultFiles,
    filterResultGroups,
    formatResultFileLabel,
    resultGroupMode,
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

test("summarizes CLI output groups as completed and invalid outcomes", () => {
    const summary = summarizeResultGroup({
        name: "2026-06-29_13h23m23s_cli_webdriver_parabank_1",
        totalSequenceCount: 2,
        files: [
            { name: "run_sequence_1_V001_LLM_COMPLETE.html", status: "ok" },
            { name: "run_sequence_2_V001_LLM_INVALID.html", status: "failed" }
        ]
    });

    assert.equal(summary.mode, "cli");
    assert.equal(summary.successLabel, "COMPLETED");
    assert.equal(summary.failureLabel, "INVALID");
    assert.equal(summary.okCount, 1);
    assert.equal(summary.failedSequenceCount, 1);
    assert.equal(summary.failedVerdictCount, 1);
    assert.deepEqual(summary.verdictGroups.map((group) => group.label), ["LLM_INVALID"]);
});

test("detects result group mode from output folder name", () => {
    assert.equal(resultGroupMode({ name: "2026-06-29_13h23m23s_cli_webdriver_parabank_1" }), "cli");
    assert.equal(resultGroupMode({ name: "2026-06-29_13h23m23s_generate_webdriver_parabank_1" }), "generate");
    assert.equal(resultGroupMode({ name: "2026-06-29_13h23m23s_webdriver_parabank_1" }), "generate");
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

test("filters LLM complete output result groups and generated files as successful", () => {
    assert.deepEqual(
        filterResultGroups([
            { name: "2026-06-29_13h23m23s_cli_webdriver_parabank_1", status: "ok" },
            { name: "2026-06-29_13h25m23s_cli_webdriver_parabank_1", status: "failed" }
        ], "ok", "cli").map((group) => group.name),
        ["2026-06-29_13h23m23s_cli_webdriver_parabank_1"]
    );

    assert.deepEqual(
        filterResultFiles([
            { name: "sequence_1_V001_LLM_COMPLETE.html", status: "ok" },
            { name: "sequence_2_V001_LLM_INVALID.html", status: "failed" }
        ], "ok").map((file) => file.name),
        ["sequence_1_V001_LLM_COMPLETE.html"]
    );
});

test("filters output result groups by execution mode", () => {
    const groups = [
        { name: "2026-06-29_13h23m23s_generate_webdriver_parabank_1", status: "ok" },
        { name: "2026-06-29_13h25m23s_cli_webdriver_parabank_1", status: "failed" }
    ];

    assert.deepEqual(
        filterResultGroups(groups, "all", "generate").map((group) => group.name),
        ["2026-06-29_13h23m23s_generate_webdriver_parabank_1"]
    );

    assert.deepEqual(
        filterResultGroups(groups, "all", "cli").map((group) => group.name),
        ["2026-06-29_13h25m23s_cli_webdriver_parabank_1"]
    );

    assert.deepEqual(
        filterResultGroups(groups, "failed", "cli").map((group) => group.name),
        ["2026-06-29_13h25m23s_cli_webdriver_parabank_1"]
    );
});

test("formats result file labels using sequence suffix", () => {
    assert.equal(
        formatResultFileLabel("2026-06-16_22h49m58s_webdriver_parabank_1_sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html"),
        "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT"
    );
});
