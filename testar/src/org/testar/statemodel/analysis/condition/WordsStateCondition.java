/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
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
                    "AND " + getField() + ".toLowerCase() containstext ?";

            String result = stateModelManager.queryStateModel(query, 
                    modelIdentifier + "%",
                    "'" + word.toLowerCase() + "'");

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
