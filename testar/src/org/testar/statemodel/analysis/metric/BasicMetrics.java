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

package org.testar.statemodel.analysis.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Set of basic metrics that can be measured during testing.
 */
public abstract class BasicMetrics {
    private List<SequenceMetrics> sequenceMetrics;

    public BasicMetrics() {
        this.sequenceMetrics = new ArrayList<>();
    }

    /**
     * Adds new metrics to the sequenceMetrics.
     * @param metrics Metrics of the new test sequence.
     */
    public void addSequenceMetrics(SequenceMetrics metrics) {
        this.sequenceMetrics.add(metrics);
    }

    public List<SequenceMetrics> getSequenceMetrics() {
        return sequenceMetrics;
    }

    public void setSequenceMetrics(List<SequenceMetrics> sequenceMetrics) {
        this.sequenceMetrics = sequenceMetrics;
    }

    /**
     * Holds basic metrics observed in a single test sequence.
     */
    public static class SequenceMetrics {
        private String modelIdentifier = "";
        private int uniqueStates = -1;
        private int uniqueActions = -1;
        private int totalActions = -1;
        private int invalidActions = -1;
        private int abstractStates = -1;
        private boolean testGoalAccomplished = false;

        public SequenceMetrics(String modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
        }

        public SequenceMetrics() {

        }

        public String getModelIdentifier() {
            return modelIdentifier;
        }

        public void setModelIdentifier(String modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
        }

        public int getUniqueStates() {
            return uniqueStates;
        }

        public void setUniqueStates(int uniqueStates) {
            this.uniqueStates = uniqueStates;
        }

        public int getUniqueActions() {
            return uniqueActions;
        }

        public void setUniqueActions(int uniqueActions) {
            this.uniqueActions = uniqueActions;
        }

        public int getTotalActions() {
            return totalActions;
        }

        public void setTotalActions(int totalActions) {
            this.totalActions = totalActions;
        }

        public int getInvalidActions() {
            return invalidActions;
        }

        public void setInvalidActions(int invalidActions) {
            this.invalidActions = invalidActions;
        }

        public int getAbstractStates() {
            return abstractStates;
        }

        public void setAbstractStates(int abstractStates) {
            this.abstractStates = abstractStates;
        }

        public boolean isTestGoalAccomplished() {
            return testGoalAccomplished;
        }

        public void setTestGoalAccomplished(boolean testGoalAccomplished) {
            this.testGoalAccomplished = testGoalAccomplished;
        }

        public int getRepeatActions() {
            return this.totalActions - this.uniqueActions;
        }
    }
}
