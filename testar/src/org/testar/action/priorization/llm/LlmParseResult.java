package org.testar.action.priorization.llm;

import org.testar.monkey.alayer.Action;

public class LlmParseResult {
    public enum ParseResult {
        SUCCESS,
        OUT_OF_RANGE,
        PARSE_FAILED
    }

    private Action actionToExecute;
    private ParseResult parseResult;

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
