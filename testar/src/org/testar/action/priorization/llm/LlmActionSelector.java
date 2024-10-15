package org.testar.action.priorization.llm;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.IActionSelector;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.WdRemoteClickAction;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

/**
 * Protocol for selecting actions using a large language model (LLM).
 * The LLM picks an action based on a test goal and given list of actions in the form of a prompt.
 * The selector communicates with the LLM using a Web API that complies with the OpenAI API used by OpenAI and LMStudio.
 * https://platform.openai.com/docs/overview
 */
public class LlmActionSelector implements IActionSelector {
    protected static final Logger logger = LogManager.getLogger();

    // TODO: Make configurable in GUI?
    private final String testGoal;
    private final String host;
    private final String appName;
    private final int port ;

    private ActionHistory actionHistory = new ActionHistory(5);
    private LlmConversation conversation;
    private int tokens_used;

    private Gson gson = new Gson();

    /**
     * Creates a new LlmActionSelector. Uses the default host and port for running LMStudio locally.
     * @param testGoal The objective of the test. Ex: Log in with username john and password demo.
     * @param appName The name of the SUT.
     */
    public LlmActionSelector(String testGoal, String appName) {
        this.testGoal = testGoal;

        // Use defaults
        this.host = "http://127.0.0.1";
        this.port = 1234;
        this.appName = appName;

        initConversation();
    }

    /**
     * Creates a new LlmActionSelector.
     * @param testGoal The objective of the test. Ex: Log in with username john and password demo.
     * @param host The host of the OpenAI compatible LLM API. Ex: http://127.0.0.1.
     * @param port The port of the API.
     * @param appName The name of the SUT.
     */
    public LlmActionSelector(String testGoal, String host, int port, String appName) {
        this.testGoal = testGoal;
        this.host = host;
        this.port = port;
        // TODO: Can we extract this from within the protocol?
        this.appName = appName;

        initConversation();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        return selectActionWithLlm(state, actions);
    }

    /**
     * Initializes the conversation with the LLM using the messages provided in JSON format.
     * TODO: Make configurable
     */
    private void initConversation() {
        conversation = new LlmConversation();

        try {
            String initPromptJson = getTextResource("prompts/fewshot.json");
            LlmConversation.Message[] initMessages = gson.fromJson(initPromptJson, LlmConversation.Message[].class);
            for(LlmConversation.Message message : initMessages) {
                conversation.addMessage(message);
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Failed to initialize conversation, LLM quality may be degraded.");
        }
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
        String prompt = generatePrompt(actions);
        logger.log(Level.DEBUG, "Generated prompt: " + prompt);
        conversation.addMessage("user", prompt);

        String conversationJson = gson.toJson(conversation);
        String llmResponse = getResponseFromLlm(conversationJson);
        LlmParseResult llmParseResult = parseLlmResponse(new ArrayList<>(actions), llmResponse);

        switch(llmParseResult.getParseResult()) {
            case SUCCESS -> {
                Action actionToTake = llmParseResult.getActionToExecute();

                logger.log(Level.DEBUG, "Selected action: " + actionToTake.toShortString());

                conversation.addMessage("assistant", llmResponse);
                actionHistory.addToHistory(actionToTake);

                return actionToTake;
            }
            case SUCCESS_FINISH ->  {
                // Terminate test.
                return null;
            }
            // Failures return no operation (NOP) actions to prevent crashing.
            // We do not add these to the action history.
            case OUT_OF_RANGE -> {
                conversation.addMessage("user", "The actionId provided was invalid.");
                return new NOP();
            }
            case PARSE_FAILED -> {
                conversation.addMessage("user", """
                        The output you provided was not formatted correctly. \
                        Please use the following format: \
                        
                        {
                        "actionId": 1,
                        "input": "Text"
                        }
                        """);
                return new NOP();
            }
            default -> {
                logger.log(Level.ERROR, "ParseResult was null, this should never happen!");
                return new NOP();
            }
        }
    }

    /**
     * Generates the prompt to be sent to the LLM based on the set of actions in the current state.
     * The prompt consists of the application name, test goal, available actions, and action history if available.
     * TODO: Add information about the current state, such as the page title.
     * @param actions Set of actions in the current state.
     * @return The generated prompt.
     */
    private String generatePrompt(Set<Action> actions) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("We are testing the \"%s\" web application. ", appName));
        builder.append(String.format("The objective of the test is: %s. ", testGoal));
        builder.append("The following actions are available: ");

        int i = 1;
        for (Action action : actions) {
            try {
                Widget widget = action.get(Tags.OriginWidget);

                if(i != 1) {
                    builder.append(", ");
                }

                String type = action.get(Tags.Role).name();
                String description = widget.get(Tags.Desc, "No description");

                // Depending on the action, format into something the LLM is more likely to understand.
                switch(type) {
                    case "ClickTypeInto":
                        // Differentiate between types of input fields. Example: password -> Password Field
                        String fieldType = StringUtils.capitalize(widget.get(WdTags.WebType, "text"));
                        builder.append(String.format("%d: Type in %sField '%s'", i, fieldType, description));
                        break;
                    case "LeftClickAt":
                        builder.append(String.format("%d: Click on '%s'", i, description));
                        break;
                    default:
                        logger.log(Level.WARN, "Unsupported action type for LLM action selection: " + type);
                        break;
                }

                i++;
            } catch(NoSuchTagException e) {
                // This usually happens when OriginWidget is unknown, so we skip these.
                logger.log(Level.WARN, "Action is missing critical tags, skipping.");
            }
        }
        builder.append(". ");
        if(!actionHistory.getActions().isEmpty()) {
            builder.append(actionHistory.toString());
        }
        builder.append("Which action should be executed to accomplish the test goal?");

        return builder.toString();
    }

    /**
     * Sends a POST request to the LLM's API and returns the response as a string.
     * @param requestBody Request body of the POST request.
     * @return Response content or null if failed.
     */
    private String getResponseFromLlm(String requestBody) {
        String testarVer = Main.TESTAR_VERSION.substring(0, Main.TESTAR_VERSION.indexOf(" "));
        URI uri = URI.create(this.host + ":" + this.port + "/v1/chat/completions");

        logger.log(Level.DEBUG, "Using endpoint: " + uri);

        try {
            URL url = new URL(this.host + ":" + this.port + "/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", "testar/" + testarVer);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(10000);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                LlmResponse modelResponse = gson.fromJson(response.toString(), LlmResponse.class);
                this.tokens_used = modelResponse.getUsage().getTotal_tokens();

                String responseContent = modelResponse.getChoices().get(0).getMessage().getContent();
                // From testing, response often includes newlines and spaces at the end.
                // We strip this here to so we can parse the result easier.
                responseContent = responseContent.replace("\n", "").replace("\r", "");
                responseContent = responseContent.replaceFirst("\\s++$", "");

                logger.log(Level.INFO, String.format("LLM Response: [%s]", responseContent));

                if(con.getResponseCode() == 200) {
                    return responseContent;
                } else {
                    throw new Exception("Server returned " + con.getResponseCode() + " status code.");
                }
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Unable to communicate with the LLM.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses the response sent by the LLM and selects an action if the response was valid.
     * @param actions ArrayList of actions in the current state.
     * @param responseContent The response of the LLM in plaintext.
     * @return LlmParseResult containing the result of the parse and the action to execute if parsing was successful.
     */
    private LlmParseResult parseLlmResponse(ArrayList<Action> actions, String responseContent) {
        try {
            LlmSelection selection = gson.fromJson(responseContent, LlmSelection.class);

            // If actionId is 0 at this stage, parsing has likely failed (there is never an action 0).
            if(selection.getActionId() == 0) {
                logger.log(Level.ERROR, "Action ID is 0, parsing LLM response has likely failed!: " + responseContent);
                return new LlmParseResult(null, LlmParseResult.ParseResult.PARSE_FAILED);
            }

            // actionId -1 is used by the LLM when the LLM thinks the test objective was accomplished.
            // This will terminate the test in the default LLM protocol.
            if(selection.getActionId() == -1) {
                return new LlmParseResult(null, LlmParseResult.ParseResult.SUCCESS_FINISH);
            }

            // ArrayList starts at 0, action list in prompt starts at 1.
            int actionId = selection.getActionId() -1;
            String input = selection.getInput();

            if(actionId >= actions.size()) {
                logger.log(Level.ERROR, String.format("Action %d requested by LLM is out of range!", actionId));
                return new LlmParseResult(null, LlmParseResult.ParseResult.OUT_OF_RANGE);
            } else {
                Action actionToExecute = actions.get(actionId);
                Action convertedAction = convertCompoundAction(actionToExecute, input);
                return new LlmParseResult(convertedAction, LlmParseResult.ParseResult.SUCCESS);
            }
        } catch(JsonParseException e) {
            logger.log(Level.ERROR, "Unable to parse response from LLM to JSON: " + responseContent);
            return new LlmParseResult(null, LlmParseResult.ParseResult.PARSE_FAILED);
        }
    }

    /**
     * Converts TESTAR compound actions into single actions.
     * TESTAR by default creates compound actions. For example: typing multiple random strings into a field.
     * We convert this to a single action. For example: A single action that types in the text the LLM requested.
     * TODO: Create single actions in protocol so this is not necessary?
     * @param action CompoundAction to convert.
     * @param input The characters to enter into the input field. Can be left empty if not applicable.
     * @return Converted action in the form of a WdXAction.
     */
    private Action convertCompoundAction(Action action, String input) {
        // TODO: This will fail if OriginWidget is unavailable, for example WdHistoryBackAction.
        String type = action.get(Tags.Role).name();
        logger.log(Level.INFO, String.format("Action %s - %s", action.getClass().getName(), type));

        Widget widget = action.get(Tags.OriginWidget);

        switch(type) {
            case "ClickTypeInto":
                return new WdRemoteTypeAction((WdWidget) widget, input);
            case "LeftClickAt":
                return new WdRemoteClickAction((WdWidget) widget);
            default:
                return action;
        }
    }

    /**
     * Loads a resource from the resources folder as plain text.
     * @param resourceLocation Location of the resource to load.
     * @return The resource as string.
     * @throws Exception When the resource failed to load or does not exist.
     */
    private String getTextResource(String resourceLocation) throws Exception {
        ClassLoader classLoader = LlmActionSelector.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourceLocation);

        if (inputStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                logger.log(Level.ERROR, "Unable to read resource " + resourceLocation);
                e.printStackTrace();
            }

        } else {
            logger.log(Level.ERROR, "Unable to load resource " + resourceLocation);
        }

        throw new Exception("Failed to load text resource, double check the resource location.");
    }
}
