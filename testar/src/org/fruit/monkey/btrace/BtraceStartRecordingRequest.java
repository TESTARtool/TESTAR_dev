package org.fruit.monkey.btrace;

public class BtraceStartRecordingRequest extends BtraceRequest<BtraceStartRecordingResponse>{

    private static final String PROJECTS_ENDPOINT_PATH = "/testar/api/action/start";

    public BtraceStartRecordingRequest(String host) {
        super(host, BtraceStartRecordingResponse.class);
    }

    @Override
    protected String buildUri() {
        return host + PROJECTS_ENDPOINT_PATH;
    }
}
