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