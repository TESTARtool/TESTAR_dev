package org.testar.statemodel.analysis.condition;

import org.openqa.selenium.InvalidArgumentException;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

/**
 * Simple condition that searches for a string in all states.
 */
public class StateCondition extends TestCondition {
    private String field;
    private String searchMessage;

    /**
     * Creates a new StateCondition.
     * @param field The field in the state model to serach. Example: WebInnerHTML.
     * @param searchMessage The string to search for.
     * @param comparator The result of the query is compared to the threshold value using the selected operator.
     * @param threshold The result of the query is compared to this value.
     */
    public StateCondition(String field, String searchMessage, ConditionComparator comparator, int threshold) {
        super(comparator, threshold);
        this.field = field;
        this.searchMessage = searchMessage;
    }

    /**
     * Returns the field in the table of the state model to select.
     * @return Selected field.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the string to search in the set field.
     * @return Selected search message.
     */
    public String getSearchMessage() {
        return searchMessage;
    }

    /**
     * Evaluates whether this condition is true.
     * @param modelIdentifier modelIdentifier of the current test sequence.
     * @param stateModelManager stateModelManager of the current test sequence.
     * @return True if condition is met.
     */
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

        int matches = QueryHelper.parseCountQueryResponse(result, "found");
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
}
