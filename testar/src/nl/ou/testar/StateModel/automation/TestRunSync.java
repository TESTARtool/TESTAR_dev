package nl.ou.testar.StateModel.automation;

public class TestRunSync {

    private static TestRunSync instance;

    private TestRunSync() {
    }

    public static TestRunSync getInstance() {
        if (instance == null) {
            instance = new TestRunSync();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new TestRunSync();
    }


    private boolean modelIsDeterministic = true;

    private int nrOfNonDeterministicActions = 0;

    private int nrOfStepsExecuted = 0;

    private boolean exceptionThrown = false;

    private String exceptionMessage = "";

    private String trackTrace = "";

    private int nrOfAbstractStates;

    private int nrOfAbstractActions;

    private int nrOfConcreteStates;

    private int nrOfConcreteActions;

    private int nrOfUnvisitedActions;

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

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public void setExceptionThrown(boolean exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getTrackTrace() {
        return trackTrace;
    }

    public void setTrackTrace(String trackTrace) {
        this.trackTrace = trackTrace;
    }

    public int getNrOfAbstractStates() {
        return nrOfAbstractStates;
    }

    public void setNrOfAbstractStates(int nrOfAbstractStates) {
        this.nrOfAbstractStates = nrOfAbstractStates;
    }

    public int getNrOfAbstractActions() {
        return nrOfAbstractActions;
    }

    public void setNrOfAbstractActions(int nrOfAbstractActions) {
        this.nrOfAbstractActions = nrOfAbstractActions;
    }

    public int getNrOfConcreteStates() {
        return nrOfConcreteStates;
    }

    public void setNrOfConcreteStates(int nrOfConcreteStates) {
        this.nrOfConcreteStates = nrOfConcreteStates;
    }

    public int getNrOfConcreteActions() {
        return nrOfConcreteActions;
    }

    public void setNrOfConcreteActions(int nrOfConcreteActions) {
        this.nrOfConcreteActions = nrOfConcreteActions;
    }

    public int getNrOfUnvisitedActions() {
        return nrOfUnvisitedActions;
    }

    public void setNrOfUnvisitedActions(int nrOfUnvisitedActions) {
        this.nrOfUnvisitedActions = nrOfUnvisitedActions;
    }

    public int getNrOfNonDeterministicActions() {
        return nrOfNonDeterministicActions;
    }

    public void setNrOfNonDeterministicActions(int nrOfNonDeterministicActions) {
        this.nrOfNonDeterministicActions = nrOfNonDeterministicActions;
    }
}
