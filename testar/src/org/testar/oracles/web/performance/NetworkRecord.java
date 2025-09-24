package org.testar.oracles.web.performance;

import org.openqa.selenium.devtools.v139.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v139.network.model.ResponseReceived;

public class NetworkRecord {
    public String requestId;
    public String url;
    public String frameId;       // unwrapped, never "Optional[...]"
    public String resourceType;  // Document, Script, Image, XHR, Fetch, Stylesheet, Font, Media, WebSocket, etc.
    public String method;        // GET/POST/...
    public int status;
    public boolean failed;
    public String errorText;

    public long startNs;
    public long endNs;
    public long durationMs;
    public long encodedBytes;

    public String sequenceId;
    public String actionId;

    static NetworkRecord fromRequest(RequestWillBeSent evt, NetworkTags t) {
        NetworkRecord r = new NetworkRecord();
        r.requestId = evt.getRequestId().toString();
        r.url = evt.getRequest().getUrl();

        // Unwrap Optional frameId cleanly
        r.frameId = (evt.getFrameId() != null) ? evt.getFrameId().map(Object::toString).orElse("") : "";

        r.resourceType = (evt.getType() != null) ? evt.getType().toString() : "Unknown";
        r.method = (evt.getRequest() != null) ? evt.getRequest().getMethod() : "";

        r.startNs = System.nanoTime();
        r.sequenceId = t.sequenceId;
        r.actionId = t.actionId;
        return r;
    }

    void applyResponse(ResponseReceived resp, NetworkTags t) {
        if (resp.getResponse() != null && resp.getResponse().getStatus() != null) {
            this.status = resp.getResponse().getStatus().intValue();
        }
        // Unwrap Optional frameId cleanly
        this.frameId = (resp.getFrameId() != null) ? resp.getFrameId().map(Object::toString).orElse(this.frameId) : this.frameId;
        this.resourceType = (resp.getType() != null) ? resp.getType().toString() : this.resourceType;

        // Refresh tags to latest window if they changed mid-flight
        this.sequenceId = (t.sequenceId != null ? t.sequenceId : this.sequenceId);
        this.actionId   = (t.actionId   != null ? t.actionId   : this.actionId);
    }

    void markFinished(long endNs, long encodedBytes) {
        this.endNs = endNs;
        this.encodedBytes = encodedBytes;
        this.durationMs = (this.startNs > 0L) ? (endNs - this.startNs) / 1_000_000 : -1;
    }

    void markFailed(String errorText) {
        this.failed = true;
        this.errorText = errorText;
    }

    /** One concise line for console printing */
    public String toLine() {
        String st = failed ? "ERR" : (status == 0 ? "" : Integer.toString(status));
        String typ = (resourceType == null) ? "" : resourceType;
        String frm = (frameId == null) ? "" : frameId;
        String m   = (method == null || method.isEmpty()) ? "" : (method + " ");
        return String.format("%5d ms  %-10s  %-3s  frame=%s  %s%s",
                durationMs, typ, st, frm, m, url);
    }
}
