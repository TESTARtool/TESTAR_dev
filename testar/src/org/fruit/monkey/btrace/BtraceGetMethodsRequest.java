package org.fruit.monkey.btrace;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public class BtraceGetMethodsRequest extends BtraceRequest<BtraceFinishRecordingResponse> {

    private static final String GET_METHODS_ENDPOINT_PATH = "/api/btrace/action/methods";
    private String sequenceId;
    private long durationMillis;

    protected HttpUriRequestBase makeRequest(String uri) {
        return new HttpGet(String.format("%s?sequence_id=%s&duration_millis=%d", uri, sequenceId, durationMillis));
    }

    public BtraceGetMethodsRequest(String host, String sequenceId, long durationMillis) {
        super(host, BtraceFinishRecordingResponse.class);
        this.sequenceId = sequenceId;
        this.durationMillis = durationMillis;
    }

    @Override
    protected String buildUri() {
        return host + GET_METHODS_ENDPOINT_PATH;
    }
}
