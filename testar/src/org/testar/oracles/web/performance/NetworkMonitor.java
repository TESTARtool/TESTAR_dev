package org.testar.oracles.web.performance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v140.network.Network;
import org.testar.OutputStructure;

public class NetworkMonitor {
    private final DevTools devTools;

    // GLOBAL: requestId -> record (kept for run-wide diagnostics if you want them)
    private final ConcurrentMap<String, NetworkRecord> records = new ConcurrentHashMap<>();

    // PER-ACTION: requestId -> record (used for summaries/prints)
    private final ConcurrentMap<String, NetworkRecord> actionRecords = new ConcurrentHashMap<>();

    // rolling counters for “active requests” (global; optional)
    private final AtomicInteger inflight = new AtomicInteger(0);

    // current measurement windows (sequence/action)
    private volatile String seqId = null;
    private volatile String actionId = null;
    private volatile long actionStartMs = 0L; // start moment of the current action window

    public NetworkMonitor(DevTools devTools) {
        this.devTools = devTools;
        enable();
    }

    private void enable() {
        devTools.createSession();

        devTools.addListener(Network.requestWillBeSent(), evt -> {
            String id = evt.getRequestId().toString();
            long nowMs = nowMonoMs();

            inflight.incrementAndGet();

            // Global record
            records.putIfAbsent(id, NetworkRecord.fromRequest(evt, currentTags()));

            // PER-ACTION: only include requests that start while an action is active
            if (actionId != null && nowMs >= actionStartMs) {
                actionRecords.putIfAbsent(id, NetworkRecord.fromRequest(evt, currentTags()));
            }
        });

        devTools.addListener(Network.responseReceived(), resp -> {
            String id = resp.getRequestId().toString();

            // Global: if we somehow missed the request event, create a minimal record in ms
            records.compute(id, (k, r) -> {
                NetworkRecord nr = (r != null) ? r : NetworkRecord.minimalFromResponse(resp, currentTags());
                nr.applyResponse(resp, currentTags());
                return nr;
            });

            // Per-action: only if already tracked for the action
            actionRecords.computeIfPresent(id, (k, r) -> {
                r.applyResponse(resp, currentTags());
                return r;
            });
        });

        devTools.addListener(Network.loadingFinished(), fin -> {
            String id = fin.getRequestId().toString();

            records.computeIfPresent(id, (k, r) -> {
                r.applyLoadingFinished(fin);
                return r;
            });

            actionRecords.computeIfPresent(id, (k, r) -> {
                r.applyLoadingFinished(fin);
                return r;
            });

            inflight.updateAndGet(i -> Math.max(0, i - 1));
        });

        devTools.addListener(Network.loadingFailed(), fail -> {
            String id = fail.getRequestId().toString();

            records.compute(id, (k, r) -> {
                NetworkRecord nr = (r == null) ? new NetworkRecord() : r;
                nr.markFailed(fail.getErrorText());
                return nr;
            });

            actionRecords.computeIfPresent(id, (k, r) -> {
                r.markFailed(fail.getErrorText());
                return r;
            });

            inflight.updateAndGet(i -> Math.max(0, i - 1));
        });

        devTools.addListener(Network.dataReceived(), dr -> {
            String id = dr.getRequestId().toString();

            // Global
            records.computeIfPresent(id, (k, r) -> {
                r.applyDataReceived(dr);
                return r;
            });

            // Current action
            actionRecords.computeIfPresent(id, (k, r) -> {
                r.applyDataReceived(dr);
                return r;
            });
        });

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.send(Network.setCacheDisabled(true));
    }

    private NetworkTags currentTags() {
        return new NetworkTags(seqId, actionId);
    }

    // Sequence window
    public void startMonitor(String id) { this.seqId = id; }
    public String getMonitorId() { return this.seqId; }
    public void stopMonitor() { this.seqId = null; }

    // ACTION window (per-step measurement)
    public void beginMeasurement(String id) {
        this.actionId = id;
        this.actionStartMs = nowMonoMs();
        this.actionRecords.clear();
    }
    public String getMeasurementId() { return this.actionId; }
    public void endMeasurement() { this.actionId = null; }

    /**
     * Wait until the CURRENT ACTION has no unfinished requests for a continuous 'quietFor' window,
     * or until 'timeout' elapses. No resource types are ignored.
     */
    public void awaitNetworkIdle(Duration quietFor, Duration timeout) {
        final long deadlineMs = nowMonoMs() + timeout.toMillis();
        long quietStartMs = -1L;

        while (nowMonoMs() < deadlineMs) {
            int active = 0;
            for (NetworkRecord r : actionRecords.values()) {
                if (r == null) continue;
                if (r.durationMs > 0 || r.failed) continue; // finished (or failed) doesn't count as active
                active++;
            }

            if (active == 0) {
                if (quietStartMs < 0) quietStartMs = nowMonoMs();
                if (nowMonoMs() - quietStartMs >= quietFor.toMillis()) return;
            } else {
                quietStartMs = -1L;
            }

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        // Optional: throw new TimeoutException("Network idle (per action) not reached within " + timeout.toMillis() + " ms");
    }

    // Monotonic ms (derived from nanoTime)
    private static long nowMonoMs() {
        return System.nanoTime() / 1_000_000L;
    }

    // === READ APIs (per-action) ===

    /** Per-action summary (non-cumulative) */
    public NetworkSummary getSummary() {
        return NetworkSummary.from(actionRecords.values(), this.seqId, this.actionId);
    }

    /** Per-action individual records (non-cumulative) */
    public Collection<NetworkRecord> getCurrentActionRecords() {
        return actionRecords.values();
    }

    public List<NetworkRecord> getCurrentFinishedActionRecords() {
        return getCurrentActionRecords().stream()
                .filter(r -> r.durationMs > 0 || r.failed) // only finished/failed
                .sorted(Comparator.comparingLong((NetworkRecord r) -> r.durationMs).reversed())
                .collect(Collectors.toList());
    }

    /** Global records (optional diagnostics) */
    public Collection<NetworkRecord> getNetworkRecords() {
        return records.values();
    }

    // === Convenience printer (per-action) ===
    public void printSummaryAndRequests() {
        System.out.print(getSummaryAndRequestsString());
    }

    public void logSummaryAndRequests() {
        String logNetworkFileName = OutputStructure.htmlOutputDir + File.separator
                + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_"
                + OutputStructure.sequenceInnerLoopCount
                + "_performance_debug.log";

        try (FileWriter fileWriter = new FileWriter(logNetworkFileName, true)) {
            fileWriter.write(getSummaryAndRequestsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSummaryAndRequestsString() {
        NetworkSummary s = getSummary();
        Iterable<NetworkRecord> records = getCurrentFinishedActionRecords();
        return buildSummaryAndRequestsString(s, records);
    }

    private String buildSummaryAndRequestsString(NetworkSummary s, Iterable<NetworkRecord> records) {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        sb.append(s.toPrettyString());

        sb.append("=== Loaded resources (slowest first) ===").append(ls);

        for (NetworkRecord r : records) {
            sb.append(r.toLine()).append(ls);
        }

        sb.append("========================================").append(ls);
        return sb.toString();
    }
}
