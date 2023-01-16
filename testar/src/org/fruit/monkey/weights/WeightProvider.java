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
        isPreparing = btraceApiClient.startRecordingMethodInvocation();
        return isPreparing;
    }

    public boolean isStaticAnalysisAvailable() {
        return repository.staticAnalysisResultAvailable();
    }

    public WeightVerdict provideWeightVerdict() {
        var recordedMethods = btraceApiClient.finishRecordingMethodInvocation();
        System.out.println("RECEIVED RECORDED METHODS: " + recordedMethods);
        isPreparing = false;
        return weightCalculator.calculateWeight(recordedMethods, repository);
    }
}
