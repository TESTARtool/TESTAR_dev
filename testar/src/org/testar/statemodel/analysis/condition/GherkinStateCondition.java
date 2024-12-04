package org.testar.statemodel.analysis.condition;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

public class GherkinStateCondition extends StateCondition {

    protected static final Logger logger = LogManager.getLogger();

    public GherkinStateCondition(String field, String searchMessage, ConditionComparator comparator, int threshold) {
        super(field, searchMessage, comparator, threshold);
        logger.log(Level.INFO, String.format("GherkinStateCondition: %s", searchMessage));
    }

    /**
     * Evaluates whether a Gherkin 'Then' condition is true.
     * @param modelIdentifier modelIdentifier of the current test sequence.
     * @param stateModelManager stateModelManager of the current test sequence.
     * @return True if condition is met.
     */
    @Override
    public boolean evaluate(String modelIdentifier, StateModelManager stateModelManager) {
        // Split the search message into individual words
        String[] searchWords = getSearchMessage().split(" ");

        // Initialize a variable to count the total number of 'Then' matched words
        int totalThenMatches = 0;

        // Iterate through each search word and perform the query
        for (String word : searchWords) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT min(1, count(*)) AS found ");
            queryBuilder.append("FROM ConcreteState ");
            queryBuilder.append("WHERE uid LIKE '" + modelIdentifier + "%' ");
            queryBuilder.append("AND " + getField() + ".toLowerCase() LIKE '%" + word.toLowerCase() + "%'");

            String query = queryBuilder.toString();

            String result = stateModelManager.queryStateModel(query);

            // Parse the count of matched entries for this word
            int wordMatch = QueryHelper.parseCountQueryResponse(result, "found");

            // Increment the total match count
            totalThenMatches += wordMatch;
        }

        // Compute the threshold based on the number of search words
        int threshold = (int) Math.ceil((double) (getThreshold() * searchWords.length) / 2);

        switch(getComparator()) {
            case EQUAL:
                return totalThenMatches == threshold;
            case LESS_THAN:
                return totalThenMatches < threshold;
            case GREATER_THAN:
                return totalThenMatches > threshold;
            case LESS_THAN_EQUALS:
                return totalThenMatches <= threshold;
            case GREATER_THAN_EQUALS:
                return totalThenMatches >= threshold;
            default:
                throw new InvalidArgumentException("Invalid comparator for condition!");
        }
    }
}
