package org.testar.action.priorization.llm;

import org.testar.monkey.alayer.Action;

/**
 * LlmParseResult contains the result of parsing the response from the LLM during action selection.
 */
public class LlmParseResult {

    public enum ParseResult {
        SUCCESS, // Parsing was successful.
        SUCCESS_FINISH, // Parsing was successful, LLM wants to terminate test.
        OUT_OF_RANGE, // Action chosen by LLM is out of range.
        PARSE_FAILED, // Failed to parse JSON or response was not JSON.
        INVALID_ACTION // Action chosen by LLM does not exist
    }

    private final Action actionToExecute;
    private final ParseResult parseResult;

    /**
     * Creates a new LlmParseResult.
     * @param actionToExecute The action to execute. Can be null if result was not SUCCESS.
     * @param parseResult Result of the parsing of the LLM response.
     */
    public LlmParseResult(Action actionToExecute, ParseResult parseResult) {
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
