package org.testar.oracles.log;

import java.util.List;

/**
 * This interface will be implemented by classes that detect errors in log messages for the LogOracle.
*/

public interface LogErrorDetector {

    /**
     *  This method need to be implemented by concrete LogErrorDetectors. It detects errors
     *  in a list of log messages. It would normally be called by the LogChecker.
     *
     * @param input List of log messages
     * @return List of error messages in an arbitrary format - one item for each error.
     *
     */
    public List<String> detectErrors (List<String> input);
}