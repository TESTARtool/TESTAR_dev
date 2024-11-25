package org.testar.statemodel.analysis;

public class SequenceMetrics {
    private String modelIdentifier;
    private int uniqueStates;
    private int uniqueActions;
    private int totalActions;
    private int invalidActions;
    private int abstractStates;
    private boolean testGoalAccomplished;

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
}
