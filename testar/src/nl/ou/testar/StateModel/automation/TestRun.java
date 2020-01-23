package nl.ou.testar.StateModel.automation;

import java.util.Set;

public class TestRun {

    private int testRunId;

    private int nrOfSequences;

    private int nrOfSteps;

    private boolean resetDbBeforeRun;

    private Set<String> widgets;

    private long startingMS;

    private long endingMS;

    private long runTime;

    private boolean modelIsDeterministic;

    private int nrOfStepsExecuted;

    public TestRun(int testRunId, int nrOfSequences, int nrOfSteps, boolean resetDbBeforeRun, Set<String> widgets) {
        this.testRunId = testRunId;
        this.nrOfSequences = nrOfSequences;
        this.nrOfSteps = nrOfSteps;
        this.resetDbBeforeRun = resetDbBeforeRun;
        this.widgets = widgets;
    }

    public int getTestRunId() {
        return testRunId;
    }

    public int getNrOfSequences() {
        return nrOfSequences;
    }

    public int getNrOfSteps() {
        return nrOfSteps;
    }

    public boolean isResetDbBeforeRun() {
        return resetDbBeforeRun;
    }

    public Set<String> getWidgets() {
        return widgets;
    }

    public long getStartingMS() {
        return startingMS;
    }

    public void setStartingMS(long startingMS) {
        this.startingMS = startingMS;
    }

    public long getEndingMS() {
        return endingMS;
    }

    public void setEndingMS(long endingMS) {
        this.endingMS = endingMS;
        this.runTime = endingMS - startingMS;
    }

    public boolean isModelIsDeterministic() {
        return modelIsDeterministic;
    }

    public void setModelIsDeterministic(boolean modelIsDeterministic) {
        this.modelIsDeterministic = modelIsDeterministic;
    }

    public int getNrOfStepsExecuted() {
        return nrOfStepsExecuted;
    }

    public void setNrOfStepsExecuted(int nrOfStepsExecuted) {
        this.nrOfStepsExecuted = nrOfStepsExecuted;
    }
}
