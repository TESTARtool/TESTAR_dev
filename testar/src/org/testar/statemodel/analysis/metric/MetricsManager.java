package org.testar.statemodel.analysis.metric;

import org.testar.statemodel.StateModelManager;

/**
 * Class used to collect and manage metrics using the strategy pattern.
 */
public class MetricsManager {
    private IMetricsCollector metricsCollector;

    /**
     * Creates a new MetricsManager with the selected metrics collector.
     * @param metricsCollector The IMetricsCollector implementation to use.
     */
    public MetricsManager(IMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    public void setMetricsCollector(IMetricsCollector collector) {
        this.metricsCollector = collector;
    }

    public void finish() {
        this.metricsCollector.finalizeMetrics();
        this.metricsCollector.printMetrics();
    }

    // TODO: Find better way to handle invalidActions (we can't easily retrieve these from the state model)
    public void collect(String modelIdentifier, StateModelManager stateModelManager, int invalidActions) {
        metricsCollector.collectMetrics(modelIdentifier, stateModelManager, invalidActions);
    }
}
