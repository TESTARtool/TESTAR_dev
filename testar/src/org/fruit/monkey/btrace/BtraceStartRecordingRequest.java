package org.fruit.monkey.btrace;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public class BtraceStartRecordingRequest extends BtraceRequest<BtraceStartRecordingResponse>{

    private static final String START_RECORDING_ENDPOINT_PATH = "/api/btrace/action/start";
    private String sequenceId;

    public BtraceStartRecordingRequest(String host, String sequenceId) {
        super(host, BtraceStartRecordingResponse.class);
        this.sequenceId = sequenceId;
    }

    protected HttpUriRequestBase makeRequest(String uri) {
        return new HttpPost(String.format("%s?sequence_id=%s", uri, sequenceId));
    }

    @Override
    protected String buildUri() {
        return host + START_RECORDING_ENDPOINT_PATH;
    }
}
