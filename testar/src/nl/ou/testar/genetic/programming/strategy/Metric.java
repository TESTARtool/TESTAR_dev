package nl.ou.testar.genetic.programming.strategy;

class Metric {
    private int sequenceNo;
    private long sequenceDuration;
    private int visitedStates;
    private int executedActions;
    private int uniqueStates;
    private int uniqueActions;
    private int notFoundActions;
    private int irregularActions;

    Metric(final int sequenceNo, final long sequenceDuration, final int visitedStates, final int executedActions, final int uniqueStates, final int uniqueActions, final int notFoundActions, final int irregularActions) {
        this.sequenceNo = sequenceNo;
        this.sequenceDuration = sequenceDuration;
        this.visitedStates = visitedStates;
        this.executedActions = executedActions;
        this.uniqueStates = uniqueStates;
        this.uniqueActions = uniqueActions;
        this.notFoundActions = notFoundActions;
        this.irregularActions = irregularActions;
    }

    int getSequenceNo() {
        return sequenceNo;
    }

    long getSequenceDuration() {
        return sequenceDuration;
    }

    int getVisitedStates() {
        return visitedStates;
    }

    int getExecutedActions() {
        return executedActions;
    }

    int getUniqueStates() {
        return uniqueStates;
    }

    int getUniqueActions() {
        return uniqueActions;
    }

    int getNotFoundActions() {
        return notFoundActions;
    }

    int getIrregularActions() {
        return irregularActions;
    }
}
