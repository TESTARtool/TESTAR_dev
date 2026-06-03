package org.testar.settings.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.testar.OutputStructure;

public class WebSocket {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Set<WsContext> CLIENTS = ConcurrentHashMap.newKeySet();
    private static final AtomicReference<TestarThread> ACTIVE_THREAD =
        new AtomicReference<>();
    private static AtomicReference<String> REPORTS_DIR =
        new AtomicReference<>();

    public static void makeWebSocket(Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                CLIENTS.add(ctx);
                System.out.println("WebSocket connected: " + ctx.sessionId());
                ctx.enableAutomaticPings();
            });
            ws.onClose(ctx -> {
                ctx.disableAutomaticPings();
                removeClientBySession(ctx.sessionId());
                System.out.println("WebSocket closed: " + ctx.sessionId());
            });
            ws.onError(WebSocket::handleError);
            ws.onMessage(WebSocket::handleMessage);
        });
    }

    public static boolean isTestarRunning() {
        TestarThread thread = ACTIVE_THREAD.get();
        return thread != null && thread.isAlive();
    }

    public static void registerActiveThread(TestarThread thread) {
        ACTIVE_THREAD.set(thread);
        broadcast(buildStatusMessage("running", "TESTAR run started", thread));
    }

    public static void clearActiveThread(
        TestarThread thread,
        Exception exception
    ) {
        ACTIVE_THREAD.compareAndSet(thread, null);

        String reportsPath = OutputStructure.htmlOutputDir;
        REPORTS_DIR.set(reportsPath);

        if (exception == null) {
            broadcast(
                buildStatusMessage("finished", "TESTAR run finished", thread)
            );
        } else {
            String message =
                exception.getMessage() == null
                    ? exception.toString()
                    : exception.getMessage();
            broadcast(buildStatusMessage("failed", message, thread));
            broadcastLog(message);
        }
    }

    public static String getReportsDir() {
        return REPORTS_DIR.get();
    }

    public static void broadcast(Map<String, Object> payload) {
        String json = toJson(payload);
        CLIENTS.removeIf(client -> !sendSafely(client, json));
    }

    public static void broadcastLog(String logMessage) {
        if (CLIENTS.isEmpty()) {
            return;
        }
        broadcast(buildLogMessage(logMessage));
    }

    public static Map<String, Object> buildLogMessage(String log) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "log");
        payload.put("log", log);
        return payload;
    }

    private static void handleError(WsErrorContext ctx) {
        System.err.println(
            "WebSocket error (" + ctx.sessionId() + "): " + ctx.error()
        );
        removeClientBySession(ctx.sessionId());
        if (ctx.session.isOpen()) {
            ctx.closeSession(1011, "websocket_error");
        }
    }

    private static void handleMessage(WsMessageContext ctx) {
        String command = parseCommand(ctx.message());
        System.out.println(ctx.message());

        switch (command) {
            case "status":
                sendStatus(ctx);
                break;
            default:
                ctx.send(
                    toJson(buildErrorMessage("Unsupported command: " + command))
                );
                break;
        }
    }

    private static void sendStatus(WsContext ctx) {
        TestarThread thread = ACTIVE_THREAD.get();
        if (thread != null && thread.isAlive()) {
            ctx.send(
                toJson(
                    buildStatusMessage(
                        "running",
                        "TESTAR run is active",
                        thread
                    )
                )
            );
        } else {
            ctx.send(
                toJson(buildStatusMessage("idle", "No active TESTAR run", null))
            );
        }
    }

    private static String parseCommand(String rawMessage) {
        String message = rawMessage == null ? "" : rawMessage.trim();
        if (message.isEmpty()) {
            return "status";
        }

        // manually handle JSON strings that contain the key "command" (can change this later)
        if (message.startsWith("{")) {
            try {
                JsonNode root = MAPPER.readTree(message);
                JsonNode command = root.path("command");
                if (command.isTextual()) {
                    return command.asText().trim().toLowerCase(Locale.ENGLISH);
                }
            } catch (IOException ignored) {
                // Fall back to plain text parsing.
            }
        }
        return message.toLowerCase(Locale.ENGLISH);
    }

    private static void removeClientBySession(String sessionId) {
        CLIENTS.removeIf(client -> client.sessionId().equals(sessionId));
    }

    private static boolean sendSafely(WsContext client, String payload) {
        if (!client.session.isOpen()) {
            return false;
        }

        try {
            client.send(payload);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Map<String, Object> buildStatusMessage(
        String status,
        String message,
        TestarThread thread
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "status");
        payload.put("status", status);
        payload.put("message", message);

        if (thread != null) {
            payload.put("threadId", thread.getId());
        }

        return payload;
    }

    private static Map<String, Object> buildErrorMessage(String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "error");
        payload.put("message", message);
        return payload;
    }

    private static String toJson(Map<String, Object> payload) {
        try {
            return MAPPER.writeValueAsString(payload);
        } catch (IOException e) {
            return "{\"type\":\"error\",\"message\":\"Unable to serialize websocket payload\"}";
        }
    }
}
