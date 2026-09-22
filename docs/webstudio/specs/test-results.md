# Test Results

Test Results provides workspace-scoped inspection, filtering, preview, and deletion of generated execution artifacts.

## Functional Requirements

### WS-FUNC-TEST-RESULTS-001 - Workspace-Scoped Test Results

Traceability: [`WS-FUNC-TEST-RESULTS-001`](../SPEC_TRACE.md#ws-func-test-results-001---workspace-scoped-test-results)

The Test Results page inspects output folders for the selected workspace. WebStudio resolves results from `testar/target/install/testar/bin/output/<workspace>` and does not mix artifacts from other workspaces.

Generate and CLI executions write results to the selected workspace output area. Result folder names include the execution mode token after the timestamp: `generate` for scriptless Generate mode and `cli` for CLI mode.

The page supports:

- output result folder selection
- sorting by date or name
- filtering by result type and execution mode
- generated file listing and filtering
- verdict outcome summarization
- HTML report rendering
- deletion of one generated file or one complete output result folder

Selecting an output folder shows its run-level verdict summary by default and does not automatically select the first generated file. Selecting a generated test sequence file shows its HTML report.

Generate summaries use `OK` and `FAILED`. CLI summaries use `COMPLETED` and `INVALID`, based on `LLM_COMPLETE` and `LLM_INVALID` result files.

The empty state is shown when no output folders exist, filters hide all folders, or the last folder is deleted. The left panel remains visible, `Generated Files` shows an empty message, the preview shows clear guidance, and stale selections are cleared.

Filtering changes visibility only and never deletes or modifies files.

Deletion rules:

- deleting a generated file removes only the selected test sequence report
- deleting an output result folder removes the complete selected run folder
- both actions require explicit modal confirmation
- unclear confirmation, cancellation, or closing the modal performs no deletion
- deleting a generated file clears the selected file and returns to the run summary
- deleting an output folder refreshes the list and clears stale selections
- deleting the last output folder shows the default Test Results empty view

## UX Requirements

### WS-UX-TEST-RESULTS-001 - Test Results Inspection View

Traceability: [`WS-UX-TEST-RESULTS-001`](../SPEC_TRACE.md#ws-ux-test-results-001---test-results-inspection-view)

The page provides fast visual understanding of output health.

The left panel contains `Output Results` and `Generated Files`. Output folders support latest-first default sorting, name/date sorting, result-type filtering, and Generate/CLI filtering. Generated files support issue/success filtering and keep fixed or predictable row sizes.

Selecting an output folder shows a run-level verdict summary in the right preview area. Selecting a generated file replaces the summary with the HTML report, and the user can return to the summary. The selected item remains visually distinct.

Summary cards have predictable height. Verdict rows and generated file lists scroll internally instead of changing the page height. The preview uses the remaining stable area.

Status colors are consistent and are accompanied by text or badges: green for successful outcomes, red for failed or invalid outcomes, amber/orange for warnings, and neutral for idle or informational states.

Delete actions are visibly separated from list rows and use confirmation modals. The modal names the selected file or folder, uses stronger wording for folder deletion, and cancellation or ambiguous confirmation never deletes anything.

Workspace changes refresh the result list for the selected workspace. Empty and filtered-empty states preserve the panel layout and remove stale folder, file, summary, and report selections.
