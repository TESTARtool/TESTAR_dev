package org.testar.action.priorization.llm;

import com.google.gson.Gson;
import org.testar.IActionSelector;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Set;

// TODO: Action execution (example: Type something specific in a text field)
// TODO: Parse response from LLM
// TODO: Attach history to web request
public class LlmActionSelector implements IActionSelector {
    // TODO: Make configurable in GUI?
    private final String testGoal;
    private final String host;
    private final int port ;
    private String systemMessage;

    private ActionHistory actionHistory = new ActionHistory(5);
    private LlmConversation conversation;

    private Gson gson = new Gson();

    public LlmActionSelector(String testGoal) {
        this.testGoal = testGoal;

        // Use defaults
        this.host = "http://127.0.0.1";
        this.port = 1234;

        conversation = new LlmConversation();
        conversation.addMessage("system", systemMessage);
    }

    public LlmActionSelector(String testGoal, String host, int port) {
        this.testGoal = testGoal;
        this.host = host;
        this.port = port;

        conversation = new LlmConversation();
        conversation.addMessage("system", systemMessage);
    }

    private Action selectActionWithLlm(State state, Set<Action> actions) {
        String prompt = generatePrompt(actions);
        conversation.addMessage("user", prompt);

        Action actionToTake = getVerdictFromLlm();

        // Remove message to prevent hitting token limit, message will be regenerated each time.
        conversation.getMessages().remove(conversation.getMessages().size() - 1);
        actionHistory.addToHistory(actionToTake, "");

        return actionToTake;
    }


    private Action getVerdictFromLlm() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(this.host + ":" + this.port + "/v1/chat/completions");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(conversation)))
                .build();

        return null;
    }

    private String generatePrompt(Set<Action> actions) {
        StringBuilder builder = new StringBuilder();
        builder.append("The test goal is: '").append(testGoal).append("'.");
        builder.append("Available actions: ");

        int i = 1;
        for (Action action : actions) {
            if(action.get(Tags.Enabled)) {
                String title = action.get(Tags.Title);
                String role = action.get(Tags.Role).name();
                String description = action.get(Tags.Desc);

                builder.append(String.format("(%d,%s,%s,%s)", i, role, title, description));
                i++;
            }
        }

        builder.append(actionHistory.toString());

        return builder.toString();
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        return selectActionWithLlm(state, actions);
    }
}
