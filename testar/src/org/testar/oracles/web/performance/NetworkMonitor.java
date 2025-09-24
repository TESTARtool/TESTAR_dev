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
import org.openqa.selenium.devtools.v139.network.Network;
import org.testar.OutputStructure;

public class NetworkMonitor {
    private final DevTools devTools;

    // requestId -> first-seen nanotime (global)
    private final ConcurrentMap<String, Long> startNs = new ConcurrentHashMap<>();
    // GLOBAL: requestId -> record (kept for run-wide diagnostics if you want them)
    private final ConcurrentMap<String, NetworkRecord> records = new ConcurrentHashMap<>();

    // PER-ACTION: requestId -> record (used for summaries/prints)
    private final ConcurrentMap<String, NetworkRecord> actionRecords = new ConcurrentHashMap<>();

    // rolling counters for “active requests” (global; optional)
    private final AtomicInteger inflight = new AtomicInteger(0);

    // current measurement windows (sequence/action)
    private volatile String seqId = null;
    private volatile String actionId = null;
    private volatile long actionStartNs = 0L; // start moment of the current action window

    public NetworkMonitor(DevTools devTools) {
        this.devTools = devTools;
        enable();
    }

    private void enable() {
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.send(Network.setCacheDisabled(true));

        devTools.addListener(Network.requestWillBeSent(), evt -> {
            String id = evt.getRequestId().toString();
            long now = System.nanoTime();

            // Global bookkeeping
            startNs.putIfAbsent(id, now);
            inflight.incrementAndGet();
            records.putIfAbsent(id, NetworkRecord.fromRequest(evt, currentTags()));

            // PER-ACTION: only include requests that start while an action is active
            if (actionId != null && now >= actionStartNs) {
                actionRecords.putIfAbsent(id, NetworkRecord.fromRequest(evt, currentTags()));
            }
        });

        devTools.addListener(Network.responseReceived(), resp -> {
            String id = resp.getRequestId().toString();

            // Update global
            records.compute(id, (k, r) -> {
                NetworkRecord nr = (r == null) ? new NetworkRecord() : r;
                nr.applyResponse(resp, currentTags());
                return nr;
            });

            // Update current action ONLY if this request is tracked for the action
            actionRecords.computeIfPresent(id, (k, r) -> {
                r.applyResponse(resp, currentTags());
                return r;
            });
        });

        devTools.addListener(Network.loadingFinished(), fin -> {
            String id = fin.getRequestId().toString();
            long endNs = System.nanoTime();

            // Global
            records.computeIfPresent(id, (k, r) -> {
                r.markFinished(endNs, (long) fin.getEncodedDataLength());
                return r;
            });

            // Current action
            actionRecords.computeIfPresent(id, (k, r) -> {
                r.markFinished(endNs, (long) fin.getEncodedDataLength());
                return r;
            });

            inflight.updateAndGet(i -> Math.max(0, i - 1));
        });

        devTools.addListener(Network.loadingFailed(), fail -> {
            String id = fail.getRequestId().toString();

            // Global
            records.compute(id, (k, r) -> {
                NetworkRecord nr = (r == null) ? new NetworkRecord() : r;
                nr.markFailed(fail.getErrorText());
                return nr;
            });

            // Current action
            actionRecords.computeIfPresent(id, (k, r) -> {
                r.markFailed(fail.getErrorText());
                return r;
            });

            inflight.updateAndGet(i -> Math.max(0, i - 1));
        });
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
        this.actionStartNs = System.nanoTime();
        this.actionRecords.clear(); // << per-action only
    }
    public String getMeasurementId() { return this.actionId; }
    public void endMeasurement() { this.actionId = null; }

    /**
     * Wait until the CURRENT ACTION has no unfinished requests for a continuous 'quietFor' window,
     * or until 'timeout' elapses. No resource types are ignored.
     */
    public void awaitNetworkIdle(Duration quietFor, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        long quietStart = -1L;

        while (System.nanoTime() < deadline) {
            // Count unfinished requests in the CURRENT ACTION only
            int active = 0;
            for (NetworkRecord r : actionRecords.values()) {
                if (r == null) continue;
                if (r.durationMs > 0 || r.failed) continue; // finished (or failed) doesn't count as active
                active++;
            }

            if (active == 0) {
                if (quietStart < 0) quietStart = System.nanoTime();
                if (System.nanoTime() - quietStart >= quietFor.toNanos()) return;
            } else {
                quietStart = -1L;
            }

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        // Optional: throw new TimeoutException("Network idle (per action) not reached within " + timeout.toMillis() + " ms");
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
        NetworkSummary s = getSummary();

        System.out.println(s.toPrettyString());

        System.out.println("=== Loaded resources (slowest first) ===");

        for (NetworkRecord r : getCurrentFinishedActionRecords()) {
            System.out.println(r.toLine());
        }

        System.out.println("========================================");
    }

    public void logSummaryAndRequests() {
        NetworkSummary s = getSummary();

        String logNetworkFileName = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount 
                + "_performance_debug.log";

        try (FileWriter fileWriter = new FileWriter(logNetworkFileName, true)) {

            fileWriter.write(s.toPrettyString());

            fileWriter.write("=== Loaded resources (slowest first) ===" + System.lineSeparator());

            for (NetworkRecord r : getCurrentFinishedActionRecords()) {
                fileWriter.write(r.toLine() + System.lineSeparator());
            }

            fileWriter.write("========================================" + System.lineSeparator());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
