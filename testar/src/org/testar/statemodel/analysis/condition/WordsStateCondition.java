package org.testar.statemodel.analysis.condition;

import org.openqa.selenium.InvalidArgumentException;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

public class WordsStateCondition extends StateCondition {

    public WordsStateCondition(String field, String searchMessage, ConditionComparator comparator, int threshold) {
        super(field, searchMessage, comparator, threshold);
    }

    /**
     * Evaluates whether half of the words of a message exists.
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
            String query = "SELECT min(1, count(*)) AS found " +
                    "FROM ConcreteState " +
                    "WHERE uid LIKE ? " +
                    "AND " + getField() + ".toLowerCase() LIKE ?";

            String result = stateModelManager.queryStateModel(query, 
                    modelIdentifier + "%",
                    "%" + word.toLowerCase() + "%");

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
