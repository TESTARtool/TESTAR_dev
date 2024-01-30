/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2023 Universitat Politecnica de Valencia - www.upv.es
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
    protected LogErrorDetector detector;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Create a new PlainLinebasedLogChecker.
     *
     * @param commands A list of commands of which the standard output is to be checked
     * @param files A list of log files to check for newly appended lines.
     * @param detector a LogErrorDetector to find errors (if any) in new lines
     */
    public PlainLinebasedLogChecker(List<String> commands, List<String> files, LogErrorDetector detector) {
        this.commands = commands;
        this.files = files;
        this.detector = detector;

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
                readNewLines(getCommandBufferedReader(command), this.logCommandPtr, command);
            }
            catch (IOException e) {
                logger.warn("LogChecker could not run command " + command + ":" + e.toString());
            }
        }

        for ( String file : files) {
            try {
                readNewLines(getFileBufferedReader(file), this.logFilePtr, file);
            }
            catch (IOException e) {
                logger.warn("LogChecker could not read file " + file + ":" + e.toString());
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
                logger.trace("LogChecker could not run command " + command + ":" + e.toString());
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
            catch (IOException e) {
                logger.trace("LogChecker could not read file " + file + ":" + e.toString());
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
            }
        }

        if ( currentLine + 1 < startLine ) {
            logger.trace("log file / log command returned fewer lines than before. LogChecker is resetting counter. ");
        }

        startLineMap.put(startLineId, currentLine + 1);
        return output;
    }
}