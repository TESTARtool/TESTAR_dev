package org.testar.oracles.web.performance;

import org.openqa.selenium.devtools.v139.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v139.network.model.ResponseReceived;
import org.openqa.selenium.devtools.v139.network.model.DataReceived;
import org.openqa.selenium.devtools.v139.network.model.LoadingFinished;

public class NetworkRecord {
    public String requestId;
    public String url;
    public String frameId;       // unwrapped, never "Optional[...]"
    public String resourceType;  // Document, Script, Image, XHR, Fetch, Stylesheet, Font, Media, WebSocket, etc.
    public String method;        // GET/POST/...
    public int status;
    public boolean failed;
    public String errorText;

    public long startMs;
    public long endMs;
    public long durationMs;
    public long encodedBytes;

    public long transferBytes;     // from LoadingFinished.encodedDataLength
    public long encodedBodyBytes;  // sum of DataReceived.encodedDataLength
    public long decodedBodyBytes;  // sum of DataReceived.dataLength
    public long headerBytes = -1;  // estimated; -1 = unknown

    public boolean inferredStart; // true when created from ResponseReceived fallback
    public boolean complete;      // true after LoadingFinished

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

        // Use DevTools monotonic timestamp as the start time (seconds -> ms)
        r.startMs = toMillis(evt.getTimestamp().toJson());

        r.sequenceId = t.sequenceId;
        r.actionId = t.actionId;
        return r;
    }

    static NetworkRecord minimalFromResponse(ResponseReceived resp, NetworkTags t) {
        NetworkRecord r = new NetworkRecord();
        r.requestId = resp.getRequestId().toString();
        r.url = (resp.getResponse() != null && resp.getResponse().getUrl() != null)
                ? resp.getResponse().getUrl() : "";
                r.frameId = (resp.getFrameId() != null) ? resp.getFrameId().map(Object::toString).orElse("") : "";
                r.resourceType = (resp.getType() != null) ? resp.getType().toString() : "Unknown";
                r.method = "";
                r.startMs = toMillis(resp.getTimestamp().toJson());
                r.inferredStart = true;
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

        // Provisional end from ResponseReceived (may be earlier than LoadingFinished)
        long respMs = toMillis(resp.getTimestamp().toJson());
        if (respMs > 0) {
            this.endMs = Math.max(this.endMs, respMs);
            recomputeDuration();
        }

        // Refresh tags to latest window if they changed mid-flight
        this.sequenceId = (t.sequenceId != null ? t.sequenceId : this.sequenceId);
        this.actionId   = (t.actionId   != null ? t.actionId   : this.actionId);
    }

    // accumulate sizes from Network.dataReceived
    void applyDataReceived(DataReceived dr) {
        if (dr.getEncodedDataLength() != null) {
            this.encodedBodyBytes += dr.getEncodedDataLength().longValue();
        }
        if (dr.getDataLength() != null) {
            this.decodedBodyBytes += dr.getDataLength().longValue();
        }
    }

    // When finishing, set transfer bytes and estimate header size
    void applyLoadingFinished(LoadingFinished fin) {
        long finishMs = toMillis(fin.getTimestamp().toJson());
        if (finishMs > 0) this.endMs = Math.max(this.endMs, finishMs);

        if (fin.getEncodedDataLength() != null) {
            this.transferBytes = fin.getEncodedDataLength().longValue();
            // Estimate headers using transfer - encoded body (clamped at 0)
            if (this.encodedBodyBytes > 0 || this.transferBytes > 0) {
                long est = this.transferBytes - this.encodedBodyBytes;
                this.headerBytes = Math.max(0L, est);
            }
        }

        this.complete = true;
        recomputeDuration();
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

        //String sizes = String.format(" xfer=%dB enc=%dB dec=%dB", transferBytes, encodedBodyBytes, decodedBodyBytes);
        String sizes = String.format(" size=%dB", transferBytes);

        return String.format("%5d ms %-10s %-3s frame=%s %s\t%s%s",
                durationMs, typ, st, frm, sizes, m, url);
    }

    private static long toMillis(Number seconds) {
        return (seconds == null) ? -1L : Math.round(seconds.doubleValue() * 1000.0);
    }

    private void recomputeDuration() {
        this.durationMs = (this.startMs > 0 && this.endMs >= this.startMs) ? (this.endMs - this.startMs) : -1L;
    }
}
