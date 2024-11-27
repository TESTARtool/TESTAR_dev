package org.testar.statemodel.analysis.condition;

import org.openqa.selenium.InvalidArgumentException;
import org.testar.statemodel.StateModelManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Condition that searches for a string in a state.
public class StateCondition extends TestCondition {
    private String field;
    private String searchMessage;

    public StateCondition(String field, String searchMessage, ConditionComparator comparator, int threshold) {
        super(comparator, threshold);
        this.field = field;
        this.searchMessage = searchMessage;
    }

    public String getField() {
        return field;
    }

    public String getSearchMessage() {
        return searchMessage;
    }

    @Override
    public boolean evaluate(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS found ");
        queryBuilder.append("FROM ConcreteState ");
        queryBuilder.append("WHERE uid LIKE '" + modelIdentifier + "%' ");
        queryBuilder.append("AND " + getField() + " LIKE '%" + getSearchMessage() + "%'");

        String query = queryBuilder.toString();
        String result = stateModelManager.queryStateModel(query);

        int matches = parseQueryResponse(result, "found");
        int threshold = getThreshold();

        switch(getComparator()) {
            case EQUAL:
                return matches == threshold;
            case LESS_THAN:
                return matches < threshold;
            case GREATER_THAN:
                return matches > threshold;
            case LESS_THAN_EQUALS:
                return matches <= threshold;
            case GREATER_THAN_EQUALS:
                return matches >= threshold;
            default:
                throw new InvalidArgumentException("Invalid comparator for condition!");
        }
    }

    // TODO: Move to util class, duplicate
    private int parseQueryResponse(String output, String field) {
        String regex = field + ":\\s*(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);

        if (matcher.find()) {
            // Parse the matched group as an integer
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }
}
