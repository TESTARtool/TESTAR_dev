package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BtraceApiClient {

    private String host;

    private final static String STATUS_STARTED = "STARTED";

    public boolean startRecordingMethodInvocation() {
        var startRecordingRequest = new BtraceStartRecordingRequest(host);
        try {
            var startRecordingResponse = startRecordingRequest.send();
            return STATUS_STARTED.equals(startRecordingResponse.getStatus());
        } catch (BtraceApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}
