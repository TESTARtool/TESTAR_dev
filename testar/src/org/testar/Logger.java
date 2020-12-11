package org.testar;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * Facade for the log4j logger.
 * Reduces the need to initialize the logger in every class.
 * By wrapping {@code log4j.logger.log} with a tag argument a more uniformed log is realized.
 */
public class Logger {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void log(Level level, String tag, String message, Object... params) {
        LOGGER.log(level, "[" + tag + "] " + message, params);
    }
}
