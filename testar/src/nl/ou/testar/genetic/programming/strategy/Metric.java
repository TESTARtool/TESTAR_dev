package nl.ou.testar.genetic.programming.strategy;

class Metric {
    private int visitedStates;
    private int executedActions;
    private int uniqueStates;
    private int uniqueActions;

    Metric(final int visitedStates, final int executedActions, final int uniqueStates, final int uniqueActions) {
        this.visitedStates = visitedStates;
        this.executedActions = executedActions;
        this.uniqueStates = uniqueStates;
        this.uniqueActions = uniqueActions;
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
}
