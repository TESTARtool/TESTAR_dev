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

import com.google.gson.Gson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.IActionSelector;
import org.testar.llm.prompt.IPromptActionGenerator;
import org.testar.llm.LlmConversation;
import org.testar.llm.LlmFactory;
import org.testar.llm.LlmResponse;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.LlmUtils;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.settings.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Protocol for selecting actions using a large language model (LLM).
 * The LLM picks an action based on a test goal and given list of actions in the form of a prompt.
 * The selector communicates with the LLM using a Web API that complies with the OpenAI API used by OpenAI and LMStudio.
 * https://platform.openai.com/docs/overview
 */
public class LlmActionSelector implements IActionSelector {
    protected static final Logger logger = LogManager.getLogger();
    private IPromptActionGenerator promptGenerator;

    private final String platform;
    private final String model;
    private final String reasoning;
    private final String hostUrl;
    private final String authorizationHeader;
    private final String actionFewshotFile;
    private final String appName;
    private final float temperature;
    private final int historySize;
    private final boolean stateless;

    private ActionHistory actionHistory;
    private LlmConversation conversation;
    private int tokens_used;
    private Integer invalidActions;

    private String previousTestGoal = "";
    private LlmTestGoal currentTestGoal;

    /**
     * Creates a new LlmActionSelector.
     * @param settings with contains:
     * 1. LlmHostAddress for the host of the OpenAI compatible LLM API. Ex: http://127.0.0.1.
     * 2. LlmHostPort for the port of the API.
     * 3. LlmTestGoals for the objective of the test. Ex: Log in with username john and password demo.
     * 4. LlmActionFewshotFile for the fewshot file that contains the prompt instructions.
     * 5. ApplicationName for the name of the SUT. 
     */
    public LlmActionSelector(Settings settings, IPromptActionGenerator generator) {
        this.promptGenerator = generator;

        this.platform = settings.get(ConfigTags.LlmPlatform);
        this.model = settings.get(ConfigTags.LlmModel);
        this.reasoning = settings.get(ConfigTags.LlmReasoning);
        this.hostUrl = settings.get(ConfigTags.LlmHostUrl);
        this.authorizationHeader = settings.get(ConfigTags.LlmAuthorizationHeader);
        this.historySize = settings.get(ConfigTags.LlmHistorySize);
        this.actionFewshotFile = settings.get(ConfigTags.LlmActionFewshotFile);
        this.appName = settings.get(ConfigTags.ApplicationName);
        this.temperature = settings.get(ConfigTags.LlmTemperature);
        actionHistory = new ActionHistory(historySize, generator.getDescriptionTag());
        this.stateless = settings.get(ConfigTags.LlmStateless);

        initializeConversation();
    }

    private void initializeConversation() {
        conversation = LlmFactory.createLlmConversation(this.platform, this.model, this.reasoning, this.temperature);
        conversation.initConversation(this.actionFewshotFile);
    }

    /**
     * Changes the active prompt generator.
     * The next time selectAction is called the new prompt generator will be used.
     * @param generator The new prompt generator to use.
     */
    public void setPromptGenerator(IPromptActionGenerator generator) {
        this.promptGenerator = generator;
    }

    /**
     * Resets the LLM Action Selector with the same settings creating a new conversation and action history.
     * @param newTestGoal The new test goal.
     * @param appendPreviousTestGoal If true, adds the previous test goal to the prompt ("We just accomplished X")
     */
    public void reset(LlmTestGoal newTestGoal, boolean appendPreviousTestGoal) {
        // Reset variables
        tokens_used = 0;
        invalidActions = 0;
        actionHistory.clear();

        if(appendPreviousTestGoal) {
            previousTestGoal = currentTestGoal.getTestGoal();
        } else {
            previousTestGoal = "";
        }

        currentTestGoal = newTestGoal;

        // When a new goal is attached, always re-initialize a new conversation
        initializeConversation();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        // If the stateless option is enabled, initialize a new prompt to reduce tokens usage
        if(this.stateless) initializeConversation();

        return selectActionWithLlm(state, actions);
    }

    /**
     * Selects an action to take using the LLM:
     * 1. The prompt is generated.
     * 2. The prompt is sent to the LLM.
     * 3. The response from the LLM is parsed.
     * TODO: Trim conversation when exceeding token limit?
     * @param state The current state of the SUT.
     * @param actions Set of actions in the current state.
     * @return The action to execute or null if failed.
     */
    private Action selectActionWithLlm(State state, Set<Action> actions) {
        String prompt = promptGenerator.generateActionSelectionPrompt(
                actions, state, actionHistory, appName, currentTestGoal.getTestGoal(), previousTestGoal);

        logger.log(Level.DEBUG, "Generated prompt: " + prompt);
        conversation.addMessage("user", prompt);

        String conversationJson = conversation.buildRequestBody();
        String llmResponse = getResponseFromLlm(conversationJson);
        LlmParseActionResponse llmParseResponse = new LlmParseActionResponse(new Gson());
        LlmParseActionResult llmParseResult = llmParseResponse.parseLlmResponse(actions, llmResponse);

        switch(llmParseResult.getParseResult()) {
            case SUCCESS: {
                Action actionToTake = llmParseResult.getActionToExecute();

                logger.log(Level.DEBUG, "Selected action: " + actionToTake.toShortString());

                conversation.addMessage("user", llmResponse);
                actionHistory.addToHistory(actionToTake);

                return actionToTake;
            }
            // Failures return no operation (NOP) actions to prevent crashing.
            // We do not add these to the action history.
            case OUT_OF_RANGE: {
                conversation.addMessage("user", "The actionId provided was invalid.");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "Invalid actionId");
                invalidActions++;
                return nop;
            }
            case PARSE_FAILED: {
                conversation.addMessage("user",
                        "The output you provided was not formatted correctly. "
                        + "Please use the following format: \n\n"
                        + "{\n"
                        + "\"actionId\": \"ACT0K4\",\n"
                        + "\"input\": \"Text\"\n"
                        + "}");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "Failed to parse LLM response");
                invalidActions++;
                return nop;
            }
            case INVALID_ACTION: {
                conversation.addMessage("user", "The actionId you provided is incorrect or does not exist. " +
                        "Please only select from actions provided to you in the current message");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "Invalid actionId from LLM");
                invalidActions++;
                return nop;
            }
            case SL_MISSING_INPUT: {
                conversation.addMessage("user", "You selected an action to set the value of a combobox " +
                        "but did not provide a value, please try again. \nFor example: To run action 'ACtest' to set" +
                        " the value of combobox 'X' to '12345', return the following: \n" +
                        "{\n" +
                        "\"actionId\": \"ACtest\",\n" +
                        "\"input\": \"12345\"\n" +
                        "}");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "Invalid select list action (no value given)");
                invalidActions++;
                return nop;
            }
            case COMMUNICATION_FAILURE: {
                logger.log(Level.ERROR, "Communication failure with the LLM");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "NOP action due to LLM communication Failure");
                invalidActions++;
                return nop;
            }
            default: {
                logger.log(Level.ERROR, "ParseResult was null, this should never happen!");
                NOP nop = new NOP();
                nop.set(Tags.Desc, "Invalid ParseResult");
                invalidActions++;
                return nop;
            }
        }
    }

    /**
     * Sends a POST request to the LLM's API and returns the response as a string.
     * @param requestBody Request body of the POST request.
     * @return Response content or null if failed.
     */
    private String getResponseFromLlm(String requestBody) {
        String testarVer = Main.TESTAR_VERSION.substring(0, Main.TESTAR_VERSION.indexOf(" "));
        URI uri = URI.create(LlmUtils.replaceApiKeyPlaceholder(this.hostUrl));

        try {
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", "testar/" + testarVer);

            // Check optional Authorization Header parameter
            if (this.authorizationHeader != null && !this.authorizationHeader.isEmpty()) {
                con.setRequestProperty("Authorization", LlmUtils.replaceApiKeyPlaceholder(this.authorizationHeader));
            }

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(10000);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if(con.getResponseCode() == 200) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    LlmResponse llmResponse = LlmFactory.createResponse(this.platform, response);
                    this.tokens_used = llmResponse.getUsageTokens();
                    logger.log(Level.INFO, String.format("LLM tokens_used for action selection: [%s]", this.tokens_used));

                    String responseContent = llmResponse.getResponse();
                    // From testing, response often includes newlines and spaces at the end.
                    // We strip this here to so we can parse the result easier.
                    responseContent = responseContent.replace("\n", "").replace("\r", "");
                    responseContent = responseContent.replaceFirst("\\s++$", "");

                    logger.log(Level.INFO, String.format("LLM Response: [%s]", responseContent));

                    return responseContent;
                }
            } else {
                // If response is not 200 OK, debug the error message
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }

                    logger.log(Level.ERROR, String.format("LLM error code %d response: %s", con.getResponseCode(), errorResponse));

                    throw new Exception("Server returned " + con.getResponseCode() + " status code.");
                }
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Unable to communicate with the LLM due to the cause:");
            if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                logger.log(Level.ERROR, e.getMessage());
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Returns the amount of invalid actions (incorrect actionId, unable to parse llm response, etc.)
     * @return Amount of invalid actions.
     */
    public int getInvalidActions() {
        return invalidActions != null ? invalidActions : 0;
    }
}
