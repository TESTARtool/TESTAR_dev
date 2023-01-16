package org.fruit.monkey.weights;

import java.util.HashMap;
import java.util.List;

public interface AnalysedMethodEntryRepository {
    List<AnalysedMethodEntry> findByClassName(String className);

    void saveAll(HashMap<String, List<AnalysedMethodEntry>> entries);

    boolean staticAnalysisResultAvailable();
}
