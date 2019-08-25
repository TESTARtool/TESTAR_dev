package nl.ou.testar.ReinforcementLearning;

public class QlearningValuesForAction {
    private double qValue;
    private double reward;
    private int executionCounter;

    public QlearningValuesForAction(double qValue, double reward, int executionCounter) {
        this.qValue = qValue;
        this.reward = reward;
        this.executionCounter = executionCounter;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(double qValue) {
        this.qValue = qValue;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public int getExecutionCounter() {
        return executionCounter;
    }

    public void setExecutionCounter(int executionCounter) {
        this.executionCounter = executionCounter;
    }
}
