package org.fruit.monkey.btrace;

public class BtraceStartRecordingRequest extends BtraceRequest<BtraceStartRecordingResponse>{

    private static final String START_RECORDING_ENDPOINT_PATH = "/api/btrace/action/start";

    public BtraceStartRecordingRequest(String host) {
        super(host, BtraceStartRecordingResponse.class);
    }

    @Override
    protected String buildUri() {
        return host + START_RECORDING_ENDPOINT_PATH;
    }
}
