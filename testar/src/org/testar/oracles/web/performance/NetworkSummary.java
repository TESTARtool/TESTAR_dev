package org.testar.oracles.web.performance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NetworkSummary {
    public long count;
    public long totalBytes;
    public long windowDurationMs; // wall-clock: max(endNs) - min(startNs)
    public long maxDurationMs;    // slowest single resource
    public long failedCount;
    public Map<String, Long> byResourceType = new HashMap<>();
    public Map<String, Long> byFrame = new HashMap<>();
    public Map<Integer, Long> byStatus = new HashMap<>();

    public static NetworkSummary from(Collection<NetworkRecord> recs, String seq, String action) {
        NetworkSummary s = new NetworkSummary();
        long minStart = Long.MAX_VALUE;
        long maxEnd = 0L;

        for (NetworkRecord r : recs) {
            if (r == null) continue;
            if (seq != null && !Objects.equals(seq, r.sequenceId)) continue;
            if (action != null && !Objects.equals(action, r.actionId)) continue;

            // Consider only finished (or failed) requests for most fields,
            // but still track minStart/maxEnd if present.
            if (r.startNs > 0) minStart = Math.min(minStart, r.startNs);
            if (r.endNs > 0)   maxEnd   = Math.max(maxEnd,   r.endNs);

            if (r.durationMs <= 0 && !r.failed) continue;

            s.count++;
            s.totalBytes += r.encodedBytes;
            s.maxDurationMs = Math.max(s.maxDurationMs, r.durationMs);
            if (r.failed) s.failedCount++;

            if (r.resourceType != null) s.byResourceType.merge(r.resourceType, 1L, Long::sum);
            if (r.frameId != null)      s.byFrame.merge(r.frameId, 1L, Long::sum);
            if (!r.failed && r.status != 0) s.byStatus.merge(r.status, 1L, Long::sum);
        }

        if (maxEnd > 0 && minStart < Long.MAX_VALUE) {
            s.windowDurationMs = (maxEnd - minStart) / 1_000_000;
        } else {
            s.windowDurationMs = 0;
        }

        return s;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== Network Summary ============\n");
        sb.append("Requests: ").append(count).append('\n');
        sb.append("Bytes:    ").append(totalBytes).append('\n');
        sb.append("Duration: ").append(windowDurationMs).append(" ms\n");
        sb.append("Max dur:  ").append(maxDurationMs).append(" ms\n");
        if (failedCount > 0) sb.append("Failed:   ").append(failedCount).append('\n');

        if (!byStatus.isEmpty()) {
            sb.append("By status:\n");
            byStatus.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append('\n'));
        }
        if (!byResourceType.isEmpty()) {
            sb.append("By resource type:\n");
            byResourceType.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append('\n'));
        }
        if (!byFrame.isEmpty()) {
            sb.append("By frame:\n");
            byFrame.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append('\n'));
        }
        sb.append("========================================");
        return sb.toString();
    }
}
