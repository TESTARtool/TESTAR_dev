/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Color;
import org.testar.monkey.alayer.FillPattern;
import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.PasteText;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.visualizers.TextVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class LlmParseActionResponse {
    protected static final Logger logger = LogManager.getLogger();

    private final Gson gson;

    public LlmParseActionResponse() {
        this(new Gson());
    }

    public LlmParseActionResponse(Gson gson) {
        this.gson = gson;
    }

    /**
     * Parses the response sent by the LLM and selects an action if the response was valid.
     * @param actions Set of actions in the current state.
     * @param responseContent The response of the LLM in plaintext.
     * @return LlmParseActionResult containing the result of the parse and the action to execute if parsing was successful.
     */
    public LlmParseActionResult parseLlmResponse(Set<Action> actions, String responseContent) {
        try {
            LlmSelectedAction llmSelectedAction = gson.fromJson(sanitizeJsonResponse(responseContent), LlmSelectedAction.class);

            Action selectedAction = getActionByIdentifier(actions, llmSelectedAction.getActionId());

            // If the selectedAction is a NOP action at this stage, parsing has likely failed.
            // Observed to happen when the LLM selects an actionId that does not exist.
            if(selectedAction instanceof NOP) {
                logger.log(Level.ERROR, "Action AbstractID not found, parsing LLM response has likely failed!: " + responseContent);
                return new LlmParseActionResult(null, LlmParseActionResult.ParseResult.INVALID_ACTION);
            }

            String input = llmSelectedAction.getInput();
            Widget widget = selectedAction.get(Tags.OriginWidget);

            // For interacting with select combobox web widgets
            // A WdSelectListAction is created to change the active value of the combobox
            if(Objects.equals(widget.get(WdTags.WebTagName, ""), "select")) {
                if(Objects.equals(input, "")) {
                    return new LlmParseActionResult(null, LlmParseActionResult.ParseResult.SL_MISSING_INPUT);
                }

                String target = widget.get(WdTags.WebId, "");
                WdSelectListAction.JsTargetMethod method;
                if (target.isEmpty()) {
                    logger.warn("elementId is empty for select widget! Using name target method.");
                    target = widget.get(WdTags.WebName, "");
                    method = WdSelectListAction.JsTargetMethod.NAME;
                } else {
                    method = WdSelectListAction.JsTargetMethod.ID;
                }

                return new LlmParseActionResult(new WdSelectListAction(target, input, widget, method), 
                        LlmParseActionResult.ParseResult.SUCCESS);
            }

            setCompoundActionInputText(selectedAction, input);
            return new LlmParseActionResult(selectedAction, LlmParseActionResult.ParseResult.SUCCESS);

        } catch(JsonParseException e) {
            logger.log(Level.ERROR, "Unable to parse response from LLM to JSON: " + responseContent);
            return new LlmParseActionResult(null, LlmParseActionResult.ParseResult.PARSE_FAILED);
        } catch(NullPointerException e) {
            logger.log(Level.ERROR, "Null response due to LLM parse response error");
            return new LlmParseActionResult(null, LlmParseActionResult.ParseResult.COMMUNICATION_FAILURE);
        } catch(Exception e) {
            logger.log(Level.ERROR, "Exception parsing LLM response");
            return new LlmParseActionResult(null, LlmParseActionResult.ParseResult.COMMUNICATION_FAILURE);
        }
    }

    /**
     * Try to sanitize markdown-style code fences sometimes generated by LLMs
     * @param responseContent
     * @return The sanitized LLM response
     */
    private String sanitizeJsonResponse(String responseContent) {
        if (responseContent == null) return null;

        if (responseContent.startsWith("```")) {
            logger.log(Level.INFO, String.format("Sanitizing Response: [%s]", responseContent));
            // Remove enclosing ```...``` or ```json...```
            responseContent = responseContent.replaceAll("(?s)^```(?:json)?\\s*", "");
            responseContent = responseContent.replaceAll("(?s)\\s*```$", "");
            logger.log(Level.INFO, String.format("Sanitized Response: [%s]", responseContent));
        }

        return responseContent;
    }

    /**
     * Retrieves an action with given actionId.
     * @param actions Set of actions to search.
     * @param actionId ActionId to search for.
     * @return Requested action if found, NOP Action if not found.
     */
    private Action getActionByIdentifier(Set<Action> actions, String actionId) {
        for(Action action : actions) {
            if(action.get(Tags.AbstractID, "").equalsIgnoreCase(actionId)) {
                return action;
            }
        }
        return new NOP();
    }

    /**
     * Sets TESTAR input text of compound Type and PasteText actions.
     * @param action CompoundAction to change.
     * @param inputText The characters to enter into the input field. Can be left empty if not applicable.
     * @return if input text changed.
     */
    private boolean setCompoundActionInputText(Action action, String inputText) {
        //TODO: Create single actions in protocol so this is not necessary?
        if(action instanceof CompoundAction) {
            for(Action innerAction : ((CompoundAction)action).getActions()) {
                if (innerAction instanceof Type || innerAction instanceof PasteText) {
                    return updateTextAction(action, innerAction, inputText);
                }
            }
        }

        return false;
    }

    private boolean updateTextAction(Action action, Action innerAction, String inputText) {
        innerAction.set(Tags.InputText, inputText);

        String widgetDesc = action.get(Tags.OriginWidget).get(Tags.Desc, "<no description>");
        action.set(Tags.Desc, innerAction.getClass().getSimpleName() + " '" + inputText + "' into '" + widgetDesc + "'");

        if(action.get(Tags.Visualizer) instanceof TextVisualizer) {
            TextVisualizer textVisualizer = (TextVisualizer) action.get(Tags.Visualizer);
            Pen newPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).setStrokeWidth(50).build();
            action.set(Tags.Visualizer, textVisualizer.withText(inputText, newPen));
        }

        return true;
    }

}
