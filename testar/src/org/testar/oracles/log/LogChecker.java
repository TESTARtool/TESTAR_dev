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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * This abstract class contains an interface for LogCheckers - objects that
 * extract log messages from command output or a file, and apply a detector to the
 * output to find possible errors.
 */

public abstract class LogChecker {

    /**
     * This method should be called when the LogOracle initializes, so that the LogChecker can
     * read the initial status of every file / every command output.
     * For example, in a log file format where messages are appended to the end of the file, the
     * LogChecker could record the current number of messages in each file or command output, so
     * that it can later tell which messages are new.
     */
    public abstract void initialRead();

    /**
     * This method should be called by the LogOracle when the status of the SUT needs to be checked.
     * The method should return a list of errors (if any), and update the state of the LogChecker.
     *
     * @return a list of strings, where each string represents an error message.
     */
    public abstract List<String> readAndCheck();

    /**
     * A utility method to get a BufferedReader for a file by its name.
     *
     * @param filename The name of the file
     * @return a BufferedReader for the file
     * @throws FileNotFoundException
     */
    protected BufferedReader getFileBufferedReader (String filename) throws FileNotFoundException {
        return new BufferedReader(new FileReader(filename));
    }

    /**
     * A utility method to get a BufferedReader for the standard output of a command
     *
     * @param command Name of the command
     * @return a BufferedReader for reading standard output of the command
     * @throws IOException
     */
    protected BufferedReader getCommandBufferedReader (String command) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command);
        return new BufferedReader(new InputStreamReader(pr.getInputStream()));
    }

}