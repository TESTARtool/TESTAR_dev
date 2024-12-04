package org.testar.statemodel.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for helping parse/create queries for the state model manager.
 */
public class QueryHelper {
    /**
     * Method used for parsing simple count queries (see usages).
     * TODO: Could be made more generic in the future to support more data types.
     * @param output Output of the query.
     * @param field Name of the output field to parse.
     * @return Parsed query response.
     */
    public static int parseCountQueryResponse(String output, String field) {
        /**
         * The output of these queries are in the form of:
         * {
         * result: 0
         * }
         * This is similar to JSON, but not valid due to missing quotation marks which is why we use regex.
         */

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
