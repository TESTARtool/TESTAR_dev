package org.testar.statemodel.analysis.metric;

import org.testar.statemodel.StateModelManager;

public class MetricsManager {
    private IMetricsCollector metricsCollector;

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
