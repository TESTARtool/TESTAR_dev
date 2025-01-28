package org.testar.action.priorization.llm.prompt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.action.priorization.llm.ActionHistory;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard prompt generator for OpenAI and Gemini.
 * Includes the following information:
 * 1. The name of the SUT.
 * 2. The current test goal and previous test goal if given.
 * 3. The page title.
 * 4. The list of available actions.
 * 5. Action history.
 */
public class StandardPromptGenerator implements IPromptGenerator {
    protected static final Logger logger = LogManager.getLogger();

    /**
     * Creates a new standard prompt generator.
     */
    public StandardPromptGenerator() {

    }

    /**
     * Generates a standard prompt for use with large language models.
     * @param actions Available actions in the current state.
     * @param state The current state.
     * @param history The action history.
     * @param appName The name of the SUT.
     * @param currentTestGoal The current test goal.
     * @param previousTestGoal The previous test goal if one was completed, ignored if empty string.
     * @return The generated prompt.
     */
    @Override
    public String generatePrompt(Set<Action> actions, State state, ActionHistory history,
                                 String appName, String currentTestGoal, String previousTestGoal) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("We are testing the \"%s\" web application. ", appName));

        if(StringUtils.isEmpty(previousTestGoal)) {
            builder.append(String.format("The objective of the test is: %s. ", currentTestGoal));
        } else {
            builder.append(String.format("The following objective was previously achieved: %s. ", previousTestGoal));
            builder.append(String.format("The current objective of the test is: %s. ", currentTestGoal));
        }

        String pageTitle = WdDriver.getRemoteWebDriver().getTitle();
        builder.append(String.format("We are currently on the following page: %s. ", pageTitle));

        builder.append("The following actions are available: ");

        for (Action action : actions) {
            try {
                Widget widget = action.get(Tags.OriginWidget);
                String type = action.get(Tags.Role).name();
                String actionId = action.get(Tags.AbstractID, "Unknown ActionId");
                String description = widget.get(Tags.Desc, "No description");

                // Depending on the action, format into something the LLM is more likely to understand.
                if(Objects.equals(widget.get(WdTags.WebTagName, ""), "select")) {
                    // Workaround for comboboxes
                    List<String> choices = getComboBoxChoices(widget, state);
                    builder.append(String.format("%s: Set ComboBox '%s' to one of the following values: ",
                            actionId, description));
                    for(String choice : choices) {
                        builder.append(String.format("%s,", choice));
                    }
                    builder.append(" ");
                } else {
                    switch (type) {
                        case "ClickTypeInto":
                            // Differentiate between types of input fields. Example: password -> Password Field
                            String fieldType = StringUtils.capitalize(widget.get(WdTags.WebType, "text"));
                            builder.append(String.format("%s: Type in %sField '%s' ", actionId, fieldType, description));
                            break;
                        case "LeftClickAt":
                            builder.append(String.format("%s: Click on '%s' ", actionId, description));
                            break;
                        default:
                            logger.log(Level.WARN, "Unsupported action type for LLM action selection: " + type);
                            break;
                    }
                }

            } catch(NoSuchTagException e) {
                // This usually happens when OriginWidget is unknown, so we skip these.
                logger.log(Level.WARN, "Action is missing critical tags, skipping.");
            }

            builder.append(", ");
        }

        builder.append(". ");

        if(!history.getActions().isEmpty()) {
            builder.append(history.toString());
        }

        builder.append("Which action should be executed to accomplish the test goal?");

        return builder.toString();
    }

    /**
     * Parses a given 'combobox' widget ('select' HTML tag) and returns the list of possible options.
     * @param combobox The combobox widget to parse.
     * @param state The SUT's current state.
     * @return List of options.
     */
    private List<String> getComboBoxChoices(Widget combobox, State state) {
        // TODO: Temporary hack, <select> element in HTML seems to be missing <option> unless we re-retrieve the HTML.
        // Could this be a timing issue? (HTML is retrieved before options are available)
        WdDriver.getRemoteWebDriver().getPageSource();

        String innerHtml = combobox.get(WdTags.WebInnerHTML);

        // Assumes there is a set of <choice> objects
        Pattern choicePattern = Pattern.compile("<option[^>]*>(.*?)</option>");
        Matcher matcher = choicePattern.matcher(innerHtml);
        List<String> choices = new ArrayList<>();
        while (matcher.find()) {
            choices.add(matcher.group(1));
        }
        return choices;
    }
}
