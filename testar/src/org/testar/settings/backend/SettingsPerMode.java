package org.testar.settings.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsPerMode {
    public static Path JSON_PATH = BackendMain.SSE_FOLDER.resolve("settings-per-mode.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Optional<List<String>> getSettingsForMode(String mode) throws IOException {
        try (InputStream in = Files.newInputStream(JSON_PATH)) {
            JsonNode root = MAPPER.readTree(in);

            boolean modeFound = false;
            List<String> out = new ArrayList<>();

            for (Map.Entry<String, JsonNode> section : root.properties()) {
                JsonNode sectionNode = section.getValue();

                if (!modePresent(sectionNode.path("Mode"), mode))
                    continue;

                modeFound = true;

                JsonNode tags = sectionNode.path("tags");
                if (!tags.isObject())
                    continue;

                for (Map.Entry<String, JsonNode> tag : tags.properties()) {
                    out.add(tag.getKey());
                }
            }

            return modeFound ? Optional.of(out) : Optional.empty();
        }
    }

    private static boolean modePresent(JsonNode modeArray, String mode) {
        if (!modeArray.isArray())
            return false;
        for (JsonNode m : modeArray) {
            if (m.isTextual() && m.asText().equalsIgnoreCase(mode))
                return true;
        }
        return false;
    }
}
