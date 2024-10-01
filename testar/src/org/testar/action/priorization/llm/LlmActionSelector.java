package org.testar.action.priorization.llm;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.IActionSelector;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdRemoteClickAction;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.webdriver.WdWidget;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LlmActionSelector implements IActionSelector {
    protected static final Logger logger = LogManager.getLogger();

    // TODO: Make configurable in GUI?
    private final String testGoal;
    private final String host;
    private final String appName;
    private final int port ;

    private ActionHistory actionHistory = new ActionHistory(5);
    private LlmConversation conversation;

    private Gson gson = new Gson();

    public LlmActionSelector(String testGoal) {
        this.testGoal = testGoal;

        // Use defaults
        this.host = "http://127.0.0.1";
        this.port = 1234;
        this.appName = "Test";

        initConversation();
    }

    public LlmActionSelector(String testGoal, String host, int port, String appName) {
        this.testGoal = testGoal;
        this.host = host;
        this.port = port;
        this.appName = appName;

        initConversation();
    }

    private void initConversation() {
        conversation = new LlmConversation();

        // TODO: Make configurable
        try {
            String initPromptJson = getTextResource("prompts/fewshot.json");
            Type type = new TypeToken<List<LlmConversation.Message[]>>() {}.getType();
            List<LlmConversation.Message> initMessages = gson.fromJson(initPromptJson, type);
            for(LlmConversation.Message message : initMessages) {
                conversation.addMessage(message);
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Failed to initialize conversation, LLM quality may be degraded.");
        }
    }

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

    // TODO: Retry mechanism, Trim conversation when exceeding token limit
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
            case OUT_OF_RANGE -> {
                // TODO: Retry mechanism
                // conversation.addMessage("user", retryOutOfRange);
            }
            case PARSE_FAILED -> {
                // TODO: Retry mechanism
                // conversation.addMessage("user", retryParseFailed);
            }
        }

        return null;
    }

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

    private LlmParseResult parseLlmResponse(ArrayList<Action> actions, String responseContent) {
        try {
            LlmSelection selection = gson.fromJson(responseContent, LlmSelection.class);
            int actionId = selection.getId();
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

    // TODO: Create single actions in protocol so this is unnecessary?
    private Action convertCompoundAction(Action action, String input) {
        String type = action.get(Tags.Role).name();
        logger.log(Level.INFO, String.format("Action %s - %s", action.getClass().getName(), type));

        Widget widget = action.get(Tags.OriginWidget);

        switch(type) {
            case "ClickTypeInto":
                // Actions for typing into a widget.
                // TESTAR by default creates compound actions for typing multiple random strings.
                // We convert this to a single action that types in the text the LLM requested.
                return new WdRemoteTypeAction((WdWidget) widget, input);
            case "LeftClickAt":
                return new WdRemoteClickAction((WdWidget) widget);
            default:
                return action;
        }
    }

    private String generatePrompt(Set<Action> actions) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("We are testing the \"%s\" web application. ", appName));
        builder.append(String.format("The objective of the test is: %s. ", testGoal));
        builder.append("The following actions are available: ");

        int i = 0;
        for (Action action : actions) {
            try {
                Widget widget = action.get(Tags.OriginWidget);

                if(i != 0) {
                    builder.append(", ");
                }

                String type = action.get(Tags.Role).name();
                String description = widget.get(Tags.Desc, "No description");

                // Depending on the action, format into something the LLM is more likely to understand.
                switch(type) {
                    case "ClickTypeInto":
                        // TODO: Differentiate between types of input fields (numeric, password, etc.)
                        builder.append(String.format("%d: Type in TextField '%s'", i, description));
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
        builder.append(actionHistory.toString());
        builder.append("Which action should be executed to accomplish the test goal?");

        return builder.toString();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        return selectActionWithLlm(state, actions);
    }
}
