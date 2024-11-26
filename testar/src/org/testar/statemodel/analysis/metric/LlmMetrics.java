package org.testar.statemodel.analysis.metric;

public class LlmMetrics extends BasicMetrics {
    public LlmMetrics() {
        super();
    }

    // Amount of time test goal was accomplished
    private int tgCompleteCount = -1;

    // For sequences that completed the test goal:

    // Minimum states to reach test goal
    private int minConcreteStatesComplete = -1;
    // Max states to reach test goal
    private int maxConcreteStatesComplete = -1;
    // Average amount of states needed to complete test goal
    private double averageConcreteStatesComplete = -1;

    // Minimum states to reach test goal
    private int minAbstractStatesComplete = -1;
    // Max states to reach test goal
    private int maxAbstractStatesComplete = -1;
    // Average amount of states needed to complete test goal
    private double averageAbstractStatesComplete = -1;

    // Min amount of invalid actions
    private int minTotalActionsComplete = -1;
    // Max amount of invalid actions
    private int maxTotalActionsComplete = -1;
    // Average amount of invalid actions;
    private double averageTotalActionsComplete = -1;

    // For all sequences:

    // Min amount of invalid actions
    private int minInvalidActions = -1;
    // Max amount of invalid actions
    private int maxInvalidActions = -1;
    // Average amount of invalid actions;
    private double averageInvalidActions = -1;

    // Min amount of repeat actions (total - unique)
    private int minRepeatActions = -1;
    // Max amount of repeat actions
    private int maxRepeatActions = -1;
    // Average amount of repeat actions;
    private double averageRepeatActions = -1;

    public int getTgCompleteCount() {
        return tgCompleteCount;
    }

    public void setTgCompleteCount(int tgCompleteCount) {
        this.tgCompleteCount = tgCompleteCount;
    }

    public int getMinConcreteStatesComplete() {
        return minConcreteStatesComplete;
    }

    public void setMinConcreteStatesComplete(int minConcreteStatesComplete) {
        this.minConcreteStatesComplete = minConcreteStatesComplete;
    }

    public int getMaxConcreteStatesComplete() {
        return maxConcreteStatesComplete;
    }

    public void setMaxConcreteStatesComplete(int maxConcreteStatesComplete) {
        this.maxConcreteStatesComplete = maxConcreteStatesComplete;
    }

    public double getAverageConcreteStatesComplete() {
        return averageConcreteStatesComplete;
    }

    public void setAverageConcreteStatesComplete(double averageConcreteStatesComplete) {
        this.averageConcreteStatesComplete = averageConcreteStatesComplete;
    }

    public int getMinAbstractStatesComplete() {
        return minAbstractStatesComplete;
    }

    public void setMinAbstractStatesComplete(int minAbstractStatesComplete) {
        this.minAbstractStatesComplete = minAbstractStatesComplete;
    }

    public int getMaxAbstractStatesComplete() {
        return maxAbstractStatesComplete;
    }

    public void setMaxAbstractStatesComplete(int maxAbstractStatesComplete) {
        this.maxAbstractStatesComplete = maxAbstractStatesComplete;
    }

    public double getAverageAbstractStatesComplete() {
        return averageAbstractStatesComplete;
    }

    public void setAverageAbstractStatesComplete(double averageAbstractStatesComplete) {
        this.averageAbstractStatesComplete = averageAbstractStatesComplete;
    }

    public int getMinInvalidActions() {
        return minInvalidActions;
    }

    public void setMinInvalidActions(int minInvalidActions) {
        this.minInvalidActions = minInvalidActions;
    }

    public int getMaxInvalidActions() {
        return maxInvalidActions;
    }

    public void setMaxInvalidActions(int maxInvalidActions) {
        this.maxInvalidActions = maxInvalidActions;
    }

    public double getAverageInvalidActions() {
        return averageInvalidActions;
    }

    public void setAverageInvalidActions(double averageInvalidActions) {
        this.averageInvalidActions = averageInvalidActions;
    }

    public int getMinTotalActionsComplete() {
        return minTotalActionsComplete;
    }

    public void setMinTotalActionsComplete(int minTotalActionsComplete) {
        this.minTotalActionsComplete = minTotalActionsComplete;
    }

    public int getMaxTotalActionsComplete() {
        return maxTotalActionsComplete;
    }

    public void setMaxTotalActionsComplete(int maxTotalActionsComplete) {
        this.maxTotalActionsComplete = maxTotalActionsComplete;
    }

    public double getAverageTotalActionsComplete() {
        return averageTotalActionsComplete;
    }

    public void setAverageTotalActionsComplete(double averageTotalActionsComplete) {
        this.averageTotalActionsComplete = averageTotalActionsComplete;
    }

    public int getMinRepeatActions() {
        return minRepeatActions;
    }

    public void setMinRepeatActions(int minRepeatActions) {
        this.minRepeatActions = minRepeatActions;
    }

    public int getMaxRepeatActions() {
        return maxRepeatActions;
    }

    public void setMaxRepeatActions(int maxRepeatActions) {
        this.maxRepeatActions = maxRepeatActions;
    }

    public double getAverageRepeatActions() {
        return averageRepeatActions;
    }

    public void setAverageRepeatActions(double averageRepeatActions) {
        this.averageRepeatActions = averageRepeatActions;
    }
}
