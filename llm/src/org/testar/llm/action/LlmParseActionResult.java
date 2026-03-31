/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.action;

import org.testar.core.action.Action;

/**
 * LlmParseActionResult contains the result of parsing the response from the LLM during action selection.
 */
public class LlmParseActionResult {

    public enum ParseResult {
        SUCCESS, // Parsing was successful.
        OUT_OF_RANGE, // Action chosen by LLM is out of range.
        PARSE_FAILED, // Failed to parse JSON or response was not JSON.
        INVALID_ACTION, // Action chosen by LLM does not exist.
        SL_MISSING_INPUT, // SelectListAction was selected, but no value was given.
        COMMUNICATION_FAILURE // Other type of communication failure with the LLM.
    }

    private final Action actionToExecute;
    private final ParseResult parseResult;

    /**
     * Creates a new LlmParseActionResult.
     * @param actionToExecute The action to execute. Can be null if result was not SUCCESS.
     * @param parseResult Result of the parsing of the LLM response.
     */
    public LlmParseActionResult(Action actionToExecute, ParseResult parseResult) {
        this.actionToExecute = actionToExecute;
        this.parseResult = parseResult;
    }

    public Action getActionToExecute() {
        return actionToExecute;
    }

    public ParseResult getParseResult() {
        return parseResult;
    }
}
