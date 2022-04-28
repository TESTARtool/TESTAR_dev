package org.testar.oracles.log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  The standard implementation of a LogDetector. It matches every log message with a regular
 *  expression to detect errors.
 */

class RegexLogErrorDetector implements LogErrorDetector {
    protected String regex;

    /**
     *  Create a new RegexLogErrorDetector
     *  @param regex Regular expression in Java 8 format to match error messages
     */
    public RegexLogErrorDetector (String regex) {
        this.regex = regex;
    }

    /**
     *  Detect errors in a list of log messages. This method would normally be called by the LogChecker.
     *
     * @param input List of log messages
     * @return List of error messages in an arbitrary format - one item for each error.
     */
    public List<String> detectErrors (List<String> messages) {
        List<String> result = new ArrayList<String>();
        for ( String message : messages) {
            if ( Pattern.matches (regex, message) ) {
                result.add("The following log message contained errors: \"" + message + "\"");
            }
        }
        return result;
    }
}