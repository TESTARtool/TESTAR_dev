/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.action.priorization.llm;

import org.testar.monkey.alayer.Action;

/**
 * LlmParseActionResult contains the result of parsing the response from the LLM during action selection.
 */
public class LlmParseActionResult {

    public enum ParseResult {
        SUCCESS, // Parsing was successful.
        OUT_OF_RANGE, // Action chosen by LLM is out of range.
        PARSE_FAILED, // Failed to parse JSON or response was not JSON.
        INVALID_ACTION, // Action chosen by LLM does not exist.
        SL_MISSING_INPUT // SelectListAction was selected, but no value was given.
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
