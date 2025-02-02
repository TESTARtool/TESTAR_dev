package org.testar.statemodel.analysis.metric;

import org.testar.statemodel.StateModelManager;

/**
 * Interface for collecting, calculating, printing, and storing metrics. (Strategy pattern)
 */
public interface IMetricsCollector {
    /**
     * Collect metrics from the current state model.
     * @param modelIdentifier modelIdentifier of the current test sequence.
     * @param stateModelManager stateModelManager of the current test sequence.
     * @param invalidActions amount of invalid actions observed (can be 0 if not using llm testing).
     */
    void collectMetrics(String modelIdentifier, StateModelManager stateModelManager, int invalidActions);
    /**
     * Adds/calculates additional (min/max/averages) metrics based on the collected metrics after testing is complete.
     */
    void finalizeMetrics();
    /**
     * Prints the metrics into a readable format to the console.
     */
    void printMetrics();
    /**
     * Saves the metrics to a file.
     */
    void saveMetrics(String fileLocation);
}
