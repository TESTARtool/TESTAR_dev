package org.fruit.monkey.btrace;

public class BtraceFinishRecordingRequest extends BtraceRequest<BtraceFinishRecordingResponse> {

    private static final String FINISH_RECORDING_ENDPOINT_PATH = "/testar/api/action/stop";

    public BtraceFinishRecordingRequest(String host) {
        super(host, BtraceFinishRecordingResponse.class);
    }

    @Override
    protected String buildUri() {
        return host + FINISH_RECORDING_ENDPOINT_PATH;
    }
}
