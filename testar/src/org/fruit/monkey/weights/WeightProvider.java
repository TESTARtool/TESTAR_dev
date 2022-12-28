package org.fruit.monkey.weights;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fruit.monkey.btrace.BtraceApiClient;

@RequiredArgsConstructor
public class WeightProvider {

    private final AnalysedMethodEntryRepository repository;
    private final BtraceApiClient btraceApiClient;
    private final WeightCalculator weightCalculator;

    @Getter
    private boolean isPreparing;

    public boolean startPreparingVerdict() {
        return false;
    }

    public WeightVerdict provideWeightVerdict() {
        return null;
    }
}
