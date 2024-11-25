package org.testar.statemodel.analysis;

import org.testar.statemodel.StateModelManager;

public interface IMetricsCollector {
    void addMetrics(String modelIdentifier, StateModelManager stateModelManager, int invalidActions);
    void saveMetrics();
    void printMetrics();
}
