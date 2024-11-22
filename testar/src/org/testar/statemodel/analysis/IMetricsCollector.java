package org.testar.statemodel.analysis;

import org.testar.statemodel.StateModelManager;

public interface IMetricsCollector {
    void addMetrics(String modelIdentifier, StateModelManager stateModelManager);
    void saveMetrics();
    void printMetrics();
}
