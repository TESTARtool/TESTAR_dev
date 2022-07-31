package org.testar.oracles.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  This is the standard implementation of LogChecker for the LogOracle. It
 *  checks files and/or command output for new lines that have been appended.
 *  Each new line is considered to be a message. The LogDetector that the LogChecker
 *  is initialized with is applied to each new message to find errors.
 */

class PlainLinebasedLogChecker extends LogChecker {
    protected List<String> commands, files;
    protected Map<String,Long> logCommandPtr, logFilePtr;
    protected int numberEntries;
    protected boolean logLines=false;
    protected LogErrorDetector detector;
    protected Logger logger;

    /**
     * Create a new PlainLinebasedLogChecker.
     *
     * @param commands A list of commands of which the standard output is to be checked
     * @param files A list of log files to check for newly appended lines.
     * @param detector a LogErrorDetector to find errors (if any) in new lines
     * @param logLines whether to log all lines processed by the checker
     */
    public PlainLinebasedLogChecker(List<String> commands, List<String> files, LogErrorDetector detector, boolean logLines) {
        this.commands = commands;
        this.files = files;
        this.detector = detector;
        this.logLines = logLines;
        this.logger = LogManager.getLogger();

        this.logCommandPtr = new HashMap<String,Long>();
        for ( String command : commands ) {
            logCommandPtr.put(command, Long.valueOf(0l));
        }

        this.logFilePtr = new HashMap<String,Long>();
        for ( String file : files ) {
            logFilePtr.put(file, Long.valueOf(0l));
        }

        this.numberEntries = commands.size() + files.size();

    }

    /**
     *  Records the initial number of lines in each log file and the output of each command, for
     *  later reference
     */
    @Override
    public void initialRead() {
        for ( String command : commands) {
            try {
                List<String> out = readNewLines(getCommandBufferedReader(command), this.logCommandPtr, command);
            }
            catch ( IOException e) {
                logger.error("Warning: LogChecker could not run command " + command + ":" + e.toString());
            }
        }

        for ( String file : files) {
            try {
                List<String> out = readNewLines(getFileBufferedReader(file), this.logFilePtr, file);
            }
            catch ( IOException e ) {
                logger.error("Warning: LogChecker could not read file " + file + ":" + e.toString());
            }
        }
    }

    @Override
    public List<String> readAndCheck() {

        ArrayList<String> result = new ArrayList<String>();

        for ( String command : commands) {
            try {
                BufferedReader reader = getCommandBufferedReader(command);
                List<String> output = readNewLines(reader, this.logCommandPtr, command);
                reader.close();

                List<String> errors = detector.detectErrors(output);

                // If there is more than one command / log file to monitor, add a prefix
                // to disambiguate each message.
                for ( String error : errors ) {
                    if ( this.numberEntries > 1 ) {
                        result.add("[Command " + command + "] :" + error);
                    }
                    else {
                        result.add(error);
                    }
                }

            }
            catch (IOException e) {
                logger.error("Warning: LogChecker could not run command " + command + ":" + e.toString());
            }
        }

        for ( String file : files) {
            try {
                BufferedReader reader = getFileBufferedReader(file);
                List<String> output = readNewLines(reader, this.logFilePtr, file);
                reader.close();

                List<String> errors = detector.detectErrors(output);

                // If there is more than one command / log file to monitor, add a prefix
                // to disambiguate each message.
                for ( String error : errors ) {
                    if ( this.numberEntries > 1 ) {
                        result.add("[File " + file + "] :" + error);
                    }
                    else {
                        result.add(error);
                    }
                }

            }
            catch ( IOException e ) {
                logger.error("Warning: LogChecker could not read file " + file + ":" + e.toString());
            }
        }

        return result;

    }

    /**
     *  Internal method for reading new lines from a file or command via a BufferedReader
     *
     * @param reader The BufferedReader to read from
     * @param startLineMap The map in which to record the number of lines read from the command/file
     * @param startLineId The key in the map that refers to the present file or command
     * @return A list of lines
     *
     */
    protected List<String> readNewLines(BufferedReader reader, Map<String,Long> startLineMap, String startLineId) throws IOException {
        List<String> output = new ArrayList<String>();
        long startLine = startLineMap.get(startLineId);
        long currentLine = -1;
        String line = "";

        while ( (line = reader.readLine()) != null ) {
            currentLine++;
            if ( currentLine >= startLine ) {
                output.add(line);
                if ( this.logLines ) {
                    logger.info("LogOracle processed line: "+ line);
                }
            }
        }

        if ( currentLine + 1 < startLine ) {
            logger.warn("Warning log file / log command returned fewer lines than before. LogChecker is resetting counter. ");
        }

        startLineMap.put(startLineId, currentLine + 1);
        return output;
    }
}