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


    private boolean modelIsDeterministic;

    private int nrOfStepsExecuted;

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
