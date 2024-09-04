package org.testar.action.priorization.llm;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.IActionSelector;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Set;

public class LlmActionSelector implements IActionSelector {
    protected static final Logger logger = LogManager.getLogger();

    // TODO: Make configurable in GUI?
    private final String testGoal;
    private final String host;
    private final int port ;

    private ActionHistory actionHistory = new ActionHistory(5);
    private LlmConversation conversation;

    private Gson gson = new Gson();

    public LlmActionSelector(String testGoal) {
        this.testGoal = testGoal;

        // Use defaults
        this.host = "http://127.0.0.1";
        this.port = 1234;

        initConversation();
    }

    public LlmActionSelector(String testGoal, String host, int port) {
        this.testGoal = testGoal;
        this.host = host;
        this.port = port;

        initConversation();
    }

    private void initConversation() {
        conversation = new LlmConversation();

        // TODO: Make configurable
        String systemMessageLocation = "prompts/llama_test.txt";
        ClassLoader classLoader = LlmActionSelector.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(systemMessageLocation);

        if (inputStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                conversation.addMessage("system", stringBuilder.toString());
            } catch (IOException e) {
                logger.log(Level.ERROR, "Unable to read system prompt file");
                e.printStackTrace();
            }

        } else {
            logger.log(Level.ERROR, "Unable to find system prompt file " + systemMessageLocation);
        }
    }

    private Action selectActionWithLlm(State state, Set<Action> actions) {
        // For debugging
        logger.log(Level.DEBUG, "Available actions: ");
        for (Action action : actions) {
            logger.log(Level.DEBUG, action.toShortString());
        }

        String prompt = generatePrompt(actions);
        logger.log(Level.DEBUG, "Generated prompt: " + prompt);
        conversation.addMessage("user", prompt);

        Action actionToTake = getVerdictFromLlm(new ArrayList<>(actions));
        logger.log(Level.DEBUG, "Selected action: " + actionToTake.toShortString());

        // Remove message to prevent hitting token limit, message will be regenerated each time.
        conversation.getMessages().remove(conversation.getMessages().size() - 1);
        actionHistory.addToHistory(actionToTake, "");

        return actionToTake;
    }

    private Action getVerdictFromLlm(ArrayList<Action> actions) {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(this.host + ":" + this.port + "/v1/chat/completions");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(conversation)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                LlmResponse llmResponse = gson.fromJson(response.body(), LlmResponse.class);
                String responseContent = llmResponse.getChoices().get(0).getMessage().getContent();

                if(StringUtils.isNumeric(responseContent)) {
                    int actionToTake = Integer.parseInt(responseContent);
                    if(actionToTake >= actions.size()) {
                        return actions.get(actionToTake);
                    } else {
                        throw new ArrayIndexOutOfBoundsException("Index requested by LLM is out of bounds.");
                    }
                } else {
                    String[] responseParts = responseContent.split(",");
                    if(responseParts.length == 2) {
                        if(StringUtils.isNumeric(responseContent)) {
                            int actionToTake = Integer.parseInt(responseParts[0]);
                            String parameters = responseParts[1];
                            return setActionParameters(actions.get(actionToTake), parameters);
                        } else {
                            throw new Exception("LLM output is invalid: " + llmResponse);
                        }
                    } else {
                        throw new Exception("LLM output is invalid: " + llmResponse);
                    }
                }

            } else {
                throw new Exception("Server returned " + response.statusCode() + " status code.");
            }

        } catch (Exception e) {
            logger.log(Level.ERROR, "Unable to select action with LLM!");
            e.printStackTrace();
            return null;
        }
    }

    private Action setActionParameters(Action action, String parameters) {
        if(action instanceof WdRemoteTypeAction) {
            ((WdRemoteTypeAction) action).setKeys(parameters);
            return action;
        }

        return action;
    }

    private String generatePrompt(Set<Action> actions) {
        StringBuilder builder = new StringBuilder();
        builder.append("Objective: ").append(testGoal).append(". ");
        builder.append("Available actions: ");

        int i = 0;
        for (Action action : actions) {
            if(action.get(Tags.Enabled)) {
                builder.append(", ");
                String title = action.get(Tags.Title);
                String role = action.get(Tags.Role).name();
                String description = action.get(Tags.Desc);

                builder.append(String.format("(%d,%s,%s,%s)", i, role, title, description));
                i++;
            }
        }
        builder.append(". ");
        builder.append(actionHistory.toString());

        return builder.toString();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        return selectActionWithLlm(state, actions);
    }
}
