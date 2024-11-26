package org.testar.statemodel.analysis.metric;

import org.testar.statemodel.StateModelManager;

public interface IMetricsCollector {
    // Collect metrics from the current state model.
    void collectMetrics(String modelIdentifier, StateModelManager stateModelManager, int invalidActions);
    // Used to add additional (min/max/averages) metrics based on the collected metrics after testing is complete.
    void finalizeMetrics();
    // Used to print the metrics to the console.
    void printMetrics();
}
