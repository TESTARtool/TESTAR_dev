package org.testar.statemodel.analysis;

public interface IMetricsCollector {
    void addMetrics(String modelIdentifier);
    void saveMetrics();
    void printMetrics();
}
