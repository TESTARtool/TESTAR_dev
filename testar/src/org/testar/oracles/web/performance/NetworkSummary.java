package org.testar.oracles.web.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NetworkSummary {
    public long count;
    public long totalBytes;
    public long stateDurationMs; // wall-clock (DevTools monotonic): max(endMs) - min(startMs)
    public long sumDurationsMs;  // sum of per-request durations (ms)
    public long busyTimeMs;      // union/merged time network was busy (ms)
    public long maxDurationMs;   // slowest single resource (ms)
    public long failedCount;

    public long totalTransferBytes;   // sum of transferBytes (LoadingFinished)

    public Map<String, Long> byResourceType = new HashMap<>();
    public Map<String, Long> byFrame = new HashMap<>();
    public Map<Integer, Long> byStatus = new HashMap<>();

    public static NetworkSummary from(Collection<NetworkRecord> recs, String seq, String action) {
        NetworkSummary s = new NetworkSummary();
        long minStart = Long.MAX_VALUE;
        long maxEnd = 0L;

        // collect [startMs, endMs] intervals for busy-time union
        List<long[]> intervals = new ArrayList<>();

        for (NetworkRecord r : recs) {
            if (r == null) continue;
            if (seq != null && !Objects.equals(seq, r.sequenceId)) continue;
            if (action != null && !Objects.equals(action, r.actionId)) continue;

            // Track span even for unfinished if timestamps are present
            if (r.startMs > 0) minStart = Math.min(minStart, r.startMs);
            if (r.endMs   > 0) maxEnd   = Math.max(maxEnd,   r.endMs);

            // Only finished (or failed) requests contribute to metrics
            if (r.durationMs <= 0 && !r.failed) continue;

            s.count++;
            s.totalBytes += r.encodedBytes;
            s.maxDurationMs = Math.max(s.maxDurationMs, r.durationMs);
            if (r.failed) s.failedCount++;

            s.totalTransferBytes   += r.transferBytes;

            if (r.resourceType != null) s.byResourceType.merge(r.resourceType, 1L, Long::sum);
            if (r.frameId != null)      s.byFrame.merge(r.frameId, 1L, Long::sum);
            if (!r.failed && r.status != 0) s.byStatus.merge(r.status, 1L, Long::sum);

            if (r.startMs > 0 && r.endMs > 0) {
                s.sumDurationsMs += r.durationMs;
                intervals.add(new long[]{ r.startMs, r.endMs }); // already ms
            }
        }

        // wall-clock span in ms
        if (maxEnd > 0 && minStart < Long.MAX_VALUE) {
            s.stateDurationMs = (maxEnd - minStart);
        } else {
            s.stateDurationMs = 0;
        }

        // merge intervals to compute busyTimeMs (union) in ms
        if (!intervals.isEmpty()) {
            intervals.sort(Comparator.comparingLong(a -> a[0]));
            long curStart = intervals.get(0)[0];
            long curEnd   = intervals.get(0)[1];
            long total = 0L;

            for (int i = 1; i < intervals.size(); i++) {
                long s0 = intervals.get(i)[0], e0 = intervals.get(i)[1];
                if (s0 <= curEnd) {
                    // overlap -> extend
                    curEnd = Math.max(curEnd, e0);
                } else {
                    total += (curEnd - curStart);
                    curStart = s0; curEnd = e0;
                }
            }
            total += (curEnd - curStart);
            s.busyTimeMs = total;
        }

        return s;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== Network Summary ============\n");
        sb.append("Requests:       ").append(count).append('\n');
        sb.append("Bytes:          ").append(totalBytes).append('\n');
        sb.append("State Duration: ").append(stateDurationMs).append(" ms\n");
        sb.append("Network Busy:   ").append(busyTimeMs).append(" ms\n");
        sb.append("Sum of items:   ").append(sumDurationsMs).append(" ms\n");
        sb.append("Max item dur:   ").append(maxDurationMs).append(" ms\n");
        sb.append("Total bytes:    ").append(totalTransferBytes).append('\n');
        if (failedCount > 0) sb.append("Failed:         ").append(failedCount).append('\n');

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
        sb.append("========================================\n");
        return sb.toString();
    }
}
