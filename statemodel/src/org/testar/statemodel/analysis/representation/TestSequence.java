/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel.analysis.representation;

import java.util.stream.IntStream;

public class TestSequence {

    public static final int VERDICT_SUCCESS = 1;

    public static final int VERDICT_INTERRUPT_BY_USER = 2;

    public static final int VERDICT_INTERRUPT_BY_SYSTEM = 3;

    public static final int VERDICT_UNKNOWN = 4;

    /**
     * The id for the test sequence.
     */
    private String sequenceId;

    /**
     * The starting date and time of the sequence.
     */
    private String startDateTime;

    /**
     * The number of steps executed in this sequence.
     */
    private String numberOfSteps;

    /**
     * The amount of errors or unexpected results that where encountered.
     */
    private int nrOfErrors;

    /**
     * Indicates whether this test sequence was deterministic -> no non-determinism in the model was introduced
     */
    private boolean deterministic;

    /**
     * An integer value representing the execution verdict for this test sequence.
     */
    private int verdict;

    public TestSequence(String sequenceId, String startDateTime, String numberOfSteps, int verdict, boolean deterministic) {
        this.sequenceId = sequenceId;
        this.startDateTime = startDateTime;
        this.numberOfSteps = numberOfSteps;
        if (IntStream.of(VERDICT_SUCCESS, VERDICT_INTERRUPT_BY_USER, VERDICT_INTERRUPT_BY_SYSTEM).anyMatch(x -> x == verdict)) {
            this.verdict = verdict;
        }
        else {
            this.verdict = VERDICT_UNKNOWN;
        }
        this.deterministic = deterministic;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(String numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public int getVerdict() {
        return verdict;
    }

    public String getVerdictIcon() {
        switch (verdict) {
            case VERDICT_SUCCESS:
                return "fa-thumbs-up";

            case VERDICT_INTERRUPT_BY_USER:
                return "fa-hand-paper";

            case VERDICT_INTERRUPT_BY_SYSTEM:
                return "fa-exclamation";

            default:
                return "fa-question";
        }
    }

    public String getVerdictTooltip() {
        switch (verdict) {
            case VERDICT_SUCCESS:
                return "Succesfully executed.";

            case VERDICT_INTERRUPT_BY_USER:
                return "Execution halted by user.";

            case VERDICT_INTERRUPT_BY_SYSTEM:
                return "Execution halted due to an error.";

            default:
                return "Unknown result";
        }
    }

    public int getNrOfErrors() {
        return nrOfErrors;
    }

    public void setNrOfErrors(int nrOfErrors) {
        this.nrOfErrors = nrOfErrors;
    }

    public String getDeterministicIcon() {
        return deterministic ? "fa-check" : "fa-times";
    }
}
