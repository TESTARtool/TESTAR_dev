package org.fruit.monkey.weights;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fruit.monkey.btrace.BtraceApiClient;
import org.fruit.monkey.btrace.MethodInvocation;

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
        for (MethodInvocation m: recordedMethods){
            System.out.println(m.methodName);
            System.out.println(m.parameterTypes);
            System.out.println(m.times);
            System.out.println("----------------");
        }

        isPreparing = false;
        return weightCalculator.calculateWeight(recordedMethods, repository);
    }
}
