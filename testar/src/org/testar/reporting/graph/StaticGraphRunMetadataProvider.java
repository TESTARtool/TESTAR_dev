package org.testar.reporting.graph;

import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StaticGraphRunMetadataProvider {

    private static final Pattern HOST_TIMESTAMP_APP_PATTERN = Pattern.compile(
            "^([A-Za-z0-9-]+)_([0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{2}h[0-9]{2}m[0-9]{2}s)_(.+)$");

    private StaticGraphRunMetadataProvider() {}

    static void writeRunMetadata(Settings settings, String outputDir, String runId, String modelIdentifier, Path runRoot) {
        try {
            String runKey = normalizeRunKey(runId);
            Path runOutputDir = resolveRunOutputDir(Paths.get(outputDir), runKey);
            String verdictTitle = detectVerdictTitle(runOutputDir);
            boolean isComplete = "LLM_COMPLETE".equalsIgnoreCase(verdictTitle);
            String oracleVerdict = detectOracleVerdict(runOutputDir, verdictTitle);
            String llmGoals = serializeGoals(settings.get(ConfigTags.LlmTestGoals, List.of()));

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"runId\": \"").append(escapeJson(runKey)).append("\",\n");
            sb.append("  \"modelIdentifier\": \"").append(escapeJson(modelIdentifier)).append("\",\n");
            sb.append("  \"verdictTitle\": \"").append(escapeJson(verdictTitle)).append("\",\n");
            sb.append("  \"oracleVerdictB64\": \"").append(escapeJson(encodeBase64(oracleVerdict))).append("\",\n");
            sb.append("  \"llmGoalsB64\": \"").append(escapeJson(encodeBase64(llmGoals))).append("\",\n");
            sb.append("  \"isComplete\": ").append(isComplete).append("\n");
            sb.append("}\n");
            Files.write(runRoot.resolve("run.json"), sb.toString().getBytes());
        } catch (Exception e) {
            StaticGraphExporter.log("Failed writing run metadata: " + e.getMessage());
        }
    }

    static StaticGraphRunMeta readRunMeta(Path runRoot) {
        Path json = runRoot.resolve("run.json");
        if (!Files.exists(json)) {
            return new StaticGraphRunMeta("UNKNOWN", "", "", "", false);
        }
        try {
            String text = new String(Files.readAllBytes(json));
            String verdictTitle = extractJsonString(text, "verdictTitle", "UNKNOWN");
            String modelIdentifier = extractJsonString(text, "modelIdentifier", "");
            String oracleVerdict = decodeBase64(extractJsonString(text, "oracleVerdictB64", ""));
            String llmGoalsText = decodeBase64(extractJsonString(text, "llmGoalsB64", ""));
            boolean isComplete = "true".equalsIgnoreCase(extractJsonBool(text, "isComplete", "false"));
            return new StaticGraphRunMeta(verdictTitle, modelIdentifier, oracleVerdict, llmGoalsText, isComplete);
        } catch (Exception e) {
            return new StaticGraphRunMeta("UNKNOWN", "", "", "", false);
        }
    }

    static List<String> splitGoals(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) return List.of();
        String normalized = normalizeGoalText(serialized);
        String[] chunks = normalized.split("\\n\\n<<<GOAL_SEPARATOR>>>\\n\\n");
        java.util.ArrayList<String> result = new java.util.ArrayList<>();
        for (String chunk : chunks) {
            String cleaned = normalizeGoalText(chunk);
            if (!cleaned.isEmpty()) result.add(cleaned);
        }
        return result;
    }

    static String normalizeRunName(String value) {
        if (value == null) return "";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return "";
        Matcher matcher = HOST_TIMESTAMP_APP_PATTERN.matcher(trimmed);
        if (!matcher.matches()) return trimmed;
        String timestamp = matcher.group(2);
        String appName = matcher.group(3);
        if (appName == null || appName.trim().isEmpty()) return trimmed;
        return appName + "_" + timestamp;
    }

    private static String normalizeRunKey(String runId) {
        if (runId == null) return "";
        String trimmed = runId.trim();
        if (trimmed.endsWith("_")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        return trimmed;
    }

    private static Path resolveRunOutputDir(Path outputRoot, String runKey) {
        if (runKey == null || runKey.isEmpty()) return outputRoot;
        Path direct = outputRoot.resolve(runKey);
        if (Files.isDirectory(direct)) return direct;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputRoot)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                String name = path.getFileName().toString();
                if (name.equals(runKey) || name.startsWith(runKey) || name.contains(runKey)) {
                    return path;
                }
            }
        } catch (IOException ignored) {
        }
        return outputRoot.resolve(runKey);
    }

    private static String detectVerdictTitle(Path runOutputDir) {
        if (runOutputDir == null || !Files.isDirectory(runOutputDir)) return "UNKNOWN";
        Path llmComplete = runOutputDir.resolve("sequences_llm_complete");
        if (Files.isDirectory(llmComplete)) return "LLM_COMPLETE";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(runOutputDir, "sequences_*")) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    String suffix = path.getFileName().toString().substring("sequences_".length());
                    return suffix.toUpperCase(Locale.ROOT);
                }
            }
        } catch (IOException ignored) {
        }
        return "UNKNOWN";
    }

    private static String detectOracleVerdict(Path runOutputDir, String fallback) {
        if (runOutputDir == null || !Files.isDirectory(runOutputDir)) return fallback;
        Path reportsDir = runOutputDir.resolve("reports");
        if (!Files.isDirectory(reportsDir)) return fallback;
        Path latestReport = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(reportsDir, "*.html")) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) continue;
                if (latestReport == null || Files.getLastModifiedTime(path).compareTo(Files.getLastModifiedTime(latestReport)) > 0) {
                    latestReport = path;
                }
            }
        } catch (IOException ignored) {
            return fallback;
        }
        if (latestReport == null) return fallback;
        try {
            String html = new String(Files.readAllBytes(latestReport));
            String marker = "Test verdict for this sequence:";
            int idx = html.indexOf(marker);
            if (idx < 0) return fallback;
            int start = idx + marker.length();
            int end = html.indexOf("<", start);
            if (end < 0) end = Math.min(start + 300, html.length());
            String raw = html.substring(start, end).replace("&nbsp;", " ").trim();
            return raw.isEmpty() ? fallback : raw;
        } catch (IOException e) {
            return fallback;
        }
    }

    private static String serializeGoals(List<String> goals) {
        if (goals == null || goals.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String goal : goals) {
            if (goal == null) continue;
            String cleaned = normalizeGoalText(goal);
            if (cleaned.isEmpty()) continue;
            if (sb.length() > 0) sb.append("\n\n<<<GOAL_SEPARATOR>>>\n\n");
            sb.append(cleaned);
        }
        return sb.toString();
    }

    private static String normalizeGoalText(String text) {
        if (text == null) return "";
        String normalized = text.replace("\\r\\n", "\n").replace("\\n", "\n").replace("\\r", "\n");
        return normalized.trim();
    }

    private static String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString((text == null ? "" : text).getBytes());
    }

    private static String decodeBase64(String text) {
        if (text == null || text.isEmpty()) return "";
        try {
            return new String(Base64.getDecoder().decode(text));
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private static String extractJsonString(String json, String key, String fallback) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return fallback;
        int colon = json.indexOf(":", idx + pattern.length());
        if (colon < 0) return fallback;
        int firstQuote = json.indexOf("\"", colon + 1);
        if (firstQuote < 0) return fallback;
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote < 0) return fallback;
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static String extractJsonBool(String json, String key, String fallback) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return fallback;
        int colon = json.indexOf(":", idx + pattern.length());
        if (colon < 0) return fallback;
        String rest = json.substring(colon + 1).trim();
        if (rest.startsWith("true")) return "true";
        if (rest.startsWith("false")) return "false";
        return fallback;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
