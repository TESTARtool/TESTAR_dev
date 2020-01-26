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

    private int nrOfStepsExecuted = 0;

    private boolean exceptionThrown = false;

    private String exceptionMessage = "";

    private String trackTrace = "";

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
}
