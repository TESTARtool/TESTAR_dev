package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BtraceApiClient {

    private String host;

    private final static String STATUS_STARTED = "STARTED";

    public boolean startRecordingMethodInvocation() throws BtraceApiException{
        var startRecordingRequest = new BtraceStartRecordingRequest(host);
        var startRecordingResponse = startRecordingRequest.send();
        return STATUS_STARTED.equals(startRecordingResponse.getStatus());

    }

    public List<MethodInvocation> finishRecordingMethodInvocation() throws BtraceApiException {
        var finishRecordingRequest = new BtraceFinishRecordingRequest(host);
        var finishRecordingResponse = finishRecordingRequest.send();
        return convertToMethodInvocations(finishRecordingResponse.getMethodsExecuted());

    }

    private List<MethodInvocation> convertToMethodInvocations(List<BtraceFinishRecordingResponse.MethodEntry> methodEntries) {
        Map<BtraceFinishRecordingResponse.MethodEntry, Long> countedMethodEntries =
                methodEntries.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        return countedMethodEntries.entrySet()
                                   .stream()
                                   .map(entry -> new MethodInvocation(entry.getKey().getClassName(),
                                                                            entry.getKey().getMethodName(),
                                                                            entry.getKey().getParameterTypes(),
                                                                            entry.getValue())).collect(Collectors.toList());
    }
}
