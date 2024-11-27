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
